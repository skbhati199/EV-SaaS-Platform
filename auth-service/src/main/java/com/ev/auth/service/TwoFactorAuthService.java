package com.ev.auth.service;

import com.ev.auth.dto.TwoFactorAuthRequest;

public interface TwoFactorAuthService {
    boolean validate(String username, String code);
    String generateSecret(String username);
    boolean enable(String username);
    boolean disable(String username);
    String getQrCodeUrl(String username, String secret);
    boolean verifyCode(TwoFactorAuthRequest request);
} 