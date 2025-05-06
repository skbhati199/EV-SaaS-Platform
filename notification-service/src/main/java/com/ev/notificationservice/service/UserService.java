package com.ev.notificationservice.service;

import com.ev.notificationservice.dto.event.UserEvent;
import java.util.UUID;

/**
 * Service for managing users in the notification service.
 * This serves as a client to the User Service microservice.
 */
public interface UserService {
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User event with basic user information
     */
    UserEvent getUserById(UUID userId);
    
    /**
     * Get user details by user ID
     * @param userId User ID
     * @return User details as formatted string
     */
    String getUserDetailsById(UUID userId);
    
    /**
     * Get user email by user ID
     * @param userId User ID
     * @return User email address
     */
    String getUserEmailById(UUID userId);
    
    /**
     * Get user phone by user ID
     * @param userId User ID
     * @return User phone number
     */
    String getUserPhoneById(UUID userId);
    
    /**
     * Check if user exists
     * @param userId User ID
     * @return true if user exists, false otherwise
     */
    boolean userExists(UUID userId);
} 