package com.ev.auth.controller;

import com.ev.auth.dto.PasswordlessLoginRequest;
import com.ev.auth.dto.PasswordlessTokenRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.service.PasswordlessAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/passwordless")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Passwordless Authentication", description = "Passwordless Authentication APIs")
public class PasswordlessAuthController {

    private final PasswordlessAuthService passwordlessAuthService;

    @PostMapping("/request")
    @Operation(summary = "Request passwordless login", description = "Initiates a passwordless login flow")
    @ApiResponse(responseCode = "200", description = "Passwordless login request initiated")
    @ApiResponse(responseCode = "400", description = "Invalid email")
    public ResponseEntity<String> requestPasswordlessLogin(@Valid @RequestBody PasswordlessLoginRequest request) {
        log.info("Passwordless login request for email: {}", request.getEmail());
        
        String token = passwordlessAuthService.generatePasswordlessRequest(request.getEmail());
        
        // In a real implementation, send the token via email, SMS, etc.
        log.info("Passwordless login token generated for email: {}", request.getEmail());
        
        return ResponseEntity.ok("Passwordless login request initiated");
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate with passwordless token", description = "Authenticates a user with a passwordless token")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    public ResponseEntity<TokenResponse> authenticateWithToken(@Valid @RequestBody PasswordlessTokenRequest request) {
        log.info("Authenticating with passwordless token");
        
        TokenResponse tokens = passwordlessAuthService.authenticateWithToken(request.getToken());
        
        log.info("Passwordless authentication successful");
        
        return ResponseEntity.ok(tokens);
    }
} 