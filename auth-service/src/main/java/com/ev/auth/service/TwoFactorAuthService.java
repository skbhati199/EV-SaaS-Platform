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
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorAuthService {

    private final SecretGenerator secretGenerator;
    private final KeycloakService keycloakService;
    
    @Value("${app.2fa.issuer}")
    private String issuer;
    
    @Value("${app.2fa.qrcode-size}")
    private int qrCodeSize;
    
    /**
     * Generate a TOTP secret for a user
     * 
     * @param userId The user ID
     * @param username The username
     * @return The generated secret
     */
    public String generateSecret(String userId, String username) {
        // Generate a new TOTP secret
        String secret = secretGenerator.generate();
        
        // Save the secret to the user's attributes in Keycloak
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("totpSecret", List.of(secret));
        attributes.put("totpEnabled", List.of("false"));
        
        keycloakService.updateUser(userId, null, null, null, attributes);
        
        log.info("Generated TOTP secret for user: {}", username);
        return secret;
    }
    
    /**
     * Generate a QR code for a TOTP secret
     * 
     * @param username The username
     * @param secret The TOTP secret
     * @return The QR code as a base64-encoded PNG image
     */
    public String generateQrCode(String username, String secret) {
        QrData data = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        
        QrGenerator qrGenerator = new ZxingPngQrGenerator();
        byte[] imageData;
        
        try {
            imageData = qrGenerator.generate(data, qrCodeSize);
        } catch (QrGenerationException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
        
        return "data:image/png;base64," + dev.samstevens.totp.util.Utils.getDataUriForImage(imageData, qrGenerator.getImageMimeType());
    }
    
    /**
     * Enable TOTP for a user
     * 
     * @param userId The user ID
     * @param code The verification code
     * @param secret The TOTP secret
     * @return True if the code is valid and TOTP was enabled
     */
    public boolean enableTOTP(String userId, String code, String secret) {
        // Verify the code
        if (!validateCode(code, secret)) {
            log.warn("Invalid TOTP code provided for user ID: {}", userId);
            return false;
        }
        
        // Update the user's attributes in Keycloak
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("totpEnabled", List.of("true"));
        
        keycloakService.updateUser(userId, null, null, null, attributes);
        
        log.info("TOTP enabled for user ID: {}", userId);
        return true;
    }
    
    /**
     * Disable TOTP for a user
     * 
     * @param userId The user ID
     */
    public void disableTOTP(String userId) {
        // Update the user's attributes in Keycloak
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("totpSecret", List.of());
        attributes.put("totpEnabled", List.of("false"));
        
        keycloakService.updateUser(userId, null, null, null, attributes);
        
        log.info("TOTP disabled for user ID: {}", userId);
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
} 