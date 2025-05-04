package com.ev.billingservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for managing charging transactions based on events from the charging session
 */
public interface TransactionService {
    
    /**
     * Create a pending transaction when a charging session starts
     *
     * @param sessionId ID of the charging session
     * @param userId ID of the user
     * @param stationId ID of the station
     * @param connectorId ID of the connector
     * @param startTime Time when the session started
     * @return ID of the created transaction
     */
    UUID createPendingTransaction(
        UUID sessionId,
        UUID userId,
        UUID stationId,
        UUID connectorId,
        LocalDateTime startTime
    );
    
    /**
     * Update a pending transaction with current values
     *
     * @param sessionId ID of the charging session
     * @param energyDeliveredKwh Energy delivered so far in kWh
     * @param durationSeconds Duration of the session in seconds
     * @param meterValue Current meter value
     */
    void updatePendingTransaction(
        UUID sessionId,
        BigDecimal energyDeliveredKwh,
        Long durationSeconds,
        BigDecimal meterValue
    );
    
    /**
     * Complete a transaction when a charging session ends and generate an invoice
     *
     * @param sessionId ID of the charging session
     * @param endTime Time when the session ended
     * @param energyDeliveredKwh Total energy delivered in kWh
     * @param durationSeconds Total duration of the session in seconds
     * @return ID of the generated invoice
     */
    UUID completeTransaction(
        UUID sessionId,
        LocalDateTime endTime,
        BigDecimal energyDeliveredKwh,
        Long durationSeconds
    );
    
    /**
     * Get the current state of a transaction
     *
     * @param sessionId ID of the charging session
     * @return Transaction information
     */
    Object getTransactionBySessionId(UUID sessionId);
} 