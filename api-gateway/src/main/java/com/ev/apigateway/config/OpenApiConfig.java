package com.ev.apigateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springdoc.core.models.GroupedOpenApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * OpenAPI Configuration for API Gateway
 * Provides centralized Swagger documentation for all microservices
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;
    
    private final Environment environment;
    
    public OpenApiConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public OpenAPI evSaasOpenAPI() {
        List<Server> servers = new ArrayList<>();
        
        // Check if Docker profile is active
        boolean isDockerProfile = Arrays.asList(environment.getActiveProfiles()).contains("docker");
        
        if (isDockerProfile) {
            // Docker environment servers
            servers.add(new Server()
                    .url("http://api-gateway:8080")
                    .description("Docker API Gateway"));
            
            servers.add(new Server()
                    .url("http://localhost:8080")
                    .description("Local Docker Access"));
        } else {
            // Standard environment servers
            servers.add(new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development Server"));
                
            servers.add(new Server()
                    .url("https://api.evsaas.com")
                    .description("Production Server"));
        }

        Contact contact = new Contact()
                .name("EV SaaS Platform Team")
                .email("support@nbevc.com")
                .url("https://www.nbevc.com/support");

        License license = new License()
                .name("Private License")
                .url("https://www.nbevc.com/license");

        Info info = new Info()
                .title("EV SaaS Platform API")
                .description("API Gateway for Electric Vehicle SaaS Platform - Provides access to all microservices")
                .version("1.0.0")
                .contact(contact)
                .license(license)
                .termsOfService("https://www.nbevc.com/terms");

        return new OpenAPI()
                .info(info)
                .servers(servers);
    }
    
    /**
     * API Gateway specific endpoints
     */
    @Bean
    public GroupedOpenApi apiGatewayGroup() {
        return GroupedOpenApi.builder()
                .group("api-gateway")
                .pathsToMatch("/api-gateway/**")
                .displayName("API Gateway")
                .build();
    }

    /**
     * Auth Service endpoints
     */
    @Bean
    public GroupedOpenApi authServiceGroup() {
        return GroupedOpenApi.builder()
                .group("auth-service")
                .pathsToMatch("/api/auth/**")
                .displayName("Auth Service")
                .build();
    }

    /**
     * User Service endpoints
     */
    @Bean
    public GroupedOpenApi userServiceGroup() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .pathsToMatch("/api/users/**")
                .displayName("User Service")
                .build();
    }

    /**
     * Station Service endpoints
     */
    @Bean
    public GroupedOpenApi stationServiceGroup() {
        return GroupedOpenApi.builder()
                .group("station-service")
                .pathsToMatch("/api/stations/**")
                .displayName("Station Service")
                .build();
    }

    /**
     * Roaming Service endpoints (both internal and OCPI)
     */
    @Bean
    public GroupedOpenApi roamingServiceGroup() {
        return GroupedOpenApi.builder()
                .group("roaming-service")
                .pathsToMatch("/api/roaming/**", "/ocpi/**")
                .displayName("Roaming Service")
                .build();
    }

    /**
     * Billing Service endpoints
     */
    @Bean
    public GroupedOpenApi billingServiceGroup() {
        return GroupedOpenApi.builder()
                .group("billing-service")
                .pathsToMatch("/api/billing/**")
                .displayName("Billing Service")
                .build();
    }

    /**
     * Smart Charging Service endpoints
     */
    @Bean
    public GroupedOpenApi smartChargingServiceGroup() {
        return GroupedOpenApi.builder()
                .group("smart-charging")
                .pathsToMatch("/api/smart-charging/**")
                .displayName("Smart Charging Service")
                .build();
    }

    /**
     * Notification Service endpoints
     */
    @Bean
    public GroupedOpenApi notificationServiceGroup() {
        return GroupedOpenApi.builder()
                .group("notification-service")
                .pathsToMatch("/api/notifications/**")
                .displayName("Notification Service")
                .build();
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