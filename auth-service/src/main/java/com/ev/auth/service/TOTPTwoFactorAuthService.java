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
    private final CodeGenerator codeGenerator;
    private final CodeVerifier codeVerifier;
    private final QrGenerator qrGenerator;
    
    @Value("${app.2fa.issuer:EVSaaS}")
    private String issuer;
    
    @Override
    public boolean validate(String username, String code) {
        // Implement using in-memory storage or database for development
        // In a production environment, implement secure storage for secrets
        Map<String, String> userSecrets = getUserSecrets();
        String secret = userSecrets.get(username);
        
        if (secret == null) {
            log.warn("No secret found for user: {}", username);
            return false;
        }
        
        return validateCode(code, secret);
    }
    
    /**
     * Validate a TOTP code against a secret
     * 
     * @param code The code to validate
     * @param secret The secret
     * @return True if valid
     */
    public boolean validateCode(String code, String secret) {
        return codeVerifier.isValidCode(secret, code);
    }
    
    @Override
    public String generateSecret(String username) {
        String secret = secretGenerator.generate();
        log.info("Generated TOTP secret for user: {}", username);
        
        // Store the secret in memory for development
        // In a production environment, implement secure storage for secrets
        Map<String, String> userSecrets = getUserSecrets();
        userSecrets.put(username, secret);
        
        return secret;
    }
    
    /**
     * Generate a QR code for TOTP
     * 
     * @param username The username
     * @param secret The secret
     * @return The QR code as a data URI
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
        
        try {
            byte[] imageData = qrGenerator.generate(data);
            String mimeType = qrGenerator instanceof ZxingPngQrGenerator ? "image/png" : "image/jpeg";
            String qrCodeImage = getDataUriForImage(imageData, mimeType);
            
            log.info("Generated QR code for user: {}", username);
            return qrCodeImage;
        } catch (QrGenerationException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Error generating QR code", e);
        }
    }
    
    @Override
    public boolean enable(String username) {
        // In a real implementation, mark the user as having 2FA enabled
        // For now, we'll return true to simulate success
        log.info("Enabled TOTP for user: {}", username);
        return true;
    }
    
    @Override
    public boolean disable(String username) {
        // In a real implementation, mark the user as having 2FA disabled
        // For now, we'll return true to simulate success
        log.info("Disabled TOTP for user: {}", username);
        return true;
    }
    
    @Override
    public boolean verifyCode(TwoFactorAuthRequest request) {
        return validateCode(request.getCode(), request.getSecret());
    }
    
    /**
     * Get a data URI for an image
     * 
     * @param imageData The image data
     * @param mimeType The MIME type
     * @return The data URI
     */
    private String getDataUriForImage(byte[] imageData, String mimeType) {
        String encoded = java.util.Base64.getEncoder().encodeToString(imageData);
        return "data:" + mimeType + ";base64," + encoded;
    }
    
    /**
     * Get user secrets
     * This is a simple in-memory implementation for development
     * In a production environment, implement secure storage for secrets
     * 
     * @return The user secrets
     */
    private Map<String, String> getUserSecrets() {
        // This should be replaced with a proper storage solution
        // For now, we'll use a static map for demonstration
        return TOTP_SECRETS;
    }
    
    // Static map for demonstration only
    private static final Map<String, String> TOTP_SECRETS = new HashMap<>();

    @Override
    public String getQrCodeUrl(String username, String secret) {
        return generateQrCode(username, secret);
    }
} 