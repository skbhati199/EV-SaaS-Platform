package com.ev.roamingservice.ocpi.module.credentials.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Business details object as per OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessDetails {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("website")
    private String website;
    
    @JsonProperty("logo")
    private Image logo;
} 