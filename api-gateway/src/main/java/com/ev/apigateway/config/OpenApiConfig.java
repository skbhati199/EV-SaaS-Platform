package com.ev.apigateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

import java.util.List;

/**
 * OpenAPI Configuration for API Gateway
 * Provides centralized Swagger documentation for all microservices
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI evSaasOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server");

        Server productionServer = new Server()
                .url("https://api.evsaas.com")
                .description("Production Server");

        Contact contact = new Contact()
                .name("EV SaaS Platform Team")
                .email("support@evsaas.com")
                .url("https://www.evsaas.com/support");

        License license = new License()
                .name("Private License")
                .url("https://www.evsaas.com/license");

        Info info = new Info()
                .title("EV SaaS Platform API")
                .description("API Gateway for Electric Vehicle SaaS Platform - Provides access to all microservices")
                .version("1.0.0")
                .contact(contact)
                .license(license)
                .termsOfService("https://www.evsaas.com/terms");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer));
    }
    
    /**
     * Default OpenAPI Group for all APIs
     */
    @Bean
    public GroupedOpenApi allApisGroup() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .pathsToMatch("/**")
                .displayName("All APIs")
                .build();
    }
} 