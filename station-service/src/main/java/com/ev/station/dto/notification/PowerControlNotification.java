package com.ev.station.dto.notification;

import com.ev.station.dto.event.PowerDistributionEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Notification sent to Admin UI when a power control command is applied to a station.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerControlNotification {
    
    /**
     * Type of notification
     */
    private NotificationType type;
    
    /**
     * ID of the station affected
     */
    private UUID stationId;
    
    /**
     * Friendly name of the station (if available)
     */
    private String stationName;
    
    /**
     * ID of the connector affected (null for station-wide)
     */
    private Integer connectorId;
    
    /**
     * New power limit in kW
     */
    private Double powerLimitKW;
    
    /**
     * Whether this is a temporary limit
     */
    private boolean temporary;
    
    /**
     * Expiration time for temporary limits
     */
    private LocalDateTime expiryTime;
    
    /**
     * Reason for the power adjustment
     */
    private PowerDistributionEvent.PowerAdjustmentReason reason;
    
    /**
     * Success flag - true if the command was accepted by the station
     */
    private boolean success;
    
    /**
     * Timestamp when the notification was created
     */
    private LocalDateTime timestamp;
    
    /**
     * Type of power control notification
     */
    public enum NotificationType {
        POWER_LIMIT_SET,
        POWER_LIMIT_CLEARED,
        POWER_LIMIT_EXPIRED,
        POWER_LIMIT_FAILED
    }
} 