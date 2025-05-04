package com.ev.roamingservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event DTO for location-related events.
 * Used for publishing changes in location data to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * Location ID
     */
    private String locationId;
    
    /**
     * Country code of the party
     */
    private String countryCode;
    
    /**
     * Party ID
     */
    private String partyId;
    
    /**
     * Type of event
     */
    private LocationEventType eventType;
    
    /**
     * Timestamp of when the event occurred
     */
    private ZonedDateTime timestamp;
    
    /**
     * Location name
     */
    private String name;
    
    /**
     * Location address
     */
    private String address;
    
    /**
     * Location city
     */
    private String city;
    
    /**
     * Location coordinates
     */
    private String coordinates;
    
    /**
     * Additional data related to the event, if any
     */
    private String additionalData;
    
    /**
     * Event types for location events
     */
    public enum LocationEventType {
        CREATED,
        UPDATED,
        DELETED,
        EVSE_ADDED,
        EVSE_UPDATED,
        EVSE_REMOVED,
        CONNECTOR_ADDED,
        CONNECTOR_UPDATED,
        CONNECTOR_REMOVED
    }
} 