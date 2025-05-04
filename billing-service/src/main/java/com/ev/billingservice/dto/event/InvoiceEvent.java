package com.ev.billingservice.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event published when an invoice is created or updated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceEvent {
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * ID of the invoice
     */
    private UUID invoiceId;
    
    /**
     * ID of the user
     */
    private UUID userId;
    
    /**
     * Type of event (CREATED, UPDATED, PAID, CANCELED, OVERDUE)
     */
    private String eventType;
    
    /**
     * Invoice number (human-readable)
     */
    private String invoiceNumber;
    
    /**
     * Total amount of the invoice
     */
    private BigDecimal totalAmount;
    
    /**
     * Currency of the invoice
     */
    private String currency;
    
    /**
     * Status of the invoice
     */
    private String status;
    
    /**
     * Timestamp when the invoice was created
     */
    private LocalDateTime issuedAt;
    
    /**
     * Timestamp when the invoice is due
     */
    private LocalDateTime dueAt;
    
    /**
     * Timestamp when the invoice was paid (if applicable)
     */
    private LocalDateTime paidAt;
    
    /**
     * IDs of related charging sessions
     */
    private List<UUID> chargingSessionIds;
    
    /**
     * IDs of transactions included in the invoice
     */
    private List<UUID> transactionIds;
    
    /**
     * Timestamp when this event was created
     */
    private LocalDateTime timestamp;
    
    /**
     * URL to access the invoice online
     */
    private String invoiceUrl;
    
    /**
     * Notes or additional information about the invoice
     */
    private String notes;
} 