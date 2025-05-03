package com.ev.roamingservice.ocpi.module.locations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCPI Image DTO for location/EVSE images
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    
    private String url;
    
    private String thumbnail;
    
    private String category;
    
    private String type;
    
    private Integer width;
    
    private Integer height;
} 