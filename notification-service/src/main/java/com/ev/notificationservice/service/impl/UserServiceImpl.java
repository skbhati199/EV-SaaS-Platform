package com.ev.notificationservice.service.impl;

import com.ev.notificationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Implementation of the UserService interface.
 * This service acts as a client to the User Service microservice.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserServiceImpl(RestTemplate restTemplate, 
                           @Value("${services.user-service.url:http://user-service:8080}") String userServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    @Override
    public String getUserDetailsById(UUID userId) {
        log.info("Getting user details for user ID: {}", userId);
        try {
            // In a real implementation, this would call the user service API
            // return restTemplate.getForObject(userServiceBaseUrl + "/api/v1/users/{id}", String.class, userId);
            
            // For now, return a stub response
            return "User " + userId;
        } catch (Exception e) {
            log.error("Error getting user details: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getUserEmailById(UUID userId) {
        log.info("Getting user email for user ID: {}", userId);
        try {
            // In a real implementation, this would call the user service API
            // return restTemplate.getForObject(userServiceBaseUrl + "/api/v1/users/{id}/email", String.class, userId);
            
            // For now, return a stub response
            return userId + "@example.com";
        } catch (Exception e) {
            log.error("Error getting user email: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getUserPhoneById(UUID userId) {
        log.info("Getting user phone for user ID: {}", userId);
        try {
            // In a real implementation, this would call the user service API
            // return restTemplate.getForObject(userServiceBaseUrl + "/api/v1/users/{id}/phone", String.class, userId);
            
            // For now, return a stub response
            return "+1234567890";
        } catch (Exception e) {
            log.error("Error getting user phone: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean userExists(UUID userId) {
        log.info("Checking if user exists for user ID: {}", userId);
        try {
            // In a real implementation, this would call the user service API
            // return restTemplate.getForObject(userServiceBaseUrl + "/api/v1/users/{id}/exists", Boolean.class, userId);
            
            // For now, return a stub response
            return userId != null;
        } catch (Exception e) {
            log.error("Error checking if user exists: {}", e.getMessage(), e);
            return false;
        }
    }
} 