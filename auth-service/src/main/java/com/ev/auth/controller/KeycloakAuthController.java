package com.ev.auth.controller;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication APIs using Keycloak")
public class KeycloakAuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user in Keycloak")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());
        String userId = keycloakService.createUser(request);
        log.info("User registered successfully with ID: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());
        TokenResponse tokens = keycloakService.getTokens(request.getUsername(), request.getPassword());
        log.info("User login successful: {}", request.getUsername());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token", description = "Get a new access token using a refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody String refreshToken) {
        log.info("Token refresh attempt");
        TokenResponse tokens = keycloakService.refreshToken(refreshToken);
        log.info("Token refreshed successfully");
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validates if a JWT token is valid")
    @ApiResponse(responseCode = "200", description = "Token is valid")
    @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        log.info("Token validation attempt");
        boolean isValid = keycloakService.validateToken(token);
        log.info("Token validation result: {}", isValid);
        return ResponseEntity.ok(isValid);
    }
} 