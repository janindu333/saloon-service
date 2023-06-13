package com.baber.saloonservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SaloonServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaloonServiceApplication.class, args);
    }

}
