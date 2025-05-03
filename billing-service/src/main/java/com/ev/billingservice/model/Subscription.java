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
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false, insertable = false, updatable = false)
    private BillingPlan billingPlan;
    
    @Column(name = "plan_id", nullable = false)
    private UUID planId;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;
    
    @Column(name = "auto_renew")
    private boolean autoRenew;
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices;
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsageRecord> usageRecords;
    
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
    
    public enum SubscriptionStatus {
        ACTIVE,
        PAST_DUE,
        CANCELED,
        EXPIRED,
        TRIAL,
        PENDING
    }
    
    public enum BillingCycle {
        MONTHLY,
        YEARLY
    }
} 