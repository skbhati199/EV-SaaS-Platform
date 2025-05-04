package com.ev.userservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event DTO for user-related events.
 * Used for publishing changes in user data to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * User ID
     */
    private UUID userId;
    
    /**
     * Type of event (CREATED, UPDATED, DELETED, PASSWORD_CHANGED, etc.)
     */
    private UserEventType eventType;
    
    /**
     * Timestamp of when the event occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * User email
     */
    private String email;
    
    /**
     * User's first name
     */
    private String firstName;
    
    /**
     * User's last name
     */
    private String lastName;
    
    /**
     * User's phone number
     */
    private String phoneNumber;
    
    /**
     * User's account status (enabled/disabled)
     */
    private boolean enabled;
    
    /**
     * Additional data related to the event, if any
     */
    private String additionalData;
    
    /**
     * Event types for user events
     */
    public enum UserEventType {
        CREATED,
        UPDATED,
        DELETED,
        PASSWORD_CHANGED,
        EMAIL_VERIFIED,
        ACCOUNT_LOCKED,
        ACCOUNT_UNLOCKED,
        ACCOUNT_DISABLED,
        ACCOUNT_ENABLED,
        PROFILE_UPDATED,
        LOGIN_SUCCESS,
        LOGIN_FAILED
    }
} 