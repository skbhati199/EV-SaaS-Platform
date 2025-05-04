package com.ev.station.service;

import com.ev.station.dto.event.PowerDistributionEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for controlling charging power on charging stations.
 */
public interface PowerControlService {
    
    /**
     * Processes a power distribution event from the smart charging service
     * and applies the appropriate power limits to the charging station.
     *
     * @param event The power distribution event to process
     * @return True if the power control was successfully applied, false otherwise
     */
    boolean processPowerDistributionEvent(PowerDistributionEvent event);
    
    /**
     * Sets a power limit on a specific connector of a charging station.
     *
     * @param stationId The ID of the charging station
     * @param connectorId The ID of the connector (0 for the whole station)
     * @param powerLimitKW The power limit in kW
     * @param durationSeconds Duration of the limit in seconds (null for persistent)
     * @param profileId Optional profile ID for tracking
     * @return A CompletableFuture that completes when the command is processed
     */
    CompletableFuture<Boolean> setConnectorPowerLimit(
            String stationId, 
            int connectorId, 
            double powerLimitKW, 
            Integer durationSeconds,
            Integer profileId);
    
    /**
     * Clears a power limit on a specific connector of a charging station.
     *
     * @param stationId The ID of the charging station
     * @param connectorId The ID of the connector (0 for the whole station)
     * @param profileId The profile ID to clear
     * @return A CompletableFuture that completes when the command is processed
     */
    CompletableFuture<Boolean> clearConnectorPowerLimit(String stationId, int connectorId, int profileId);
    
    /**
     * Calculates a power limit profile ID based on the event.
     * This helps to track and manage power limit profiles.
     *
     * @param event The power distribution event
     * @return A unique profile ID
     */
    int calculateProfileId(PowerDistributionEvent event);
} 