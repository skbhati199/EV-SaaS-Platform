package com.ev.station.dto;

import com.ev.station.model.StationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStationRequest {
    
    private String name;
    
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
    
    private StationStatus status;
} 