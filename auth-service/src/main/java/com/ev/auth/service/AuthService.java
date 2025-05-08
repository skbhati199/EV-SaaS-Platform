package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;

import java.util.UUID;

/**
 * Service for user authentication and registration
 */
public interface AuthService {
    
    /**
     * Register a new user
     * @param request User registration details
     * @return User response with created user details
     */
    UserResponse registerUser(RegisterRequest request);
    
    /**
     * Authenticate a user and generate tokens
     * @param email User's email
     * @param password User's password
     * @return Authentication tokens
     */
    TokenResponse login(String email, String password);
    
    /**
     * Extract user ID from JWT token
     * @param token JWT token
     * @return User ID
     */
    UUID getUserIdFromToken(String token);
}
