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
 * OCPI Location DTO representing a charging location with EVSEs
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @JsonProperty("country_code")
    private String countryCode;
    
    @JsonProperty("party_id")
    private String partyId;
    
    private String id;
    
    private Boolean publish;
    
    private String name;
    
    private String address;
    
    private String city;
    
    @JsonProperty("postal_code")
    private String postalCode;
    
    private String country;
    
    @JsonProperty("coordinates")
    private GeoLocation coordinates;
    
    @JsonProperty("related_locations")
    private List<AdditionalGeoLocation> relatedLocations;
    
    @JsonProperty("parking_type")
    private String parkingType;
    
    private List<EVSE> evses;
    
    private List<DisplayText> directions;
    
    @JsonProperty("operator")
    private BusinessDetails operator;
    
    @JsonProperty("suboperator")
    private BusinessDetails suboperator;
    
    @JsonProperty("owner")
    private BusinessDetails owner;
    
    private List<Facility> facilities;
    
    @JsonProperty("time_zone")
    private String timeZone;
    
    @JsonProperty("opening_times")
    private Hours openingTimes;
    
    @JsonProperty("charging_when_closed")
    private Boolean chargingWhenClosed;
    
    private List<Image> images;
    
    @JsonProperty("energy_mix")
    private EnergyMix energyMix;
    
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private ZonedDateTime lastUpdated;
} 