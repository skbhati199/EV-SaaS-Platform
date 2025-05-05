package com.ev.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorVerifyRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Secret is required")
    private String secret;
} 