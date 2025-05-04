package com.ev.userservice.service;

import com.ev.userservice.config.KafkaConfig;
import com.ev.userservice.dto.event.RfidTokenEvent;
import com.ev.userservice.dto.event.UserEvent;
import com.ev.userservice.dto.event.WalletEvent;
import com.ev.userservice.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for sending Kafka events related to users, wallets, and RFID tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send a user event to Kafka
     * @param event The user event to send
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendUserEvent(UserEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        
        log.info("Sending user event: type={}, userId={}, eventId={}", 
                event.getEventType(), event.getUserId(), event.getEventId());
        
        return kafkaTemplate.send(KafkaConfig.USER_EVENTS_TOPIC, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("User event sent successfully: {}", event.getEventId());
                    } else {
                        log.error("Failed to send user event: {}", event.getEventId(), ex);
                    }
                });
    }
    
    /**
     * Send a wallet event to Kafka
     * @param event The wallet event to send
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendWalletEvent(WalletEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        
        log.info("Sending wallet event: type={}, walletId={}, userId={}, eventId={}", 
                event.getEventType(), event.getWalletId(), event.getUserId(), event.getEventId());
        
        return kafkaTemplate.send(KafkaConfig.WALLET_EVENTS_TOPIC, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Wallet event sent successfully: {}", event.getEventId());
                    } else {
                        log.error("Failed to send wallet event: {}", event.getEventId(), ex);
                    }
                });
    }
    
    /**
     * Send an RFID token event to Kafka
     * @param event The RFID token event to send
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendRfidTokenEvent(RfidTokenEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        
        log.info("Sending RFID token event: type={}, rfidTokenId={}, userId={}, eventId={}", 
                event.getEventType(), event.getRfidTokenId(), event.getUserId(), event.getEventId());
        
        return kafkaTemplate.send(KafkaConfig.RFID_TOKEN_EVENTS_TOPIC, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("RFID token event sent successfully: {}", event.getEventId());
                    } else {
                        log.error("Failed to send RFID token event: {}", event.getEventId(), ex);
                    }
                });
    }
    
    /**
     * Create a user event from a User entity
     * @param user The user entity
     * @param eventType The type of event
     * @return The created UserEvent
     */
    public UserEvent createUserEvent(User user, UserEvent.UserEventType eventType) {
        return UserEvent.builder()
                .eventId(UUID.randomUUID())
                .userId(user.getId())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .build();
    }
} 