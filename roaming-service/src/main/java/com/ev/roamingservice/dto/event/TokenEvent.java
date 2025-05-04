package com.ev.roamingservice.dto.event;

import com.ev.roamingservice.model.OcpiTokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event DTO for OCPI token-related events.
 * Used for publishing changes in token data to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * Token ID from the system
     */
    private Long tokenId;
    
    /**
     * The actual token value
     */
    private String tokenValue;
    
    /**
     * Type of token
     */
    private OcpiTokenType tokenType;
    
    /**
     * Type of event
     */
    private TokenEventType eventType;
    
    /**
     * Party ID associated with the token
     */
    private Long partyId;
    
    /**
     * Country code of the party
     */
    private String countryCode;
    
    /**
     * Party ID text
     */
    private String partyIdText;
    
    /**
     * Timestamp of when the event occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * Valid until date/time
     */
    private LocalDateTime validUntil;
    
    /**
     * Whether the token is revoked
     */
    private boolean revoked;
    
    /**
     * Additional data related to the event, if any
     */
    private String additionalData;
    
    /**
     * Event types for token events
     */
    public enum TokenEventType {
        CREATED,
        UPDATED,
        DELETED,
        REVOKED,
        VALIDATED,
        EXPIRED
    }
} 