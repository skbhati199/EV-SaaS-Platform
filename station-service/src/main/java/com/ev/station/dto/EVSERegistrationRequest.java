package com.ev.station.dto;

import com.ev.station.model.ConnectorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EVSERegistrationRequest {
    
    @NotBlank(message = "EVSE ID is required")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "EVSE ID must contain only alphanumeric characters, hyphens, and underscores")
    private String evseId;
    
    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    @NotNull(message = "Maximum power is required")
    @Positive(message = "Maximum power must be positive")
    private Double maxPower;
    
    @NotNull(message = "Connector type is required")
    private ConnectorType connectorType;
    
    @NotBlank(message = "Firmware version is required")
    private String firmwareVersion;
}
