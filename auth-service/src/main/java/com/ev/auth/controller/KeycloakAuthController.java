package com.ev.auth.controller;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.LoginWithUsernameRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Legacy auth controller using direct Keycloak integration
 * @deprecated Use AuthController instead
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication APIs using Keycloak")
@Profile("legacy") // Only activate in legacy profile
@Deprecated
public class KeycloakAuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user in Keycloak")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());
        
        String userId = keycloakService.createUser(request);
        log.info("User registered successfully with ID: {}", userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginWithUsernameRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        
        TokenResponse tokenResponse = keycloakService.getTokens(request.getUsername(), request.getPassword());
        log.info("User logged in: {}", request.getUsername());
        
        return ResponseEntity.ok(tokenResponse);
    }
    
    @PostMapping("/email-login")
    @Operation(summary = "Login with email and password", description = "Authenticates a user with email and returns JWT tokens")
    public ResponseEntity<TokenResponse> loginWithEmail(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt with email: {}", request.getEmail());
        
        // In a real implementation, you might need to look up the username by email
        // For simplicity, we're using the email as the username here
        TokenResponse tokenResponse = keycloakService.getTokens(request.getEmail(), request.getPassword());
        log.info("User logged in with email: {}", request.getEmail());
        
        return ResponseEntity.ok(tokenResponse);
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