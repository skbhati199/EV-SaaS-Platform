package com.ev.smartcharging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "power_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "station_id")
    private UUID stationId;
    
    @Column(name = "group_id")
    private UUID groupId;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "max_power_kw", nullable = false)
    private Double maxPowerKW;
    
    @Column(name = "min_power_kw")
    private Double minPowerKW;
    
    @Column(name = "day_of_week")
    private String dayOfWeek; // comma-separated values, e.g. "1,2,3,4,5" for weekdays
    
    @Column(name = "price_tier")
    @Enumerated(EnumType.STRING)
    private PriceTier priceTier;
    
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