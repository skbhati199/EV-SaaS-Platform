package com.ev.station.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "charging_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "station_id", nullable = false)
    private UUID stationId;
    
    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;
    
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;
    
    @Column(name = "id_tag")
    private String idTag;
    
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(name = "start_timestamp", nullable = false)
    private LocalDateTime startTimestamp;
    
    @Column(name = "stop_timestamp")
    private LocalDateTime stopTimestamp;
    
    @Column(name = "meter_start", nullable = false)
    private Integer meterStart;
    
    @Column(name = "meter_stop")
    private Integer meterStop;
    
    @Column(name = "start_reason")
    private String startReason;
    
    @Column(name = "stop_reason")
    private String stopReason;
    
    @Column(name = "total_energy_kwh")
    private BigDecimal totalEnergyKwh;
    
    @Column(name = "total_cost")
    private BigDecimal totalCost;
    
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currency == null) {
            currency = "USD";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 