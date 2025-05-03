package com.ev.notificationservice.service;

import com.ev.notificationservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service for sending notifications via Kafka events
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventNotificationService {

    private final KafkaProducerService kafkaProducerService;
    
    /**
     * Send a notification via Kafka
     * @param event The notification event to send
     * @return The ID of the created notification
     */
    public UUID sendNotification(NotificationEvent event) {
        log.info("Sending notification via Kafka: {}", event);
        kafkaProducerService.sendNotificationEvent(event);
        
        // We don't need to save the notification here, as it will be saved
        // by the Kafka consumer when it processes the event
        return null;
    }
    
    /**
     * Send an email notification
     * @param userId User ID
     * @param email Email address
     * @param subject Email subject
     * @param content Email content
     * @param type Notification type
     * @return The ID of the created notification
     */
    public UUID sendEmailNotification(UUID userId, String email, String subject, String content, String type) {
        NotificationEvent event = NotificationEvent.createEmailEvent(userId, subject, content, email, type);
        return sendNotification(event);
    }
    
    /**
     * Send an email notification with a template
     * @param userId User ID
     * @param email Email address
     * @param subject Email subject
     * @param templateId Template ID
     * @param templateData Template data
     * @param type Notification type
     * @return The ID of the created notification
     */
    public UUID sendTemplatedEmailNotification(UUID userId, String email, String subject, 
                                               String templateId, Map<String, Object> templateData, String type) {
        NotificationEvent event = NotificationEvent.builder()
                .userId(userId)
                .type(type)
                .subject(subject)
                .templateId(templateId)
                .templateData(templateData)
                .channel("EMAIL")
                .recipient(email)
                .timestamp(LocalDateTime.now())
                .build();
                
        return sendNotification(event);
    }
    
    /**
     * Send an SMS notification
     * @param userId User ID
     * @param phoneNumber Phone number
     * @param message SMS message
     * @param type Notification type
     * @return The ID of the created notification
     */
    public UUID sendSmsNotification(UUID userId, String phoneNumber, String message, String type) {
        NotificationEvent event = NotificationEvent.createSmsEvent(userId, message, phoneNumber, type);
        return sendNotification(event);
    }
    
    /**
     * Send a push notification
     * @param userId User ID
     * @param deviceToken Device token
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @return The ID of the created notification
     */
    public UUID sendPushNotification(UUID userId, String deviceToken, String title, String message, String type) {
        NotificationEvent event = NotificationEvent.createPushEvent(userId, title, message, deviceToken, type);
        return sendNotification(event);
    }
} 