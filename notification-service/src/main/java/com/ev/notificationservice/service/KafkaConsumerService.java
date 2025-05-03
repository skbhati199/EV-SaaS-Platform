package com.ev.notificationservice.service;

import com.ev.notificationservice.config.KafkaConfig;
import com.ev.notificationservice.dto.NotificationEvent;
import com.ev.notificationservice.model.Notification;
import com.ev.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushNotificationService;

    /**
     * Consume email notification events
     */
    @KafkaListener(topics = "#{kafkaConfig.EMAIL_NOTIFICATIONS_TOPIC}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeEmailNotification(NotificationEvent event) {
        log.info("Received email notification event: {}", event);
        
        try {
            String to = event.getRecipient();
            String subject = event.getSubject();
            String content = event.getContent();
            String templateId = event.getTemplateId();
            var templateData = event.getTemplateData();
            
            Notification notification = createNotificationFromEvent(event);
            notification.setSent(false);
            
            notification = notificationRepository.save(notification);
            
            boolean success = emailService.sendEmail(to, subject, content, templateId, templateData);
            
            if (success) {
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                
                log.info("Email notification sent successfully: {}", subject);
            } else {
                log.error("Failed to send email notification");
            }
        } catch (Exception e) {
            log.error("Error processing email notification", e);
        }
    }
    
    /**
     * Consume SMS notification events
     */
    @KafkaListener(topics = "#{kafkaConfig.SMS_NOTIFICATIONS_TOPIC}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeSmsNotification(NotificationEvent event) {
        log.info("Received SMS notification event");
        
        try {
            String to = event.getRecipient();
            String content = event.getContent();
            
            Notification notification = createNotificationFromEvent(event);
            notification.setSent(false);
            
            notification = notificationRepository.save(notification);
            
            boolean success = smsService.sendSms(to, content);
            
            if (success) {
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                
                log.info("SMS notification sent successfully");
            } else {
                log.error("Failed to send SMS notification");
            }
        } catch (Exception e) {
            log.error("Error processing SMS notification", e);
        }
    }
    
    /**
     * Consume push notification events
     */
    @KafkaListener(topics = "#{kafkaConfig.PUSH_NOTIFICATIONS_TOPIC}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumePushNotification(NotificationEvent event) {
        log.info("Received push notification event");
        
        try {
            String to = event.getRecipient();
            String subject = event.getSubject();
            String content = event.getContent();
            
            Notification notification = createNotificationFromEvent(event);
            notification.setSent(false);
            
            notification = notificationRepository.save(notification);
            
            boolean success = pushNotificationService.sendPushNotification(to, subject, content);
            
            if (success) {
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                
                log.info("Push notification sent successfully: {}", subject);
            } else {
                log.error("Failed to send push notification");
            }
        } catch (Exception e) {
            log.error("Error processing push notification", e);
        }
    }
    
    private Notification createNotificationFromEvent(NotificationEvent event) {
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