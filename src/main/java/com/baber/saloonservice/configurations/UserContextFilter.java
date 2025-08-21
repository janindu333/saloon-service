package com.baber.saloonservice.configurations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class UserContextFilter extends OncePerRequestFilter {
    private static final String SECRET_KEY = "U2dWa1lwMzczNjc5NzkyRjQyRjQ1Mjg0ODJCNGRiNjI1MTY1NTQ2ODU3NmQ1YTcxNDc0Nw=="; // Same as API Gateway

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = request.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                String userDetailsJson = claims.getSubject();
                UserContext.setUserDetailsJson(userDetailsJson);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } finally {
            UserContext.clear();
        }
    }
}
