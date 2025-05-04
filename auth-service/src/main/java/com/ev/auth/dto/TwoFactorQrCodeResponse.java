package com.ev.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorQrCodeResponse {
    
    private String secret;
    private String qrCodeImage;
} 