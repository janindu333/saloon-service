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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Map;

@Component
@Order(1)
public class UserContextFilter extends OncePerRequestFilter {

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
                return null;
            }
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");
            if (data != null && data.isNumber()) {
                return data.longValue();
            }
            if (data != null && data.isTextual()) {
                return Long.parseLong(data.asText());
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    private String extractRole(Map<String, ?> claims) {
        String role = stringClaim(claims, "role");
        if (role != null) {
            return role;
        }
        Object realmAccessObj = claims.get("realm_access");
        if (!(realmAccessObj instanceof java.util.Map<?, ?> realmAccessMap)) {
            return null;
        }
        Object rolesObj = realmAccessMap.get("roles");
        if (!(rolesObj instanceof java.util.List<?> roles) || roles.isEmpty()) {
            return null;
        }
        for (Object roleObj : roles) {
            if (!(roleObj instanceof String r)) continue;
            if ("super_admin".equalsIgnoreCase(r) || "admin".equalsIgnoreCase(r) || "owner".equalsIgnoreCase(r)) {
                return r;
            }
        }
        Object first = roles.get(0);
        return first == null ? null : first.toString();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                if (token.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
                        userId = Long.parseLong(userIdStr);
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
                        username = userDetailsJson.isBlank() ? null : userDetailsJson;
                    }
                    if (username != null) {
                        UserContext.setUsername(username);
                    }
                } catch (JwtException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
