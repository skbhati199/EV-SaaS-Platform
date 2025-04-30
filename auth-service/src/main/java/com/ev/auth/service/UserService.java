package com.ev.auth.service;

import com.ev.auth.dto.UserResponse;
import com.ev.auth.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for user management operations
 */
public interface UserService {
    
    /**
     * Get a user by ID
     * @param id User ID
     * @return Optional containing the user if found
     */
    Optional<UserResponse> getUserById(UUID id);
    
    /**
     * Get a user by email
     * @param email User email
     * @return Optional containing the user if found
     */
    Optional<UserResponse> getUserByEmail(String email);
    
    /**
     * Get all users
     * @return List of all users
     */
    List<UserResponse> getAllUsers();
    
    /**
     * Deactivate a user account
     * @param id User ID
     * @return Updated user information
     */
    UserResponse deactivateUser(UUID id);
    
    /**
     * Activate a user account
     * @param id User ID
     * @return Updated user information
     */
    UserResponse activateUser(UUID id);
}
