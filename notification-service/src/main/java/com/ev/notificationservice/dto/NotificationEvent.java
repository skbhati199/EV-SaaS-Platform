package com.ev.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    
    private UUID userId;
    private String type; // USER_VERIFICATION, PAYMENT_CONFIRMATION, CHARGING_STARTED, etc.
    private String subject;
    private String content;
    private String templateId; // Optional, for templated notifications
    private String channel; // EMAIL, SMS, PUSH, WEBHOOK
    private String recipient; // email, phone number, etc.
    private Map<String, Object> templateData; // For template variables
    private UUID relatedEntityId; // e.g., charging session ID, invoice ID
    private String relatedEntityType; // e.g., ChargingSession, Invoice
    private LocalDateTime timestamp;
    
    // Helper methods to create specific notification types
    public static NotificationEvent createEmailEvent(UUID userId, String subject, String content, 
                                                    String recipient, String type) {
        return NotificationEvent.builder()
                .userId(userId)
                .type(type)
                .subject(subject)
                .content(content)
                .channel("EMAIL")
                .recipient(recipient)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static NotificationEvent createSmsEvent(UUID userId, String content, 
                                                  String phoneNumber, String type) {
        return NotificationEvent.builder()
                .userId(userId)
                .type(type)
                .content(content)
                .channel("SMS")
                .recipient(phoneNumber)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static NotificationEvent createPushEvent(UUID userId, String subject, String content, 
                                                   String deviceToken, String type) {
        return NotificationEvent.builder()
                .userId(userId)
                .type(type)
                .subject(subject)
                .content(content)
                .channel("PUSH")
                .recipient(deviceToken)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static NotificationEvent createTemplatedEvent(UUID userId, String templateId, 
                                                        Map<String, Object> templateData, 
                                                        String channel, String recipient, String type) {
        return NotificationEvent.builder()
                .userId(userId)
                .type(type)
                .templateId(templateId)
                .templateData(templateData)
                .channel(channel)
                .recipient(recipient)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 