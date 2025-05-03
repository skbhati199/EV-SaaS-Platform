package com.ev.billingservice.service.impl;

import com.ev.billingservice.client.UserClient;
import com.ev.billingservice.service.UserService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserClient userClient;
    
    @Override
    public String getUserEmailById(UUID userId) {
        try {
            return userClient.getUserEmailById(userId);
        } catch (FeignException e) {
            log.error("Failed to get user email for user ID: {}, Status: {}", userId, e.status(), e);
            return null;
        }
    }
} 