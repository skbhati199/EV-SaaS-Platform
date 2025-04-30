package com.ev.auth.service;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;

public interface AuthService {
    
    /**
     * Register a new user in the system and Keycloak
     * @param request Registration details
     * @return User information
     */
    UserResponse register(RegisterRequest request);
    
    /**
     * Authenticate a user and generate access tokens
     * @param request Login credentials
     * @return Authentication tokens
     */
    TokenResponse login(LoginRequest request);
    
    /**
     * Get the current authenticated user
     * @return User information
     */
    UserResponse getCurrentUser();
}
