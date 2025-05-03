package com.ev.apigateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * API Documentation Controller
 * Provides a consolidated view of all microservice APIs
 */
@RestController
@RequestMapping("/api-docs")
@Tag(name = "API Documentation", description = "Operations related to API documentation")
public class ApiDocsController {

    @Value("classpath:openapi-services.yml")
    private Resource servicesYaml;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Get a list of all services with API documentation
     * @return List of all services and their API documentation URLs
     */
    @GetMapping("/services")
    @Operation(
        summary = "API Services",
        description = "Lists all microservices with API documentation",
        tags = {"API Documentation"}
    )
    public Mono<ResponseEntity<Map<String, Object>>> getServices() {
        try {
            Map<String, Object> servicesMap = yamlMapper.readValue(servicesYaml.getInputStream(), Map.class);
            return Mono.just(ResponseEntity.ok(servicesMap));
        } catch (IOException e) {
            return Mono.just(ResponseEntity
                    .status(500)
                    .body(Map.of(
                            "error", "Failed to load services documentation",
                            "message", e.getMessage()
                    )));
        }
    }

    /**
     * Data classes for representing services information
     */
    @Data
    public static class ServiceInfo {
        private String name;
        private String description;
        private String baseUrl;
        private String apiDocsUrl;
        private String version;
        private List<EndpointInfo> endpoints;
    }

    @Data
    public static class EndpointInfo {
        private String path;
        private String method;
        private String description;
    }
} 