package com.ev.station.dto;

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
public class ChargingStationDto {
    private UUID id;
    private String name;
    private String serialNumber;
    private String model;
    private String vendor;
    private String firmwareVersion;
    private Double locationLatitude;
    private Double locationLongitude;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private UUID cpoId;
    private StationStatus status;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime registrationDate;
    private int availableConnectors;
    private int totalConnectors;
} 