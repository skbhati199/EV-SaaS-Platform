package com.ev.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorVerifyRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Secret is required")
    private String secret;
    
    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Code must be a 6-digit number")
    private String code;
} 