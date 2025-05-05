package com.ev.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.ev.auth"})
@OpenAPIDefinition(
    info = @Info(
        title = "Auth Service API",
        version = "1.0.0",
        description = "Authentication and Authorization Service for EV SaaS Platform"
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Local Development Server"),
        @Server(url = "http://auth-service:8081", description = "Docker Container Server")
    }
)
@SpringBootApplication(scanBasePackages = "com.ev")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
