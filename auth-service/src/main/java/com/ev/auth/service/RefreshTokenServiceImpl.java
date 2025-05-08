package com.ev.auth.service;

import com.ev.auth.exception.TokenRefreshException;
import com.ev.auth.model.RefreshToken;
import com.ev.auth.model.User;
import com.ev.auth.repository.RefreshTokenRepository;
import com.ev.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;
    
    @Override
    @Transactional
    public String createRefreshToken(String userId) {
        log.info("Creating refresh token for user ID: {}", userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Generate JWT refresh token
        String jwtRefreshToken = jwtService.generateRefreshToken(user);
        
        // Store the token in the database
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(jwtRefreshToken)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created successfully for user ID: {}", userId);
        
        return jwtRefreshToken;
    }
    
    @Override
    @Transactional
    public String validateRefreshToken(String token) {
        log.info("Validating refresh token");
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found in database"));
        
        // Check if token is expired
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException(token, "Refresh token was expired");
        }
        
        // Check if token is revoked
        if (refreshToken.isRevoked()) {
            throw new TokenRefreshException(token, "Refresh token was revoked");
        }
        
        // Also validate with JWT service to ensure token signature is valid
        try {
            UUID userId = jwtService.extractUserId(token);
            return userId.toString();
        } catch (Exception e) {
            throw new TokenRefreshException(token, "Invalid JWT refresh token");
        }
    }
    
    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        log.info("Revoking refresh token");
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException(token, "Refresh token not found in database"));
        
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        
        log.info("Refresh token revoked successfully");
    }
    
    @Override
    @Transactional
    public void revokeAllUserRefreshTokens(String userId) {
        log.info("Revoking all refresh tokens for user ID: {}", userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        refreshTokenRepository.findAllByUser(user)
                .forEach(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
        
        log.info("All refresh tokens revoked for user ID: {}", userId);
    }
} 