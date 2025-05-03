package com.ev.station.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartChargingSessionRequest {
    
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    private String idTag;
    
    private UUID userId;
    
    @NotNull(message = "Connector ID is required")
    @Positive(message = "Connector ID must be positive")
    private Integer connectorId;
    
    @NotNull(message = "Meter start is required")
    @Positive(message = "Meter start must be positive")
    private Integer meterStart;
    
    private String startReason;
} 