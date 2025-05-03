package com.ev.auth.service;

import com.ev.auth.dto.TwoFactorAuthRequest;
import com.ev.auth.model.TwoFactorAuth;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.time.LocalDateTime;

@Service
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {
    
    private final UserRepository userRepository;
    
    public TwoFactorAuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public boolean validateCode(String secretKey, String code) {
        // This is a placeholder implementation
        // In a real app, use TOTP algorithm to validate the code based on the secretKey
        return true;
    }
    
    @Override
    public boolean validate(String username, String code) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        if (user.getTwoFactorAuth() == null || !user.getTwoFactorAuth().isEnabled()) {
            return true; // 2FA not enabled, so validation passes
        }
        
        // Validate the 2FA code
        return validateCode(user.getTwoFactorAuth().getSecretKey(), code);
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
        
        // Create or update the TwoFactorAuth entity
        TwoFactorAuth twoFactorAuth = user.getTwoFactorAuth();
        if (twoFactorAuth == null) {
            twoFactorAuth = new TwoFactorAuth();
            twoFactorAuth.setUser(user);
            twoFactorAuth.setSecretKey(generateSecret(username));
            twoFactorAuth.setCreatedAt(LocalDateTime.now());
        }
        
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setUpdatedAt(LocalDateTime.now());
        
        user.setTwoFactorAuth(twoFactorAuth);
        userRepository.save(user);
        return true;
    }
    
    @Override
    public boolean disable(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TwoFactorAuth twoFactorAuth = user.getTwoFactorAuth();
        if (twoFactorAuth != null) {
            twoFactorAuth.setEnabled(false);
            twoFactorAuth.setUpdatedAt(LocalDateTime.now());
            user.setTwoFactorAuth(twoFactorAuth);
            userRepository.save(user);
        }
        
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