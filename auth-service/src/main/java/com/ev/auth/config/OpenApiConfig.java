package com.ev.auth.config;

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
import org.springdoc.core.models.GroupedOpenApi;

import java.util.Arrays;
import java.util.List;

/**
 * OpenAPI Configuration for Auth Service
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Authentication and Authorization Service for EV SaaS Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EV SaaS Platform Team")
                                .email("support@nbevc.com")
                                .url("https://www.nbevc.com/support"))
                        .license(new License()
                                .name("Private License")
                                .url("https://www.nbevc.com/license"))
                        .termsOfService("https://www.nbevc.com/terms"))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:" + serverPort).description("Local Direct Access"),
                        new Server().url("http://localhost:8080/api/auth").description("Local API Gateway"),
                        new Server().url("https://api.nbevc.com/api/auth").description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
    
    /**
     * Make sure the group ID matches the one defined in API Gateway
     */
    @Bean
    public GroupedOpenApi authServiceApi() {
        return GroupedOpenApi.builder()
                .group("auth-service")
                .packagesToScan("com.ev.auth.controller")
                .pathsToMatch("/auth/**", "/oauth/**", "/api/v1/**")
                .displayName("Auth Service API")
                .build();
    }
} 