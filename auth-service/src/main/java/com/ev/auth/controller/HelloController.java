package com.ev.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Hello", description = "Endpoints for testing Swagger integration")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Say Hello", description = "This endpoint returns a simple greeting.")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from Docker!");
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "This endpoint returns the health status of the service.")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is healthy!");
    }
}
