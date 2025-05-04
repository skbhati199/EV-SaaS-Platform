package com.ev.billingservice.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event received from station-service when a charging session changes state
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargingSessionEvent {
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * ID of the charging session
     */
    private UUID sessionId;
    
    /**
     * ID of the station
     */
    private UUID stationId;
    
    /**
     * ID of the connector
     */
    private UUID connectorId;
    
    /**
     * Type of event (STARTED, UPDATED, ENDED)
     */
    private String eventType;
    
    /**
     * ID of the user who initiated the session
     */
    private UUID userId;
    
    /**
     * ID token used to authenticate the session (RFID, etc)
     */
    private String idToken;
    
    /**
     * Current status of the session
     */
    private String sessionStatus;
    
    /**
     * Timestamp when the session started
     */
    private LocalDateTime startTime;
    
    /**
     * Timestamp when the session ended (null if still active)
     */
    private LocalDateTime endTime;
    
    /**
     * Timestamp when this event was created
     */
    private LocalDateTime timestamp;
    
    /**
     * Energy consumed in kWh so far
     */
    private BigDecimal energyDeliveredKwh;
    
    /**
     * Duration of the session in seconds so far
     */
    private Long durationSeconds;
    
    /**
     * Current meter start in kWh
     */
    private BigDecimal meterStart;
    
    /**
     * Current meter value in kWh
     */
    private BigDecimal meterValue;
    
    /**
     * Reason for ending the session (if applicable)
     */
    private String stopReason;
} 