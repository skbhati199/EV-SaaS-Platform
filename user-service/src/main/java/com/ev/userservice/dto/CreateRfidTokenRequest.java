package com.ev.userservice.dto;

import com.ev.userservice.model.RfidToken;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRfidTokenRequest {
    
    @NotBlank(message = "Token value is required")
    private String tokenValue;
    
    @NotNull(message = "Token type is required")
    private RfidToken.TokenType tokenType;
    
    private LocalDateTime expiryDate;
} 