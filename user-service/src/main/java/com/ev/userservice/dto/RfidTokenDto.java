package com.ev.userservice.dto;

import com.ev.userservice.model.RfidToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfidTokenDto {
    private UUID id;
    private UUID userId;
    private String tokenValue;
    private RfidToken.TokenType tokenType;
    private boolean isActive;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
} 