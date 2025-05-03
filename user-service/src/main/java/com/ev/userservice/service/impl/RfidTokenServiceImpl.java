package com.ev.userservice.service.impl;

import com.ev.userservice.dto.CreateRfidTokenRequest;
import com.ev.userservice.dto.RfidTokenDto;
import com.ev.userservice.model.RfidToken;
import com.ev.userservice.model.User;
import com.ev.userservice.repository.RfidTokenRepository;
import com.ev.userservice.repository.UserRepository;
import com.ev.userservice.service.RfidTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RfidTokenServiceImpl implements RfidTokenService {

    private final RfidTokenRepository rfidTokenRepository;
    private final UserRepository userRepository;

    @Override
    public List<RfidTokenDto> getAllTokensByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        return rfidTokenRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RfidTokenDto getTokenById(UUID id) {
        return rfidTokenRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("RFID token not found with id: " + id));
    }

    @Override
    public RfidTokenDto getTokenByValue(String tokenValue) {
        return rfidTokenRepository.findByTokenValue(tokenValue)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("RFID token not found with value: " + tokenValue));
    }

    @Override
    @Transactional
    public RfidTokenDto createToken(UUID userId, CreateRfidTokenRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        if (rfidTokenRepository.existsByTokenValue(request.getTokenValue())) {
            throw new IllegalArgumentException("Token value already in use: " + request.getTokenValue());
        }
        
        RfidToken token = RfidToken.builder()
                .user(user)
                .tokenValue(request.getTokenValue())
                .tokenType(request.getTokenType())
                .isActive(true)
                .expiryDate(request.getExpiryDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return mapToDto(rfidTokenRepository.save(token));
    }

    @Override
    @Transactional
    public RfidTokenDto activateToken(UUID id) {
        RfidToken token = rfidTokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RFID token not found with id: " + id));
        
        token.setActive(true);
        token.setUpdatedAt(LocalDateTime.now());
        
        return mapToDto(rfidTokenRepository.save(token));
    }

    @Override
    @Transactional
    public RfidTokenDto deactivateToken(UUID id) {
        RfidToken token = rfidTokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RFID token not found with id: " + id));
        
        token.setActive(false);
        token.setUpdatedAt(LocalDateTime.now());
        
        return mapToDto(rfidTokenRepository.save(token));
    }

    @Override
    @Transactional
    public void deleteToken(UUID id) {
        if (!rfidTokenRepository.existsById(id)) {
            throw new EntityNotFoundException("RFID token not found with id: " + id);
        }
        
        rfidTokenRepository.deleteById(id);
    }

    @Override
    public boolean isTokenValid(String tokenValue) {
        return rfidTokenRepository.findByTokenValue(tokenValue)
                .map(token -> {
                    // Check if token is active
                    if (!token.isActive()) {
                        return false;
                    }
                    
                    // Check if token has expired
                    if (token.getExpiryDate() != null && token.getExpiryDate().isBefore(LocalDateTime.now())) {
                        return false;
                    }
                    
                    // Check if user is enabled
                    return token.getUser().isEnabled();
                })
                .orElse(false);
    }
    
    private RfidTokenDto mapToDto(RfidToken token) {
        return RfidTokenDto.builder()
                .id(token.getId())
                .userId(token.getUser().getId())
                .tokenValue(token.getTokenValue())
                .tokenType(token.getTokenType())
                .isActive(token.isActive())
                .expiryDate(token.getExpiryDate())
                .createdAt(token.getCreatedAt())
                .build();
    }
} 