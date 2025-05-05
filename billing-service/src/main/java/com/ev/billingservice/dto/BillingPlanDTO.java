package com.ev.billingservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingPlanDTO {
    
    private UUID id;
    
    @NotBlank(message = "Plan name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly price must be greater than 0")
    private BigDecimal priceMonthly;
    
    @NotNull(message = "Yearly price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Yearly price must be greater than 0")
    private BigDecimal priceYearly;
    
    private String features;
    
    private boolean isActive;
    
    private String currency;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Energy rate must be non-negative")
    private BigDecimal energyRate;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Time rate must be non-negative")
    private BigDecimal timeRate;
}