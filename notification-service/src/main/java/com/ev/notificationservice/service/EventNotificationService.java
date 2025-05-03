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
        log.info("Sending notification: channel={}, recipient={}", event.getChannel(), event.getRecipient());
        return kafkaProducerService.sendNotificationEvent(event);
    }
    
    /**
     * Send an email notification
     * @param userId The ID of the user
     * @param email The recipient's email address
     * @param subject The email subject
     * @param content The email content
     * @return The ID of the created notification
     */
    public UUID sendEmailNotification(UUID userId, String email, String subject, String content) {
        NotificationEvent event = NotificationEvent.createEmailEvent(userId, email, subject, content);
        return sendNotification(event);
    }
    
    /**
     * Send an email notification with a template
     * @param userId The ID of the user
     * @param email The recipient's email address
     * @param subject The email subject
     * @param templateId The template ID
     * @param templateData The template data
     * @return The ID of the created notification
     */
    public UUID sendTemplatedEmailNotification(UUID userId, String email, String subject, 
                                             String templateId, Map<String, Object> templateData) {
        NotificationEvent event = NotificationEvent.createEmailTemplateEvent(
                userId, email, subject, templateId, templateData);
        return sendNotification(event);
    }
    
    /**
     * Send an SMS notification
     * @param userId The ID of the user
     * @param phoneNumber The recipient's phone number
     * @param message The SMS message
     * @return The ID of the created notification
     */
    public UUID sendSmsNotification(UUID userId, String phoneNumber, String message) {
        NotificationEvent event = NotificationEvent.createSmsEvent(userId, phoneNumber, message);
        return sendNotification(event);
    }
    
    /**
     * Send a push notification
     * @param userId The ID of the user
     * @param deviceToken The recipient's device token
     * @param title The notification title
     * @param message The notification message
     * @return The ID of the created notification
     */
    public UUID sendPushNotification(UUID userId, String deviceToken, String title, String message) {
        NotificationEvent event = NotificationEvent.createPushEvent(userId, deviceToken, title, message);
        return sendNotification(event);
    }
} 