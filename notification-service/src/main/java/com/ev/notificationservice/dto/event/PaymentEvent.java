package com.ev.notificationservice.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event received from billing service when a payment is processed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentEvent {
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * ID of the payment
     */
    private UUID paymentId;
    
    /**
     * ID of the user
     */
    private UUID userId;
    
    /**
     * ID of the invoice
     */
    private UUID invoiceId;
    
    /**
     * Type of event (CREATED, PROCESSING, COMPLETED, FAILED, REFUNDED)
     */
    private String eventType;
    
    /**
     * Amount of the payment
     */
    private BigDecimal amount;
    
    /**
     * Currency of the payment
     */
    private String currency;
    
    /**
     * Payment method (CREDIT_CARD, BANK_TRANSFER, WALLET, etc.)
     */
    private String paymentMethod;
    
    /**
     * Payment method details (last 4 digits of card, wallet ID, etc.)
     */
    private String paymentMethodDetails;
    
    /**
     * Status of the payment
     */
    private String status;
    
    /**
     * Timestamp when the payment was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the payment status was last updated
     */
    private LocalDateTime updatedAt;
    
    /**
     * Timestamp when this event was created
     */
    private LocalDateTime timestamp;
    
    /**
     * Transaction ID from payment processor
     */
    private String transactionId;
    
    /**
     * Error code if the payment failed
     */
    private String errorCode;
    
    /**
     * Error message if the payment failed
     */
    private String errorMessage;
} 