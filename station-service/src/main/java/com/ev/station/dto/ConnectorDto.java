package com.ev.station.dto;

import com.ev.station.model.ConnectorType;
import com.ev.station.model.PowerType;
import com.ev.station.model.StationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorDto {
    private UUID id;
    private UUID stationId;
    private Integer connectorId;
    private ConnectorType connectorType;
    private PowerType powerType;
    private Integer maxVoltage;
    private Integer maxAmperage;
    private Double maxPowerKw;
    private StationStatus status;
    private LocalDateTime lastStatusUpdate;
} 