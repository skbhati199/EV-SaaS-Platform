package com.ev.smartcharging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private UUID id; // This comes from station-service
    
    @Column(name = "station_id")
    private UUID stationId;
    
    @Column(name = "connector_id")
    private Integer connectorId;
    
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "allocated_power_kw")
    private Double allocatedPowerKW;
    
    @Column(name = "max_power_kw")
    private Double maxPowerKW;
    
    @Column(name = "energy_delivered_kwh")
    private Double energyDeliveredKWh;
    
    @Column(name = "priority_level")
    private Integer priorityLevel;
    
    @Column(name = "session_status")
    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 