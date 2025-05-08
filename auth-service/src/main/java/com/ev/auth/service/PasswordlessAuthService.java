package com.ev.auth.service;

import com.ev.auth.dto.TokenResponse;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
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

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Map<String, PasswordlessRequest> pendingRequests = new ConcurrentHashMap<>();
    
    @Value("${app.passwordless.token-expiration-minutes}")
    private int tokenExpirationMinutes;
    
    private static class PasswordlessRequest {
        private final String token;
        private final String email;
        private final LocalDateTime expiresAt;
        
        public PasswordlessRequest(String token, String email, LocalDateTime expiresAt) {
            this.token = token;
            this.email = email;
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
        // Generate a secure token
        String token = generateSecureToken();
        
        // Store the token with an expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);
        PasswordlessRequest request = new PasswordlessRequest(token, email, expiresAt);
        pendingRequests.put(token, request);
        
        log.info("Generated passwordless login token for email: {}", email);
        
        return token;
    }
    
    /**
     * Authenticate a user with a passwordless token
     * 
     * @param token The passwordless token
     * @return Authentication tokens
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
        
        // Find the user by email
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate JWT tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        log.info("User authenticated with passwordless token: {}", token);
        
        // Return tokens
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600) // 1 hour in seconds
                .build();
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