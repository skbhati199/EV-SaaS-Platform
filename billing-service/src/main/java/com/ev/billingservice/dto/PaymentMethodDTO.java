package com.ev.billingservice.dto;

import com.ev.billingservice.model.PaymentMethod.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    
    private UUID id;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Payment type is required")
    private PaymentType type;
    
    @NotBlank(message = "Provider is required")
    private String provider;
    
    private String tokenId;
    
    @Pattern(regexp = "^\\d{4}$", message = "Last four digits must be exactly 4 digits")
    private String lastFour;
    
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiry month must be between 01 and 12")
    private String expiryMonth;
    
    @Pattern(regexp = "^\\d{4}$", message = "Expiry year must be exactly 4 digits")
    private String expiryYear;
    
    private boolean isDefault;
} 