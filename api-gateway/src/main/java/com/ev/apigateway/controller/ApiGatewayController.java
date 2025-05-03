package com.ev.apigateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * API Gateway Controller
 * Provides basic API endpoints for the gateway itself
 */
@RestController
@RequestMapping("/api-gateway")
@Tag(name = "API Gateway", description = "Operations related to API Gateway status and information")
public class ApiGatewayController {

    /**
     * Health check endpoint
     * @return Gateway status information
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health Check",
        description = "Returns the current status of the API Gateway",
        tags = {"Health"}
    )
    public Mono<ResponseEntity<Map<String, Object>>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "API Gateway");
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.ok(response));
    }

    /**
     * Fallback endpoint for service unavailable
     * @return Service unavailable response
     */
    @GetMapping("/fallback")
    @Operation(
        summary = "Service Fallback",
        description = "Fallback endpoint when a service is unavailable",
        tags = {"Fallback"}
    )
    public Mono<ResponseEntity<Map<String, Object>>> fallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "The requested service is currently unavailable. Please try again later.");
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.status(503).body(response));
    }
} 