package com.ev.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false, insertable = false, updatable = false)
    private Invoice invoice;
    
    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELED
    }
} 