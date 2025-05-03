package com.ev.auth.controller;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;
import com.ev.auth.service.AuthService;
import com.ev.auth.service.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Authentication", description = "Authentication and Authorization API")
public class AuthController {

    private final AuthService authService;
    private final KeycloakService keycloakService;
    
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account in the system",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully", 
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for user: {}", request.getEmail());
        UserResponse response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user and returns JWT tokens",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful", 
                content = @Content(schema = @Schema(implementation = TokenResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account locked or disabled")
    })
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());
        TokenResponse tokenResponse = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }
    
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Issues a new access token using refresh token",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully", 
                content = @Content(schema = @Schema(implementation = TokenResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<TokenResponse> refreshToken(
            @Parameter(description = "Refresh token", required = true)
            @RequestParam String refreshToken) {
        log.info("Token refresh request received");
        TokenResponse tokenResponse = keycloakService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
    
    @GetMapping("/validate")
    @Operation(
        summary = "Validate token",
        description = "Validates whether a JWT token is valid and not expired",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token validation result", 
                content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> validateToken(
            @Parameter(description = "JWT token to validate", required = true)
            @RequestParam String token) {
        log.info("Token validation request received");
        boolean isValid = keycloakService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}
