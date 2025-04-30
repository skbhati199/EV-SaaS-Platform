package com.ev.station.dto;

import com.ev.station.model.ConnectorType;
import com.ev.station.model.EVSEStatus;
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
public class EVSEResponse {
    
    private UUID id;
    private String evseId;
    private String serialNumber;
    private String model;
    private String manufacturer;
    private EVSEStatus status;
    private String location;
    private Double latitude;
    private Double longitude;
    private Double maxPower;
    private ConnectorType connectorType;
    private UUID ownerId;
    private LocalDateTime lastHeartbeat;
    private String firmwareVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
