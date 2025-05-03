package com.ev.notificationservice.service;

import com.ev.notificationservice.config.KafkaConfig;
import com.ev.notificationservice.dto.NotificationEvent;
import com.ev.notificationservice.model.Notification;
import com.ev.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushService;
    
    /**
     * Listen for email notification events
     * @param event The notification event from Kafka
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_EMAIL_NOTIFICATIONS, groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeEmailNotifications(@Payload NotificationEvent event) {
        log.info("Received email notification event: {}", event);
        try {
            // Convert the event to a notification entity
            Notification notification = convertToNotification(event);
            
            // Process and send the email
            boolean sent = emailService.sendEmail(
                    event.getRecipient(),
                    event.getSubject(),
                    event.getContent(),
                    event.getTemplateId(),
                    event.getTemplateData()
            );
            
            // Update the notification status
            notification.setSent(sent);
            if (sent) {
                notification.setSentAt(LocalDateTime.now());
            }
            
            // Save the notification
            notificationRepository.save(notification);
            
            log.info("Email notification processed successfully: {}", event.getSubject());
        } catch (Exception ex) {
            log.error("Error processing email notification: {}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Listen for SMS notification events
     * @param event The notification event from Kafka
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_SMS_NOTIFICATIONS, groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeSmsNotifications(@Payload NotificationEvent event) {
        log.info("Received SMS notification event: {}", event);
        try {
            // Convert the event to a notification entity
            Notification notification = convertToNotification(event);
            
            // Process and send the SMS
            boolean sent = smsService.sendSms(
                    event.getRecipient(),
                    event.getContent()
            );
            
            // Update the notification status
            notification.setSent(sent);
            if (sent) {
                notification.setSentAt(LocalDateTime.now());
            }
            
            // Save the notification
            notificationRepository.save(notification);
            
            log.info("SMS notification processed successfully");
        } catch (Exception ex) {
            log.error("Error processing SMS notification: {}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Listen for push notification events
     * @param event The notification event from Kafka
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_PUSH_NOTIFICATIONS, groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumePushNotifications(@Payload NotificationEvent event) {
        log.info("Received push notification event: {}", event);
        try {
            // Convert the event to a notification entity
            Notification notification = convertToNotification(event);
            
            // Process and send the push notification
            boolean sent = pushService.sendPushNotification(
                    event.getRecipient(),
                    event.getSubject(),
                    event.getContent()
            );
            
            // Update the notification status
            notification.setSent(sent);
            if (sent) {
                notification.setSentAt(LocalDateTime.now());
            }
            
            // Save the notification
            notificationRepository.save(notification);
            
            log.info("Push notification processed successfully: {}", event.getSubject());
        } catch (Exception ex) {
            log.error("Error processing push notification: {}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Convert a NotificationEvent to a Notification entity
     * @param event The notification event
     * @return The notification entity
     */
    private Notification convertToNotification(NotificationEvent event) {
        return Notification.builder()
                .userId(event.getUserId())
                .type(event.getType())
                .subject(event.getSubject())
                .content(event.getContent())
                .channel(event.getChannel())
                .recipient(event.getRecipient())
                .sent(false)
                .read(false)
                .createdAt(LocalDateTime.now())
                .relatedEntityId(event.getRelatedEntityId())
                .relatedEntityType(event.getRelatedEntityType())
                .build();
    }
} 