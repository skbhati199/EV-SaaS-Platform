package com.ev.roamingservice.dto.event;

import com.ev.roamingservice.model.OcpiConnectionStatus;
import com.ev.roamingservice.model.OcpiRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event DTO for roaming partner-related events.
 * Used for publishing changes in roaming partner data to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoamingPartnerEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * Roaming partner ID
     */
    private Long partnerId;
    
    /**
     * Country code of the party
     */
    private String countryCode;
    
    /**
     * Party ID text
     */
    private String partyId;
    
    /**
     * Type of event
     */
    private RoamingPartnerEventType eventType;
    
    /**
     * Timestamp of when the event occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * Partner name
     */
    private String name;
    
    /**
     * Partner role (CPO, EMSP, etc.)
     */
    private OcpiRole role;
    
    /**
     * Connection status (CONNECTED, PENDING, etc.)
     */
    private OcpiConnectionStatus status;
    
    /**
     * Versions URL
     */
    private String versionsUrl;
    
    /**
     * Additional data related to the event, if any
     */
    private String additionalData;
    
    /**
     * Event types for roaming partner events
     */
    public enum RoamingPartnerEventType {
        CREATED,
        UPDATED,
        DELETED,
        CONNECTION_ESTABLISHED,
        CONNECTION_FAILED,
        CONNECTION_SUSPENDED,
        CONNECTION_RESUMED
    }
} 