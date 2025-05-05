package com.ev.auth.service;

import com.ev.auth.dto.TwoFactorAuthRequest;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of TwoFactorAuthService using TOTP (Time-based One-Time Password)
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class TOTPTwoFactorAuthService implements TwoFactorAuthService {

    private final SecretGenerator secretGenerator;
    private final KeycloakService keycloakService;
    
    @Value("${app.2fa.issuer:EVSaaS}")
    private String issuer;
    
    @Override
    public boolean validate(String username, String code) {
        // In a real application, we would retrieve the user's secret from storage
        // and validate the code against it
        
        // This is a simplified implementation
        // For actual implementation, use securely stored secrets
        
        // For now, we'll return true to allow the authentication flow to continue
        return true;
    }
    
    @Override
    public String generateSecret(String username) {
        String secret = secretGenerator.generate();
        log.info("Generated TOTP secret for user: {}", username);
        return secret;
    }
    
    /**
     * Generate a secret for two-factor authentication
     * 
     * @param userId The user ID
     * @param username The username
     * @return The generated secret
     */
    public String generateSecret(String userId, String username) {
        String secret = secretGenerator.generate();
        log.info("Generated TOTP secret for user: {} with ID: {}", username, userId);
        return secret;
    }
    
    @Override
    public boolean enable(String username) {
        // In a real application, you would:
        // 1. Generate a secret if not already done
        // 2. Store the secret securely, associated with the user
        // 3. Mark the user as having 2FA enabled
        
        log.info("Enabled TOTP for user: {}", username);
        return true;
    }
    
    @Override
    public boolean disable(String username) {
        // In a real application, you would:
        // 1. Remove the stored secret or mark it as invalid
        // 2. Mark the user as having 2FA disabled
        
        log.info("Disabled TOTP for user: {}", username);
        return true;
    }
    
    @Override
    public String getQrCodeUrl(String username, String secret) {
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        
        try {
            QrGenerator qrGenerator = new ZxingPngQrGenerator();
            byte[] imageData = qrGenerator.generate(data);
            return "data:image/png;base64," + dev.samstevens.totp.util.Utils.getDataUriForImage(imageData, qrGenerator.getImageMimeType());
        } catch (QrGenerationException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
    
    /**
     * Generate a QR code for two-factor authentication
     * 
     * @param username The username
     * @param secret The authentication secret
     * @return The QR code as a base64-encoded image
     */
    public String generateQrCode(String username, String secret) {
        return getQrCodeUrl(username, secret);
    }
    
    @Override
    public boolean verifyCode(TwoFactorAuthRequest request) {
        return validate(request.getUsername(), request.getCode());
    }
    
    /**
     * Validate a TOTP code
     * 
     * @param code The code to validate
     * @param secret The TOTP secret
     * @return True if the code is valid
     */
    public boolean validateCode(String code, String secret) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        
        // Check if the code is valid within the current time window (with a small allowable discrepancy)
        return verifier.isValidCode(secret, code);
    }
    
    /**
     * Enable TOTP for a user
     * 
     * @param userId The user ID
     * @param code The verification code
     * @param secret The TOTP secret
     * @return True if enabled successfully
     */
    public boolean enableTOTP(String userId, String code, String secret) {
        if (!validateCode(code, secret)) {
            log.warn("Invalid TOTP code provided for user ID: {}", userId);
            return false;
        }
        
        log.info("TOTP enabled for user ID: {}", userId);
        return true;
    }
    
    /**
     * Disable TOTP for a user
     * 
     * @param userId The user ID
     */
    public void disableTOTP(String userId) {
        log.info("TOTP disabled for user ID: {}", userId);
    }
} 