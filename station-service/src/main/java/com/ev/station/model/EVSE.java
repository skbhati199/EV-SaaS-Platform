package com.ev.station.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EVSE {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String evseId;
    
    @Column(nullable = false)
    private String serialNumber;
    
    @Column(nullable = false)
    private String model;
    
    @Column(nullable = false)
    private String manufacturer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EVSEStatus status;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    @Column(nullable = false)
    private Double maxPower;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectorType connectorType;
    
    @Column(nullable = false)
    private UUID ownerId;
    
    @Column(nullable = false)
    private LocalDateTime lastHeartbeat;
    
    @Column(nullable = false)
    private String firmwareVersion;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
