package com.ev.schedulerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * OpenAPI Configuration for Scheduler Service
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8089}")
    private String serverPort;

    @Bean
    public OpenAPI schedulerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scheduler Service API")
                        .description("Scheduler Service for EV SaaS Platform - Handles scheduled tasks, recurring jobs, and time-based operations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EV SaaS Platform Team")
                                .email("support@evsaas.com")
                                .url("https://www.evsaas.com/support"))
                        .license(new License()
                                .name("Private License")
                                .url("https://www.evsaas.com/license"))
                        .termsOfService("https://www.evsaas.com/terms"))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:" + serverPort).description("Local Development Server"),
                        new Server().url("https://api.evsaas.com").description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
} 