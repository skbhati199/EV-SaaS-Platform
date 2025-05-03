package com.ev.userservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordChargingSessionRequest {
    
    @NotNull(message = "Session ID is required")
    private UUID sessionId;
    
    @NotNull(message = "Station ID is required")
    private UUID stationId;
    
    @NotNull(message = "Connector ID is required")
    private Integer connectorId;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @Positive(message = "Energy consumed must be positive")
    private BigDecimal energyConsumedKwh;
    
    @Positive(message = "Cost must be positive")
    private BigDecimal cost;
    
    private String currency;
} 