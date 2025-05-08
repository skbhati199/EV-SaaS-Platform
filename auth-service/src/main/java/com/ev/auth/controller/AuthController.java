package com.ev.auth.controller;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.LoginResponse;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;
import com.ev.auth.exception.AuthenticationException;
import com.ev.auth.exception.TokenRefreshException;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
import com.ev.auth.service.AuthService;
import com.ev.auth.service.JwtService;
import com.ev.auth.service.RefreshTokenService;
import com.ev.auth.service.UserService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and Authorization API")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    
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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());
        
        // Get tokens
        TokenResponse tokenResponse = authService.login(request.getEmail(), request.getPassword());
        
        // Get user info
        UUID userId = authService.getUserIdFromToken(tokenResponse.getAccessToken());
        UserResponse userResponse = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found after login"));
        
        // Combine into login response
        LoginResponse response = new LoginResponse(userResponse, tokenResponse.getAccessToken(), 
            tokenResponse.getRefreshToken(), false);
        
        return ResponseEntity.ok(response);
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
            @Parameter(description = "Refresh token", required = true, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestParam String refreshToken) {
        log.info("Token refresh request received");
        
        try {
            // Validate the refresh token and get user ID
            String userId = refreshTokenService.validateRefreshToken(refreshToken);
            
            // Find user in database
            User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "User not found"));
            
            // Generate new tokens
            String accessToken = jwtService.generateToken(user);
            
            // Optionally create a new refresh token and revoke the old one for added security
            // Comment out these lines if you want to keep using the same refresh token
            String newRefreshToken = refreshTokenService.createRefreshToken(userId);
            refreshTokenService.revokeRefreshToken(refreshToken);
            
            log.info("Tokens refreshed successfully for user: {}", user.getEmail());
            
            return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(3600) // 1 hour in seconds
                .build());
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new TokenRefreshException(refreshToken, "Failed to refresh token: " + e.getMessage());
        }
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
            @Parameter(description = "JWT token to validate", required = true, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestParam String token) {
        log.info("Token validation request received");
        
        // Validate token using jwtService
        boolean isValid = false;
        
        try {
            // Extract user ID from token
            UUID userId = jwtService.extractUserId(token);
            
            // Verify token is not expired
            isValid = !jwtService.isTokenExpired(token);
            
            log.info("Token validation result: {}", isValid);
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(isValid);
    }
    
    @PostMapping("/logout")
    @Operation(
        summary = "Logout user",
        description = "Revokes the user's refresh token",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logged out successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<Void> logout(
            @Parameter(description = "Refresh token", required = true, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestParam String refreshToken) {
        log.info("Logout request received");
        
        try {
            refreshTokenService.revokeRefreshToken(refreshToken);
            log.info("User logged out successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
