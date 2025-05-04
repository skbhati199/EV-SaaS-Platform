package com.ev.station.service;

import com.ev.station.dto.event.PowerDistributionEvent;
import com.ev.station.dto.notification.PowerControlNotification;

import java.util.UUID;

/**
 * Service for sending real-time notifications to clients.
 */
public interface NotificationService {
    
    /**
     * Sends a power control notification to all admin UI clients
     * 
     * @param notification The notification to send
     */
    void sendPowerControlNotification(PowerControlNotification notification);
    
    /**
     * Creates and sends a power limit set notification
     * 
     * @param stationId The ID of the charging station
     * @param stationName The name of the station (if available)
     * @param connectorId The ID of the connector (null for station-wide)
     * @param powerLimitKW The power limit in kW
     * @param reason The reason for the power adjustment
     * @param temporary Whether this is a temporary limit
     * @param durationSeconds Duration in seconds for temporary limits
     * @param success Whether the command was successful
     */
    void notifyPowerLimitSet(
            UUID stationId,
            String stationName,
            Integer connectorId,
            Double powerLimitKW,
            PowerDistributionEvent.PowerAdjustmentReason reason,
            boolean temporary,
            Integer durationSeconds,
            boolean success);
    
    /**
     * Creates and sends a power limit cleared notification
     * 
     * @param stationId The ID of the charging station
     * @param stationName The name of the station (if available)
     * @param connectorId The ID of the connector (null for station-wide)
     * @param success Whether the command was successful
     */
    void notifyPowerLimitCleared(
            UUID stationId,
            String stationName,
            Integer connectorId,
            boolean success);
    
    /**
     * Creates and sends a power limit expired notification
     * 
     * @param stationId The ID of the charging station
     * @param stationName The name of the station (if available)
     * @param connectorId The ID of the connector (null for station-wide)
     */
    void notifyPowerLimitExpired(
            UUID stationId,
            String stationName,
            Integer connectorId);
} 