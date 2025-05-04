package com.ev.billingservice.repository;

import com.ev.billingservice.model.ChargingTransaction;
import com.ev.billingservice.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing charging transactions
 */
@Repository
public interface ChargingTransactionRepository extends JpaRepository<ChargingTransaction, UUID> {
    
    /**
     * Find a transaction by session ID
     */
    Optional<ChargingTransaction> findBySessionId(UUID sessionId);
    
    /**
     * Find all transactions for a user
     */
    List<ChargingTransaction> findByUserId(UUID userId);
    
    /**
     * Find all transactions for a station
     */
    List<ChargingTransaction> findByStationId(UUID stationId);
    
    /**
     * Find all transactions with a given status
     */
    List<ChargingTransaction> findByStatus(TransactionStatus status);
    
    /**
     * Find all transactions for a user with a given status
     */
    List<ChargingTransaction> findByUserIdAndStatus(UUID userId, TransactionStatus status);
    
    /**
     * Find all transactions created between two dates
     */
    List<ChargingTransaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find all transactions for a user created between two dates
     */
    List<ChargingTransaction> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find all transactions included in an invoice
     */
    List<ChargingTransaction> findByInvoiceId(UUID invoiceId);
    
    /**
     * Find all transactions for a subscription
     */
    List<ChargingTransaction> findBySubscriptionId(UUID subscriptionId);
    
    /**
     * Find all completed transactions that are not yet invoiced
     */
    List<ChargingTransaction> findByStatusAndInvoiceIdIsNull(TransactionStatus status);
} 