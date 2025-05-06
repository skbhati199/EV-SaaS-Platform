package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;

import java.util.List;
import java.util.Map;

/**
 * Service interface for interacting with Keycloak identity provider
 */
public interface KeycloakService {

    /**
     * Create a new user in Keycloak
     * @param request User registration details
     * @return Keycloak user ID
     */
    String createUser(RegisterRequest request);
    
    /**
     * Get authentication tokens
     * 
     * @param username The username
     * @param password The password
     * @return Authentication tokens
     */
    TokenResponse getTokens(String username, String password);
    
    /**
     * Validate a token
     * 
     * @param token The token to validate
     * @return True if token is valid
     */
    boolean validateToken(String token);
    
    /**
     * Refresh authentication tokens
     * 
     * @param refreshToken The refresh token
     * @return New authentication tokens
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * Update an existing user in Keycloak
     * 
     * @param userId The Keycloak user ID
     * @param email The updated email
     * @param firstName The updated first name
     * @param lastName The updated last name
     * @param attributes Any additional attributes to update
     */
    void updateUser(String userId, String email, String firstName, String lastName, Map<String, List<String>> attributes);
    
    /**
     * Delete a user from Keycloak
     * 
     * @param userId The Keycloak user ID
     */
    void deleteUser(String userId);
}
