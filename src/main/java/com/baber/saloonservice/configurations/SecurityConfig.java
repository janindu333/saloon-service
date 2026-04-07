package com.baber.saloonservice.configurations; 

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/api/saloon/**" // Allow all - authentication handled by UserContextFilter and controller
                        ).permitAll()
                        .anyRequest().permitAll()
                );
        // Note: Authentication is handled by UserContextFilter (JWT validation)
        // and role-based authorization is enforced in the controller
        return http.build();
    }
}
