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
                                .email("support@evsaas.com")
                                .url("https://www.evsaas.com/support"))
                        .license(new License()
                                .name("Private License")
                                .url("https://www.evsaas.com/license"))
                        .termsOfService("https://www.evsaas.com/terms"))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:" + serverPort).description("Local Direct Access"),
                        new Server().url("http://localhost:8080/api/auth").description("Local API Gateway"),
                        new Server().url("https://api.evsaas.com/api/auth").description("Production Server")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("auth-service-public")
                .pathsToMatch("/api/v1/auth/**")
                .displayName("Auth Service API")
                .build();
    }
} 