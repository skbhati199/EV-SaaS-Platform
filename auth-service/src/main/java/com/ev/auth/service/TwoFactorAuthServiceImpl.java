package com.ev.auth.service;

import com.ev.auth.dto.TwoFactorAuthRequest;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {
    
    private final UserRepository userRepository;
    
    public TwoFactorAuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean validate(String username, String code) {
        // This is a placeholder implementation
        // In a real app, use TOTP algorithm to validate the code
        return true;
    }
    
    @Override
    public String generateSecret(String username) {
        // Generate a random secret key
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        String secret = Base64.getEncoder().encodeToString(bytes);
        
        return secret;
    }
    
    @Override
    public boolean enable(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
        return true;
    }
    
    @Override
    public boolean disable(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(false);
        userRepository.save(user);
        return true;
    }
    
    @Override
    public String getQrCodeUrl(String username, String secret) {
        // Format: otpauth://totp/Label?secret=JBSWY3DPEHPK3PXP&issuer=Issuer
        return "otpauth://totp/EVSaaS:" + username + "?secret=" + secret + "&issuer=EVSaaS";
    }
    
    @Override
    public boolean verifyCode(TwoFactorAuthRequest request) {
        return validate(request.getUsername(), request.getCode());
    }
} 