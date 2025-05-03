package com.ev.roamingservice.ocpi.module.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCPI DisplayText DTO for multilingual text
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayText {
    
    private String language;
    private String text;
} 