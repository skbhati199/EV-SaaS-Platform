package com.ev.auth.service;

import com.ev.auth.model.User;

/**
 * Service for managing refresh tokens
 */
public interface RefreshTokenService {
    
    /**
     * Create a new refresh token for a user
     * @param userId The user ID
     * @return The refresh token
     */
    String createRefreshToken(String userId);
    
    /**
     * Validate a refresh token
     * @param token The refresh token
     * @return The user ID if valid
     */
    String validateRefreshToken(String token);
    
    /**
     * Revoke a refresh token
     * @param token The refresh token
     */
    void revokeRefreshToken(String token);
    
    /**
     * Revoke all refresh tokens for a user
     * @param userId The user ID
     */
    void revokeAllUserRefreshTokens(String userId);
} 