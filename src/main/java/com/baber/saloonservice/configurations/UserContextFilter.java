package com.baber.saloonservice.configurations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class UserContextFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(UserContextFilter.class);

    /**
     * Base64-encoded HMAC key — must match identity-service / api-gateway ({@code Decoders.BASE64.decode} + HS256).
     */
    @Value("${jwt.secret:U2dWa1lwMzczNjc5NzkyRjQyRjQ1Mjg0ODJCNGRiNjI1MTY1NTQ2ODU3NmQ1YTcxNDc0Nw==}")
    private String jwtSecret;

    @Value("${identity.service.url:http://identity-service:8082}")
    private String identityServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Key signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * HS256: verify with shared secret (legacy identity-issued tokens).
     * RS256/ES256: decode payload only — Keycloak access tokens; rely on api-gateway (or TLS mesh) having validated the signature before traffic reaches this service.
     */
    private Map<String, Object> parseTokenClaims(String token) throws JwtException {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new JwtException("Malformed JWT");
        }
        String alg;
        try {
            byte[] headerBytes = Decoders.BASE64URL.decode(parts[0]);
            JsonNode header = objectMapper.readTree(headerBytes);
            JsonNode algNode = header.get("alg");
            alg = algNode != null && algNode.isTextual() ? algNode.asText() : "";
        } catch (IOException e) {
            throw new JwtException("Invalid JWT header", e);
        }

        if ("HS256".equals(alg)) {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return body;
        }

        if ("RS256".equals(alg) || "RS384".equals(alg) || "RS512".equals(alg)
                || "ES256".equals(alg) || "ES384".equals(alg) || "ES512".equals(alg)) {
            try {
                byte[] payloadBytes = Decoders.BASE64URL.decode(parts[1]);
                return objectMapper.readValue(payloadBytes, new TypeReference<Map<String, Object>>() {});
            } catch (IOException e) {
                throw new JwtException("Invalid JWT payload", e);
            }
        }

        throw new JwtException("Unsupported JWT algorithm: " + alg);
    }

    @Nullable
    private static String stringClaim(Map<String, ?> claims, String name) {
        Object v = claims.get(name);
        return v instanceof String s && !s.isBlank() ? s : null;
    }

    @Nullable
    private Long resolveUserIdFromIdentity(String authHeader) {
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    identityServiceUrl + "/auth/internal/me-id",
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("identity /auth/internal/me-id non-2xx or empty body: {}", response.getStatusCode());
                return null;
            }
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode successNode = root.get("success");
            if (successNode == null || !successNode.asBoolean(false)) {
                JsonNode msg = root.get("message");
                log.warn("identity /auth/internal/me-id success=false: {}", msg != null ? msg.asText() : response.getBody());
                return null;
            }
            JsonNode data = root.get("data");
            if (data != null && data.isNumber()) {
                return data.longValue();
            }
            if (data != null && data.isTextual()) {
                return Long.parseLong(data.asText());
            }
            return null;
        } catch (RestClientResponseException e) {
            log.warn("identity /auth/internal/me-id HTTP {}: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.warn("identity /auth/internal/me-id failed: {}", e.getMessage());
            return null;
        }
    }

    private static void addRolesFromAccess(Map<?, ?> accessMap, List<String> out) {
        if (accessMap == null) {
            return;
        }
        Object rolesObj = accessMap.get("roles");
        if (!(rolesObj instanceof Collection<?> roles)) {
            return;
        }
        for (Object roleObj : roles) {
            if (roleObj == null) {
                continue;
            }
            String r = roleObj.toString().trim();
            if (!r.isEmpty()) {
                out.add(r);
            }
        }
    }

    private static void collectKeycloakRoles(Map<String, ?> claims, List<String> out) {
        Object realmAccessObj = claims.get("realm_access");
        if (realmAccessObj instanceof Map<?, ?> realmAccess) {
            addRolesFromAccess(realmAccess, out);
        }
        Object resourceAccessObj = claims.get("resource_access");
        if (resourceAccessObj instanceof Map<?, ?> resourceAccess) {
            for (Object clientObj : resourceAccess.values()) {
                if (clientObj instanceof Map<?, ?> clientAccess) {
                    addRolesFromAccess(clientAccess, out);
                }
            }
        }
    }

    /** Custom / legacy tokens: top-level {@code roles} or Spring-style {@code authorities}. */
    private static void collectTopLevelRoleArrays(Map<String, ?> claims, List<String> out) {
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?> roles) {
            for (Object roleObj : roles) {
                if (roleObj == null) {
                    continue;
                }
                String r = roleObj.toString().trim();
                if (!r.isEmpty()) {
                    out.add(r);
                }
            }
        }
        Object authObj = claims.get("authorities");
        if (authObj instanceof Collection<?> auths) {
            for (Object a : auths) {
                if (a == null) {
                    continue;
                }
                String r = a.toString().trim();
                if (!r.isEmpty()) {
                    out.add(r);
                }
            }
        }
    }

    private static String stripRolePrefix(String raw) {
        if (raw == null) {
            return "";
        }
        String t = raw.trim();
        while (t.length() >= 5) {
            if (t.regionMatches(true, 0, "ROLE_", 0, 5)) {
                t = t.substring(5).trim();
            } else if (t.regionMatches(true, 0, "role_", 0, 5)) {
                t = t.substring(5).trim();
            } else {
                break;
            }
        }
        return t;
    }

    private static boolean roleMatchesPreferred(String raw, String preferred) {
        if (raw == null || preferred == null) {
            return false;
        }
        if (preferred.equalsIgnoreCase(raw)) {
            return true;
        }
        return preferred.equalsIgnoreCase(stripRolePrefix(raw));
    }

    @Nullable
    private String canonicalizeChosenRole(String raw) {
        if (raw == null) {
            return null;
        }
        String stripped = stripRolePrefix(raw);
        if ("admin".equalsIgnoreCase(stripped) || "administrator".equalsIgnoreCase(stripped)) {
            return "admin";
        }
        if ("super_admin".equalsIgnoreCase(stripped)) {
            return "super_admin";
        }
        if ("owner".equalsIgnoreCase(stripped)) {
            return "owner";
        }
        return raw.trim();
    }

    @Nullable
    private String extractRole(Map<String, ?> claims) {
        String direct = stringClaim(claims, "role");
        if (direct != null) {
            return canonicalizeChosenRole(direct);
        }
        List<String> roles = new ArrayList<>();
        collectKeycloakRoles(claims, roles);
        collectTopLevelRoleArrays(claims, roles);
        for (String preferred : List.of(
                "super_admin", "admin", "owner", "Administrator", "Manager", "Customer", "Staff", "Scheduler")) {
            for (String r : roles) {
                if (roleMatchesPreferred(r, preferred)) {
                    return canonicalizeChosenRole(r);
                }
            }
        }
        return roles.isEmpty() ? null : canonicalizeChosenRole(roles.get(0));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                if (token.isEmpty()) {
                    writeUnauthorized(response, "empty_bearer_token", "Bearer token is empty");
                    return;
                }
                try {
                    Map<String, Object> claims = parseTokenClaims(token);

                    String userDetailsJson = stringClaim(claims, "sub");
                    if (userDetailsJson == null) {
                        userDetailsJson = "";
                    }
                    UserContext.setUserDetailsJson(userDetailsJson);

                    String role = extractRole(claims);
                    if (role != null) {
                        UserContext.setRole(role);
                    }

                    Object userIdObj = claims.get("userId");
                    Long userId = userIdObj instanceof Number ? ((Number) userIdObj).longValue() : null;
                    if (userId == null && userIdObj instanceof String userIdStr && !userIdStr.isBlank()) {
                        try {
                            userId = Long.parseLong(userIdStr);
                        } catch (NumberFormatException nfe) {
                            log.warn("Invalid userId claim in JWT for {}: {}", request.getRequestURI(), userIdStr);
                            userId = null;
                        }
                    }
                    if (userId == null) {
                        userId = resolveUserIdFromIdentity(authHeader);
                    }
                    if (userId != null) {
                        UserContext.setUserId(userId);
                    }

                    String username = stringClaim(claims, "preferred_username");
                    if (username == null) {
                        username = stringClaim(claims, "email");
                    }
                    if (username == null) {
                        username = stringClaim(claims, "username");
                    }
                    if (username == null) {
                        username = userDetailsJson.isBlank() ? null : userDetailsJson;
                    }
                    if (username != null) {
                        UserContext.setUsername(username);
                    }
                } catch (JwtException e) {
                    log.warn("JWT rejected for {}: {}", request.getRequestURI(), e.getMessage());
                    writeUnauthorized(response, "invalid_token", e.getMessage());
                    return;
                } catch (Exception e) {
                    log.warn("Bearer processing failed for {}: {}", request.getRequestURI(), e.getMessage());
                    writeUnauthorized(response, "auth_processing_error", "Failed to process authentication");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("success", false);
        body.put("code", code);
        body.put("message", message != null ? message : "");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
