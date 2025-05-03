package com.ev.billingservice.dto;

import jakarta.validation.constraints.Email;
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
public class BillingSettingsDTO {
    
    private UUID id;
    
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
    
    @Email(message = "Must be a valid email address")
    private String billingEmail;
    
    private String taxId;
    
    private String billingAddress;
    
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter currency code")
    private String currency;
} 