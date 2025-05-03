package com.ev.roamingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

/**
 * Database entity representing an OCPI Connector
 */
@Entity
@Table(name = "ocpi_connector")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evse_id", nullable = false)
    private EvseEntity evse;
    
    @Column(name = "connector_id", nullable = false, length = 36)
    private String connectorId;
    
    @Column(name = "connector_type", nullable = false, length = 20)
    private String connectorType;
    
    @Column(name = "format", nullable = false, length = 20)
    private String format;
    
    @Column(name = "power_type", nullable = false, length = 20)
    private String powerType;
    
    @Column(name = "max_voltage")
    private Integer maxVoltage;
    
    @Column(name = "max_amperage")
    private Integer maxAmperage;
    
    @Column(name = "max_electric_power")
    private Integer maxElectricPower;
    
    @Column(name = "tariff_ids")
    private String[] tariffIds;
    
    @Column(name = "last_updated")
    private ZonedDateTime lastUpdated;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
} 