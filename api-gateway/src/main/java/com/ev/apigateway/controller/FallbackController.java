package com.ev.apigateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller
 * Provides fallback endpoints for circuit breaker
 */
@RestController
@RequestMapping("/api-gateway/service-fallback")
@Tag(name = "Fallback", description = "Fallback endpoints for unavailable services")
public class FallbackController {

    /**
     * General fallback endpoint
     * @return Service unavailable response
     */
    @GetMapping
    @Operation(
        summary = "General Fallback",
        description = "Fallback endpoint when a service is unavailable",
        tags = {"Fallback"}
    )
    public Mono<ResponseEntity<Map<String, Object>>> fallback() {
        return getFallbackResponse("The requested service is currently unavailable. Please try again later.");
    }

    /**
     * Auth service fallback endpoint
     */
    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return getFallbackResponse("Authentication service is currently unavailable. Please try again later.");
    }

    /**
     * User service fallback endpoint
     */
    @GetMapping("/users")
    public Mono<ResponseEntity<Map<String, Object>>> userFallback() {
        return getFallbackResponse("User service is currently unavailable. Please try again later.");
    }

    /**
     * Station service fallback endpoint
     */
    @GetMapping("/stations")
    public Mono<ResponseEntity<Map<String, Object>>> stationFallback() {
        return getFallbackResponse("Station service is currently unavailable. Please try again later.");
    }

    /**
     * Billing service fallback endpoint
     */
    @GetMapping("/billing")
    public Mono<ResponseEntity<Map<String, Object>>> billingFallback() {
        return getFallbackResponse("Billing service is currently unavailable. Please try again later.");
    }

    /**
     * Roaming service fallback endpoint
     */
    @GetMapping("/roaming")
    public Mono<ResponseEntity<Map<String, Object>>> roamingFallback() {
        return getFallbackResponse("Roaming service is currently unavailable. Please try again later.");
    }

    /**
     * Smart charging service fallback endpoint
     */
    @GetMapping("/smart-charging")
    public Mono<ResponseEntity<Map<String, Object>>> smartChargingFallback() {
        return getFallbackResponse("Smart charging service is currently unavailable. Please try again later.");
    }

    /**
     * Notification service fallback endpoint
     */
    @GetMapping("/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationFallback() {
        return getFallbackResponse("Notification service is currently unavailable. Please try again later.");
    }

    /**
     * OCPI fallback endpoint
     */
    @GetMapping("/ocpi")
    public Mono<ResponseEntity<Map<String, Object>>> ocpiFallback() {
        return getFallbackResponse("OCPI service is currently unavailable. Please try again later.");
    }

    /**
     * Helper method to create a standardized fallback response
     */
    private Mono<ResponseEntity<Map<String, Object>>> getFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        response.put("path", "n/a");
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
} 