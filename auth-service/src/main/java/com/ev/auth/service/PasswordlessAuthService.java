package com.ev.auth.service;

import com.ev.auth.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordlessAuthService {

    private final KeycloakService keycloakService;
    private final Map<String, PasswordlessRequest> pendingRequests = new ConcurrentHashMap<>();
    
    @Value("${app.passwordless.token-expiration-minutes}")
    private int tokenExpirationMinutes;
    
    private static class PasswordlessRequest {
        private final String token;
        private final String userId;
        private final LocalDateTime expiresAt;
        
        public PasswordlessRequest(String token, String userId, LocalDateTime expiresAt) {
            this.token = token;
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
    
    /**
     * Generate a passwordless authentication request
     * 
     * @param email The user's email
     * @return A token for passwordless authentication
     */
    public String generatePasswordlessRequest(String email) {
        try {
            // Create a secure token
            String token = generateSecureToken();
            
            // Get the user ID from Keycloak
            // This is a simplification - in a real implementation, you would need to query Keycloak for the user ID
            String userId = "user-id"; // Replace with actual Keycloak user ID lookup
            
            // Store the request
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);
            pendingRequests.put(token, new PasswordlessRequest(token, userId, expiresAt));
            
            log.info("Generated passwordless login request for email: {}", email);
            return token;
        } catch (Exception e) {
            log.error("Error generating passwordless login request", e);
            throw new RuntimeException("Failed to generate passwordless login request", e);
        }
    }
    
    /**
     * Validate a passwordless authentication token and authenticate the user
     * 
     * @param token The passwordless token
     * @return Authentication tokens if the token is valid
     */
    public TokenResponse authenticateWithToken(String token) {
        // Get and remove the request
        PasswordlessRequest request = pendingRequests.remove(token);
        
        if (request == null) {
            log.warn("Invalid passwordless token: {}", token);
            throw new RuntimeException("Invalid or expired token");
        }
        
        if (request.isExpired()) {
            log.warn("Expired passwordless token: {}", token);
            throw new RuntimeException("Token has expired");
        }
        
        // Authenticate the user
        // In a real implementation, you would use Keycloak's client credentials flow or similar
        // to authenticate the user without a password
        
        log.info("User authenticated with passwordless token: {}", token);
        
        // For now, we'll simulate token generation
        return new TokenResponse("simulated-access-token", "simulated-refresh-token");
    }
    
    /**
     * Generate a secure random token
     * 
     * @return A secure random token
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
} 