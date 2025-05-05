package com.ev.apigateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling WebSocket messages
 * Temporarily disabled to resolve conflict with Spring Cloud Gateway
 */
// @Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Client subscription handler - users can subscribe to specific event types
     */
    @MessageMapping("/subscribe")
    public void handleSubscription(@Payload Map<String, Object> payload, 
                                  SimpMessageHeaderAccessor headerAccessor) {
        String eventType = (String) payload.get("eventType");
        String userId = (String) payload.get("userId");
        
        if (eventType == null || eventType.isEmpty()) {
            log.warn("Received subscription request with empty eventType");
            return;
        }
        
        // Store subscription info in session if needed
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("SUBSCRIBED_EVENT_TYPE", eventType);
            if (userId != null) {
                headerAccessor.getSessionAttributes().put("USER_ID", userId);
            }
        }
        
        log.info("User subscribed to event type: {}, userId: {}", eventType, userId);
        
        // Send confirmation to client
        Map<String, Object> confirmation = new HashMap<>();
        confirmation.put("status", "SUBSCRIBED");
        confirmation.put("eventType", eventType);
        
        messagingTemplate.convertAndSend("/topic/subscription/" + headerAccessor.getSessionId(), confirmation);
    }

    /**
     * Handles ping messages from clients to keep connection alive
     */
    @MessageMapping("/ping")
    public void handlePing(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> pong = new HashMap<>();
        pong.put("type", "PONG");
        pong.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/pong/" + headerAccessor.getSessionId(), pong);
    }
    
    /**
     * Test endpoint for sending test messages to clients
     */
    @MessageMapping("/test")
    public void sendTestMessage(@Payload Map<String, Object> payload) {
        String destination = (String) payload.getOrDefault("destination", "all");
        
        Map<String, Object> testMessage = new HashMap<>();
        testMessage.put("type", "TEST");
        testMessage.put("content", payload.getOrDefault("content", "Test message"));
        testMessage.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/test/" + destination, testMessage);
        log.info("Sent test message to destination: {}", destination);
    }
} 