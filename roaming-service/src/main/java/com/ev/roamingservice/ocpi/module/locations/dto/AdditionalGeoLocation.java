package com.ev.roamingservice.ocpi.module.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCPI AdditionalGeoLocation DTO for related locations
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalGeoLocation {
    
    private String name;
    private GeoLocation coordinates;
} 