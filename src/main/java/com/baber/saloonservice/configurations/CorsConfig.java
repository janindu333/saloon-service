package com.baber.saloonservice.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration
 * 
 * CORS is handled by API Gateway, so we disable it here to avoid duplicate headers.
 * If you need to access this service directly (not through gateway), uncomment the method below.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // CORS is handled by API Gateway, so we disable it here to avoid duplicate headers
    // If you need to access this service directly (not through gateway), uncomment below:
    /*
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
    */
}