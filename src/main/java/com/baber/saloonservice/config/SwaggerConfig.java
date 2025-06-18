package com.baber.saloonservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI apiInfo() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Saloon Service Server");

        return new OpenAPI()
                .info(new Info()
                        .title("Saloon Service API")
                        .description("API Documentation for Saloon Service")
                        .version("1.0.0"))
                .servers(List.of(server));
    }
}
