package com.ev.roamingservice.ocpi.module.credentials.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Role object as per OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role {
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("business_details")
    private BusinessDetails businessDetails;
    
    @JsonProperty("party_id")
    private String partyId;
    
    @JsonProperty("country_code")
    private String countryCode;
} 