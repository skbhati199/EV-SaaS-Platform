package com.ev.station.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "connectors",
       uniqueConstraints = @UniqueConstraint(columnNames = {"station_id", "connector_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connector {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;
    
    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "connector_type", nullable = false)
    private ConnectorType connectorType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "power_type", nullable = false)
    private PowerType powerType;
    
    @Column(name = "max_voltage")
    private Integer maxVoltage;
    
    @Column(name = "max_amperage")
    private Integer maxAmperage;
    
    @Column(name = "max_power_kw")
    private Double maxPowerKw;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StationStatus status;
    
    @Column(name = "last_status_update")
    private LocalDateTime lastStatusUpdate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastStatusUpdate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 