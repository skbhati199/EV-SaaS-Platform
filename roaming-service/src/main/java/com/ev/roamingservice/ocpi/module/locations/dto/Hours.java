package com.ev.roamingservice.ocpi.module.locations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OCPI Hours DTO representing opening hours
 * Based on OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hours {
    
    @JsonProperty("regular_hours")
    private List<RegularHours> regularHours;
    
    @JsonProperty("exceptional_openings")
    private List<ExceptionalPeriod> exceptionalOpenings;
    
    @JsonProperty("exceptional_closings")
    private List<ExceptionalPeriod> exceptionalClosings;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegularHours {
        
        @JsonProperty("weekday")
        private Integer weekday;
        
        @JsonProperty("period_begin")
        private String periodBegin;
        
        @JsonProperty("period_end")
        private String periodEnd;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExceptionalPeriod {
        
        @JsonProperty("period_begin")
        private String periodBegin;
        
        @JsonProperty("period_end")
        private String periodEnd;
    }
} 