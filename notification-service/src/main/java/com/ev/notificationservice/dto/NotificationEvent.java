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
    
    private UUID id;
    private UUID userId;
    private String type; // USER_VERIFICATION, PAYMENT_CONFIRMATION, CHARGING_STARTED, etc.
    private String subject;
    private String content;
    private String channel; // email, sms, push, in-app
    private String recipient; // email address, phone number, device token
    private String templateId; // For email templates
    private Map<String, Object> templateData; // For email templates
    private LocalDateTime timestamp;
    private UUID relatedEntityId; // e.g., charging session ID, invoice ID
    private String relatedEntityType; // e.g., ChargingSession, Invoice
    
    // Factory methods for different notification types
    public static NotificationEvent createEmailEvent(UUID userId, String recipient, String subject, String content) {
        return NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("EMAIL")
                .subject(subject)
                .content(content)
                .channel("email")
                .recipient(recipient)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static NotificationEvent createEmailTemplateEvent(UUID userId, String recipient, String subject, 
                                                           String templateId, Map<String, Object> templateData) {
        return NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("EMAIL_TEMPLATE")
                .subject(subject)
                .channel("email")
                .recipient(recipient)
                .templateId(templateId)
                .templateData(templateData)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static NotificationEvent createSmsEvent(UUID userId, String recipient, String content) {
        return NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("SMS")
                .content(content)
                .channel("sms")
                .recipient(recipient)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static NotificationEvent createPushEvent(UUID userId, String recipient, String subject, String content) {
        return NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("PUSH")
                .subject(subject)
                .content(content)
                .channel("push")
                .recipient(recipient)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 