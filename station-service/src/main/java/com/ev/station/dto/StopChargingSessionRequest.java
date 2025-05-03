package com.ev.station.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopChargingSessionRequest {
    
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    @NotNull(message = "Meter stop is required")
    @Positive(message = "Meter stop must be positive")
    private Integer meterStop;
    
    private String stopReason;
    
    private BigDecimal totalEnergyKwh;
    
    private BigDecimal totalCost;
    
    private String currency;
} 