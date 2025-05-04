package com.ev.smartcharging.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "charging_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "max_power_kw", nullable = false)
    private Double maxPowerKW;
    
    @Column(name = "current_power_kw")
    private Double currentPowerKW;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(name = "load_balancing_strategy")
    @Enumerated(EnumType.STRING)
    private LoadBalancingStrategy loadBalancingStrategy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "chargingGroup", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<ChargingStation> stations = new HashSet<>();
    
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