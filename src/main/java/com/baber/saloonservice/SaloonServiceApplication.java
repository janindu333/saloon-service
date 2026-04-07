package com.baber.saloonservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableCaching
public class SaloonServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaloonServiceApplication.class, args);
    }
}