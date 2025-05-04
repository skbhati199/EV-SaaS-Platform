package com.ev.smartcharging.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event sent to charging stations to adjust power distribution in real-time.
 * This is used for dynamic power control based on smart charging algorithms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerDistributionEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * Station ID to which the power command is directed
     */
    private UUID stationId;
    
    /**
     * Connector ID (if applicable, null for whole station control)
     */
    private Integer connectorId;
    
    /**
     * New power limit in kW
     */
    private Double powerLimitKW;
    
    /**
     * Whether this is a temporary or persistent limit
     */
    private boolean temporary;
    
    /**
     * Duration in seconds (applicable for temporary limits)
     */
    private Integer durationSeconds;
    
    /**
     * Timestamp when the event was created
     */
    private LocalDateTime timestamp;
    
    /**
     * Reason for the power adjustment
     */
    private PowerAdjustmentReason reason;
    
    /**
     * Priority level of this command (higher priority commands override lower ones)
     */
    private int priority;
    
    /**
     * Transaction ID if this relates to a specific charging session
     */
    private UUID transactionId;
    
    /**
     * Reasons for power adjustment
     */
    public enum PowerAdjustmentReason {
        LOAD_BALANCING,
        GRID_CONSTRAINT,
        USER_REQUEST,
        SCHEDULED_PROFILE,
        DYNAMIC_PRICING,
        EMERGENCY_REDUCTION,
        SYSTEM_MAINTENANCE,
        OPTIMIZATION
    }
} 