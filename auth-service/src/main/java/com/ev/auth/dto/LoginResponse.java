package com.ev.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
    private boolean requiresTwoFactor;
} 