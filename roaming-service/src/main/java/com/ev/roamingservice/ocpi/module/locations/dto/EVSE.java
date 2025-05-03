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
 * OCPI EVSE DTO representing an Electric Vehicle Supply Equipment
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EVSE {
    
    private String uid;
    
    @JsonProperty("evse_id")
    private String evseId;
    
    private String status;
    
    @JsonProperty("status_schedule")
    private List<StatusSchedule> statusSchedule;
    
    private List<Capability> capabilities;
    
    private List<Connector> connectors;
    
    @JsonProperty("floor_level")
    private String floorLevel;
    
    private GeoLocation coordinates;
    
    @JsonProperty("physical_reference")
    private String physicalReference;
    
    private List<DisplayText> directions;
    
    @JsonProperty("parking_restrictions")
    private List<ParkingRestriction> parkingRestrictions;
    
    private List<Image> images;
    
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private ZonedDateTime lastUpdated;
} 