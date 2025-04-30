package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;

/**
 * Service for interacting with Keycloak identity provider
 */
public interface KeycloakService {
    
    /**
     * Create a new user in Keycloak
     * @param request User registration details
     * @return Keycloak user ID
     */
    String createUser(RegisterRequest request);
    
    /**
     * Get authentication tokens from Keycloak
     * @param username User's email
     * @param password User's password
     * @return Authentication tokens
     */
    TokenResponse getTokens(String username, String password);
    
    /**
     * Validate a token
     * @param token JWT token to validate
     * @return True if token is valid
     */
    boolean validateToken(String token);
    
    /**
     * Refresh an access token using a refresh token
     * @param refreshToken Refresh token
     * @return New authentication tokens
     */
    TokenResponse refreshToken(String refreshToken);
}
