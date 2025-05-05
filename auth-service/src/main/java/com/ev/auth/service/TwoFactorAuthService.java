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

/**
 * Service interface for two-factor authentication
 */
public interface TwoFactorAuthService {
    
    /**
     * Validate a two-factor authentication code
     * 
     * @param username The username
     * @param code The authentication code
     * @return True if the code is valid
     */
    boolean validate(String username, String code);
    
    /**
     * Generate a secret for two-factor authentication
     * 
     * @param username The username
     * @return The generated secret
     */
    String generateSecret(String username);
    
    /**
     * Enable two-factor authentication for a user
     * 
     * @param username The username
     * @return True if two-factor authentication was enabled
     */
    boolean enable(String username);
    
    /**
     * Disable two-factor authentication for a user
     * 
     * @param username The username
     * @return True if two-factor authentication was disabled
     */
    boolean disable(String username);
    
    /**
     * Get the QR code URL for setting up two-factor authentication
     * 
     * @param username The username
     * @param secret The two-factor authentication secret
     * @return The QR code URL
     */
    String getQrCodeUrl(String username, String secret);
    
    /**
     * Verify a two-factor authentication code
     * 
     * @param request The two-factor authentication request
     * @return True if the code is valid
     */
    boolean verifyCode(TwoFactorAuthRequest request);
} 