package com.ev.roamingservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Event DTO for Charge Detail Record (CDR) events.
 * Used for publishing CDR data to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdrEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * CDR ID
     */
    private String cdrId;
    
    /**
     * Country code of the CPO
     */
    private String countryCode;
    
    /**
     * Party ID of the CPO
     */
    private String partyId;
    
    /**
     * Type of event
     */
    private CdrEventType eventType;
    
    /**
     * Timestamp of when the event occurred
     */
    private ZonedDateTime timestamp;
    
    /**
     * Start date/time of the charging session
     */
    private ZonedDateTime startDateTime;
    
    /**
     * End date/time of the charging session
     */
    private ZonedDateTime endDateTime;
    
    /**
     * Session ID
     */
    private String sessionId;
    
    /**
     * Location ID where charging took place
     */
    private String locationId;
    
    /**
     * EVSE ID where charging took place
     */
    private String evseId;
    
    /**
     * Connector ID used for charging
     */
    private String connectorId;
    
    /**
     * Total energy delivered in kWh
     */
    private BigDecimal totalEnergy;
    
    /**
     * Total cost of the charging session
     */
    private BigDecimal totalCost;
    
    /**
     * Currency of the cost
     */
    private String currency;
    
    /**
     * Status of the CDR (e.g., OPEN, SENT, RECEIVED, SETTLED)
     */
    private String status;
    
    /**
     * Token ID used for charging
     */
    private String authorizationId;
    
    /**
     * Additional data related to the event, if any
     */
    private String additionalData;
    
    /**
     * Event types for CDR events
     */
    public enum CdrEventType {
        CREATED,
        UPDATED,
        SENT,
        RECEIVED,
        SETTLED,
        DISPUTED,
        CORRECTED
    }
} 