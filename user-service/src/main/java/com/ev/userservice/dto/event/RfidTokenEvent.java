package com.ev.userservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event DTO for RFID token-related events.
 * Used for publishing changes in RFID token data to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidTokenEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * User ID
     */
    private UUID userId;
    
    /**
     * RFID token ID
     */
    private UUID rfidTokenId;
    
    /**
     * RFID token value
     */
    private String tokenValue;
    
    /**
     * Type of event
     */
    private RfidTokenEventType eventType;
    
    /**
     * Timestamp of the event
     */
    private LocalDateTime timestamp;
    
    /**
     * Status of the RFID token
     */
    private String status;
    
    /**
     * Name given to the token by the user
     */
    private String tokenName;
    
    /**
     * Additional data related to the event
     */
    private String additionalData;
    
    /**
     * Event types for RFID token events
     */
    public enum RfidTokenEventType {
        CREATED,
        ACTIVATED,
        DEACTIVATED,
        SUSPENDED,
        LOST,
        DELETED,
        UPDATED,
        USED,
        EXPIRED
    }
} 