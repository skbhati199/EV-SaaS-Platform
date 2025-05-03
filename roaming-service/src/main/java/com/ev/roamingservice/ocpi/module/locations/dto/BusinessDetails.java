package com.ev.roamingservice.ocpi.module.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCPI BusinessDetails DTO for operator/owner details
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDetails {
    
    private String name;
    private String website;
    private Image logo;
} 