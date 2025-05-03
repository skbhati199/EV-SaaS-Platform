package com.ev.station.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "charging_stations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;
    
    private String model;
    
    private String vendor;
    
    @Column(name = "firmware_version")
    private String firmwareVersion;
    
    @Column(name = "location_latitude")
    private Double locationLatitude;
    
    @Column(name = "location_longitude")
    private Double locationLongitude;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String country;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "cpo_id")
    private UUID cpoId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StationStatus status;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Connector> connectors = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        registrationDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addConnector(Connector connector) {
        connectors.add(connector);
        connector.setStation(this);
    }
    
    public void removeConnector(Connector connector) {
        connectors.remove(connector);
        connector.setStation(null);
    }
} 