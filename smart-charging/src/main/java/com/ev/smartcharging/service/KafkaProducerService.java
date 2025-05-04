package com.ev.smartcharging.service;

import com.ev.smartcharging.dto.event.PowerDistributionEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for producing Kafka events related to smart charging.
 */
public interface KafkaProducerService {
    
    /**
     * Sends a power distribution event to the Kafka topic for station control.
     * @param event The power distribution event to send
     * @return A completable future that completes when the event is sent
     */
    CompletableFuture<Void> sendPowerDistributionEvent(PowerDistributionEvent event);
    
    /**
     * Creates and sends a power distribution event with the given parameters.
     * @param stationId The ID of the charging station
     * @param connectorId The ID of the connector (null for whole station)
     * @param powerLimitKW The new power limit in kW
     * @param reason The reason for the power adjustment
     * @param temporary Whether this is a temporary or persistent limit
     * @param durationSeconds Duration in seconds (for temporary limits)
     * @param transactionId Optional transaction ID if related to a specific session
     * @return The ID of the created event
     */
    UUID sendPowerAdjustmentCommand(
            UUID stationId, 
            Integer connectorId, 
            Double powerLimitKW,
            PowerDistributionEvent.PowerAdjustmentReason reason,
            boolean temporary,
            Integer durationSeconds,
            UUID transactionId);
    
    /**
     * Sends an emergency power reduction command to a station or connector.
     * @param stationId The ID of the charging station
     * @param connectorId The ID of the connector (null for whole station)
     * @param powerLimitKW The reduced power limit in kW
     * @param durationSeconds Duration of the emergency reduction
     * @return The ID of the created event
     */
    UUID sendEmergencyPowerReduction(
            UUID stationId,
            Integer connectorId,
            Double powerLimitKW,
            Integer durationSeconds);
} 