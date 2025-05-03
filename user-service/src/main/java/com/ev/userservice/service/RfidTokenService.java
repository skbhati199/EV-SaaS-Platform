package com.ev.userservice.service;

import com.ev.userservice.dto.CreateRfidTokenRequest;
import com.ev.userservice.dto.RfidTokenDto;

import java.util.List;
import java.util.UUID;

public interface RfidTokenService {
    List<RfidTokenDto> getAllTokensByUserId(UUID userId);
    RfidTokenDto getTokenById(UUID id);
    RfidTokenDto getTokenByValue(String tokenValue);
    RfidTokenDto createToken(UUID userId, CreateRfidTokenRequest request);
    RfidTokenDto activateToken(UUID id);
    RfidTokenDto deactivateToken(UUID id);
    void deleteToken(UUID id);
    boolean isTokenValid(String tokenValue);
} 