package com.ev.roamingservice.ocpi.module.credentials.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for OCPI Credentials module
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Credentials {
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("roles")
    private List<CredentialsRole> roles;
    
    @JsonProperty("business_details")
    private BusinessDetails businessDetails;
    
    @JsonProperty("party_id")
    private String partyId;
    
    @JsonProperty("country_code")
    private String countryCode;
} 