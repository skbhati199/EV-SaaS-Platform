package com.ev.roamingservice.ocpi.module.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCPI GeoLocation DTO representing geographic coordinates
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoLocation {
    
    private Double latitude;
    private Double longitude;
} 