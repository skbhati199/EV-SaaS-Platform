package com.ev.billingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usage_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false, insertable = false, updatable = false)
    private Subscription subscription;
    
    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;
    
    @Column(name = "meter_type", nullable = false)
    private String meterType;
    
    @Column(nullable = false)
    private BigDecimal quantity;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private boolean processed;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    
    public enum MeterType {
        CHARGING_SESSION,
        ENERGY_CONSUMPTION,
        API_CALLS,
        USERS,
        CHARGING_STATIONS
    }
} 