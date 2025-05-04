package com.ev.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a charging transaction
 */
@Entity
@Table(name = "charging_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * ID of the charging session this transaction is for
     */
    @Column(unique = true, nullable = false)
    private UUID sessionId;
    
    /**
     * ID of the user who initiated the session
     */
    @Column(nullable = false)
    private UUID userId;
    
    /**
     * ID of the station used for charging
     */
    @Column(nullable = false)
    private UUID stationId;
    
    /**
     * ID of the connector used for charging
     */
    @Column(nullable = false)
    private UUID connectorId;
    
    /**
     * Status of the transaction (PENDING, COMPLETED, CANCELED, FAILED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    
    /**
     * Time when the transaction was created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Time when the transaction was last updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Time when the charging session started
     */
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    /**
     * Time when the charging session ended (null if still active)
     */
    private LocalDateTime endTime;
    
    /**
     * Energy consumed in kWh
     */
    @Column(precision = 10, scale = 3)
    private BigDecimal energyDeliveredKwh;
    
    /**
     * Duration of the session in seconds
     */
    private Long durationSeconds;
    
    /**
     * Current meter value in kWh
     */
    @Column(precision = 10, scale = 3)
    private BigDecimal meterValue;
    
    /**
     * Total amount charged (calculated based on billing plan)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * Currency of the amount
     */
    private String currency;
    
    /**
     * ID of the invoice this transaction is included in (null if not yet invoiced)
     */
    private UUID invoiceId;
    
    /**
     * ID of the billing plan used for calculation
     */
    private UUID billingPlanId;
    
    /**
     * ID of the subscription associated with this transaction
     */
    private UUID subscriptionId;
    
    /**
     * Pre-persist hook to set timestamps
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    /**
     * Pre-update hook to update timestamps
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 