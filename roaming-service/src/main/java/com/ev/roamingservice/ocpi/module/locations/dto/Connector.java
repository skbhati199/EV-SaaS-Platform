package com.ev.roamingservice.ocpi.module.locations.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * OCPI Connector DTO representing a specific connector socket at an EVSE
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connector {
    
    private String id;
    
    private String standard;
    
    private String format;
    
    @JsonProperty("power_type")
    private String powerType;
    
    @JsonProperty("max_voltage")
    private Integer maxVoltage;
    
    @JsonProperty("max_amperage")
    private Integer maxAmperage;
    
    @JsonProperty("max_electric_power")
    private Integer maxElectricPower;
    
    @JsonProperty("tariff_ids")
    private List<String> tariffIds;
    
    @JsonProperty("terms_and_conditions")
    private String termsAndConditions;
    
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private ZonedDateTime lastUpdated;
} 