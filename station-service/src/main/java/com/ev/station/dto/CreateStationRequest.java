package com.ev.station.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStationRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Serial number is required")
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
    
    @NotNull(message = "CPO ID is required")
    private UUID cpoId;
} 