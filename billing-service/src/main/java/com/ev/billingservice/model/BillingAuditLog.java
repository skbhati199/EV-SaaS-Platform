package com.ev.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "billing_audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "entity_type", nullable = false)
    private String entityType;
    
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;
    
    @Column(nullable = false)
    private String action;
    
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(columnDefinition = "jsonb")
    private String details;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
    }
    
    public enum EntityType {
        BILLING_PLAN,
        SUBSCRIPTION,
        INVOICE,
        PAYMENT,
        USAGE_RECORD,
        PAYMENT_METHOD,
        BILLING_SETTINGS
    }
    
    public enum Action {
        CREATE,
        UPDATE,
        DELETE,
        ACTIVATE,
        DEACTIVATE,
        PAYMENT_PROCESSED,
        SUBSCRIPTION_RENEWED,
        SUBSCRIPTION_CANCELED,
        INVOICE_GENERATED,
        INVOICE_PAID
    }
} 