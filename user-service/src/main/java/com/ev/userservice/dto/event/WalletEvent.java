package com.ev.userservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event DTO for wallet-related events.
 * Used for publishing changes in wallet data to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletEvent {
    
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * User ID
     */
    private UUID userId;
    
    /**
     * Wallet ID
     */
    private UUID walletId;
    
    /**
     * Type of event
     */
    private WalletEventType eventType;
    
    /**
     * Timestamp of the event
     */
    private LocalDateTime timestamp;
    
    /**
     * Amount involved in the transaction (if applicable)
     */
    private BigDecimal amount;
    
    /**
     * New balance after the transaction
     */
    private BigDecimal newBalance;
    
    /**
     * Transaction ID (if applicable)
     */
    private UUID transactionId;
    
    /**
     * Description of the transaction
     */
    private String description;
    
    /**
     * Reference to external system (e.g., payment gateway transaction ID)
     */
    private String externalReference;
    
    /**
     * Event types for wallet events
     */
    public enum WalletEventType {
        CREATED,
        TOPPED_UP,
        DEBITED,
        PAYMENT_INITIATED,
        PAYMENT_COMPLETED,
        PAYMENT_FAILED,
        REFUND_INITIATED,
        REFUND_COMPLETED,
        BALANCE_ADJUSTED,
        SUSPENDED,
        UNSUSPENDED
    }
} 