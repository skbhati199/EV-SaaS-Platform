package com.ev.station.dto;

import com.ev.station.model.ConnectorType;
import com.ev.station.model.PowerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConnectorRequest {
    
    @NotNull(message = "Connector ID is required")
    @Positive(message = "Connector ID must be positive")
    private Integer connectorId;
    
    @NotNull(message = "Connector type is required")
    private ConnectorType connectorType;
    
    @NotNull(message = "Power type is required")
    private PowerType powerType;
    
    @Min(value = 0, message = "Max voltage must be non-negative")
    private Integer maxVoltage;
    
    @Min(value = 0, message = "Max amperage must be non-negative")
    private Integer maxAmperage;
    
    @Min(value = 0, message = "Max power must be non-negative")
    private Double maxPowerKw;
} 