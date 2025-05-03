package com.ev.billingservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UsageRecordDTO {
    
    private UUID id;
    
    @NotNull(message = "Subscription ID is required")
    private UUID subscriptionId;
    
    @NotBlank(message = "Meter type is required")
    private String meterType;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
    private BigDecimal quantity;
    
    private LocalDateTime timestamp;
    
    private boolean processed;
} 