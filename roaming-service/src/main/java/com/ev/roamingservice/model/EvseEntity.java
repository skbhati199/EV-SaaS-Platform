package com.ev.roamingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Database entity representing an OCPI EVSE (Electric Vehicle Supply Equipment)
 */
@Entity
@Table(name = "ocpi_evse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;
    
    @Column(name = "evse_id", nullable = false, length = 36)
    private String evseId;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    @Column(name = "status_schedule_id", length = 36)
    private String statusScheduleId;
    
    @Column(name = "capabilities")
    private String[] capabilities;
    
    @Column(name = "floor_level", length = 4)
    private String floorLevel;
    
    @Column(name = "coordinates", length = 50)
    private String coordinates;
    
    @Column(name = "physical_reference", length = 16)
    private String physicalReference;
    
    @OneToMany(mappedBy = "evse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectorEntity> connectors;
    
    @Column(name = "last_updated")
    private ZonedDateTime lastUpdated;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
} 