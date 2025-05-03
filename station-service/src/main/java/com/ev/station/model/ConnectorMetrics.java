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
@Table(name = "connector_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "station_id", nullable = false)
    private UUID stationId;
    
    @Column(name = "connector_id", nullable = false)
    private Integer connectorId;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "meter_value", nullable = false)
    private BigDecimal meterValue;
    
    @Column(name = "current_a")
    private BigDecimal currentA;
    
    @Column(name = "voltage_v")
    private BigDecimal voltageV;
    
    @Column(name = "power_kw")
    private BigDecimal powerKw;
    
    @Column(name = "temperature_c")
    private BigDecimal temperatureC;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
} 