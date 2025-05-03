package com.ev.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller for Circuit Breaker responses
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * General fallback method
     * @return Error response
     */
    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        Map<String, Object> response = createErrorResponse(
                "Service Unavailable", 
                "The service is currently unavailable. Please try again later.");
        return Mono.just(ResponseEntity.status(503).body(response));
    }
    
    /**
     * OCPI specific fallback
     * @return OCPI-formatted error response
     */
    @GetMapping("/ocpi")
    public Mono<ResponseEntity<Map<String, Object>>> ocpiFallback() {
        Map<String, Object> ocpiResponse = new HashMap<>();
        ocpiResponse.put("status_code", 2000); // OCPI Server Error
        ocpiResponse.put("status_message", "Service temporarily unavailable");
        ocpiResponse.put("timestamp", System.currentTimeMillis());
        return Mono.just(ResponseEntity.status(503).body(ocpiResponse));
    }
    
    /**
     * Auth service fallback
     * @return Error response
     */
    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        Map<String, Object> response = createErrorResponse(
                "Authentication Service Unavailable", 
                "The authentication service is currently unavailable. Please try again later.");
        return Mono.just(ResponseEntity.status(503).body(response));
    }
    
    /**
     * Create a standard error response
     * @param title Error title
     * @param message Error message
     * @return Map of error response
     */
    private Map<String, Object> createErrorResponse(String title, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", title);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
} 