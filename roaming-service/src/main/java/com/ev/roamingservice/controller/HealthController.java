package com.ev.roamingservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple controller for checking the health of the service
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Return a simple health check response
     */
    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "roaming-service");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
} 