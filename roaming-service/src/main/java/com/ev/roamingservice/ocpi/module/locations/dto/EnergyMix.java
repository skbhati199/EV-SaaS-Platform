package com.ev.roamingservice.ocpi.module.locations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OCPI EnergyMix DTO for location energy sources
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyMix {
    
    @JsonProperty("is_green_energy")
    private Boolean isGreenEnergy;
    
    @JsonProperty("energy_sources")
    private List<EnergySource> energySources;
    
    @JsonProperty("environ_impact")
    private List<EnvironmentalImpact> environImpact;
    
    @JsonProperty("supplier_name")
    private String supplierName;
    
    @JsonProperty("energy_product_name")
    private String energyProductName;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnergySource {
        
        private String source;
        private Float percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvironmentalImpact {
        
        private String source;
        private Float amount;
    }
} 