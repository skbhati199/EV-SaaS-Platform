package com.ev.smartcharging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "charging_stations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStation {
    
    @Id
    private UUID id; // This comes from station-service 
    
    @Column(name = "max_power_kw", nullable = false)
    private Double maxPowerKW;
    
    @Column(name = "current_power_kw")
    private Double currentPowerKW;
    
    @Column(name = "priority_level")
    private Integer priorityLevel;
    
    @Column(nullable = false)
    private Boolean enabled;
    
    @Column(name = "smart_charging_enabled", nullable = false)
    private Boolean smartChargingEnabled;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "charging_group_id")
    private ChargingGroup chargingGroup;
    
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