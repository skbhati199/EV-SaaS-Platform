package com.ev.roamingservice.service;

import com.ev.roamingservice.dto.event.TokenEvent;
import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiToken;
import com.ev.roamingservice.model.OcpiTokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for managing OCPI tokens and publishing token events to Kafka
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final KafkaProducerService kafkaProducerService;
    
    /**
     * Create a new token for a party
     * 
     * @param party The OCPI party
     * @param tokenType The type of token
     * @param validityPeriodHours Hours until the token expires
     * @return The created token
     */
    @Transactional
    public OcpiToken createToken(OcpiParty party, OcpiTokenType tokenType, int validityPeriodHours) {
        // Generate a random token
        String tokenValue = UUID.randomUUID().toString();
        
        // Create token entity
        OcpiToken token = OcpiToken.builder()
                .token(tokenValue)
                .tokenType(tokenType)
                .party(party)
                .validUntil(LocalDateTime.now().plusHours(validityPeriodHours))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        // In a real implementation, save the token to the database here
        
        // Publish token creation event
        try {
            TokenEvent event = kafkaProducerService.createTokenEvent(token, TokenEvent.TokenEventType.CREATED);
            
            kafkaProducerService.sendTokenEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send token creation event: {}", exception.getMessage());
                        } else {
                            log.debug("Token creation event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing token creation event: {}", e.getMessage(), e);
        }
        
        return token;
    }
    
    /**
     * Validate a token
     * 
     * @param tokenValue The token value to validate
     * @return true if the token is valid, false otherwise
     */
    @Transactional
    public boolean validateToken(String tokenValue) {
        // In a real implementation, look up the token in the database and check:
        // 1. The token exists
        // 2. It hasn't expired (validUntil > now)
        // 3. It hasn't been revoked
        
        // This is a simplified implementation
        boolean isValid = true;
        
        // Create a mock token for the event
        OcpiToken token = OcpiToken.builder()
                .token(tokenValue)
                .tokenType(OcpiTokenType.C)
                .validUntil(LocalDateTime.now().plusHours(24))
                .revoked(false)
                .build();
        
        // Publish token validation event
        try {
            TokenEvent event = kafkaProducerService.createTokenEvent(token, TokenEvent.TokenEventType.VALIDATED);
            
            kafkaProducerService.sendTokenEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send token validation event: {}", exception.getMessage());
                        } else {
                            log.debug("Token validation event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing token validation event: {}", e.getMessage(), e);
        }
        
        return isValid;
    }
    
    /**
     * Revoke a token
     * 
     * @param token The token to revoke
     */
    @Transactional
    public void revokeToken(OcpiToken token) {
        // In a real implementation, update the token in the database
        token.setRevoked(true);
        token.setUpdatedAt(LocalDateTime.now());
        
        // Publish token revocation event
        try {
            TokenEvent event = kafkaProducerService.createTokenEvent(token, TokenEvent.TokenEventType.REVOKED);
            
            kafkaProducerService.sendTokenEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send token revocation event: {}", exception.getMessage());
                        } else {
                            log.debug("Token revocation event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing token revocation event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Refresh a token, extending its validity period
     * 
     * @param token The token to refresh
     * @param validityPeriodHours New hours until the token expires
     * @return The updated token
     */
    @Transactional
    public OcpiToken refreshToken(OcpiToken token, int validityPeriodHours) {
        // In a real implementation, update the token in the database
        token.setValidUntil(LocalDateTime.now().plusHours(validityPeriodHours));
        token.setUpdatedAt(LocalDateTime.now());
        
        // Publish token update event
        try {
            TokenEvent event = kafkaProducerService.createTokenEvent(token, TokenEvent.TokenEventType.UPDATED);
            
            kafkaProducerService.sendTokenEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send token update event: {}", exception.getMessage());
                        } else {
                            log.debug("Token update event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing token update event: {}", e.getMessage(), e);
        }
        
        return token;
    }
} 