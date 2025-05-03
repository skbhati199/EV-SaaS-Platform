package com.ev.roamingservice.ocpi.module.credentials.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Image object as per OCPI 2.2 specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Image {
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("thumbnail")
    private String thumbnail;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("width")
    private Integer width;
    
    @JsonProperty("height")
    private Integer height;
} 