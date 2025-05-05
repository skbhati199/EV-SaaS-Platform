package com.ev.auth.controller;

import com.ev.auth.dto.TwoFactorAuthRequest;
import com.ev.auth.dto.TwoFactorEnableRequest;
import com.ev.auth.dto.TwoFactorQrCodeResponse;
import com.ev.auth.dto.TwoFactorVerifyRequest;
import com.ev.auth.service.TOTPTwoFactorAuthService;
import com.ev.auth.service.TwoFactorAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth/2fa")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Two-Factor Authentication", description = "Two-Factor Authentication APIs")
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final TOTPTwoFactorAuthService totpService;

    @PostMapping("/setup")
    @Operation(summary = "Set up 2FA", description = "Generates a TOTP secret and QR code for 2FA setup")
    @ApiResponse(responseCode = "200", description = "Setup information generated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TwoFactorQrCodeResponse> setup(Principal principal) {
        String username = principal.getName();
        String userId = extractUserIdFromAuth();
        
        log.info("Setting up 2FA for user: {}", username);
        
        // Generate a secret - using the standard interface method
        String secret = twoFactorAuthService.generateSecret(username);
        
        // Generate a QR code - using the implementation-specific method
        String qrCodeImage = totpService.generateQrCode(username, secret);
        
        TwoFactorQrCodeResponse response = new TwoFactorQrCodeResponse(secret, qrCodeImage);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/enable")
    @Operation(summary = "Enable 2FA", description = "Verifies the code and enables 2FA for the user")
    @ApiResponse(responseCode = "200", description = "2FA enabled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid code")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Boolean> enable(@Valid @RequestBody TwoFactorEnableRequest request, Principal principal) {
        String username = principal.getName();
        String userId = extractUserIdFromAuth();
        
        log.info("Enabling 2FA for user: {}", username);
        
        // Validate the code first
        boolean codeValid = totpService.validateCode(request.getCode(), request.getSecret());
        if (!codeValid) {
            log.warn("Invalid 2FA code provided for user: {}", username);
            return ResponseEntity.badRequest().body(false);
        }
        
        // Enable 2FA for the user
        boolean enabled = twoFactorAuthService.enable(username);
        
        if (enabled) {
            log.info("2FA enabled successfully for user: {}", username);
            return ResponseEntity.ok(true);
        } else {
            log.warn("Failed to enable 2FA for user: {}", username);
            return ResponseEntity.badRequest().body(false);
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify 2FA code", description = "Verifies a TOTP code")
    @ApiResponse(responseCode = "200", description = "Code verified successfully")
    @ApiResponse(responseCode = "400", description = "Invalid code")
    public ResponseEntity<Boolean> verify(@Valid @RequestBody TwoFactorVerifyRequest request) {
        log.info("Verifying 2FA code for user: {}", request.getUsername());
        
        // Create a new request object with the appropriate data
        TwoFactorAuthRequest authRequest = TwoFactorAuthRequest.builder()
                .username(request.getUsername())
                .code(request.getCode())
                .secret(request.getSecret())
                .build();
                
        boolean valid = twoFactorAuthService.verifyCode(authRequest);
        
        if (valid) {
            log.info("2FA code verified successfully for user: {}", request.getUsername());
            return ResponseEntity.ok(true);
        } else {
            log.warn("Invalid 2FA code for user: {}", request.getUsername());
            return ResponseEntity.badRequest().body(false);
        }
    }

    @PostMapping("/disable")
    @Operation(summary = "Disable 2FA", description = "Disables 2FA for the user")
    @ApiResponse(responseCode = "200", description = "2FA disabled successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> disable(Principal principal) {
        String username = principal.getName();
        
        log.info("Disabling 2FA for user: {}", username);
        
        boolean disabled = twoFactorAuthService.disable(username);
        
        if (disabled) {
            log.info("2FA disabled successfully for user: {}", username);
            return ResponseEntity.ok().build();
        } else {
            log.warn("Failed to disable 2FA for user: {}", username);
            return ResponseEntity.badRequest().build();
        }
    }
    
    private String extractUserIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();
            return jwt.getSubject(); // The subject claim in the JWT should be the user ID
        }
        
        throw new RuntimeException("User ID not found in security context");
    }
} 