package com.ev.notificationservice.service.impl;

import com.ev.notificationservice.dto.NotificationDTO;
import com.ev.notificationservice.model.Notification;
import com.ev.notificationservice.repository.NotificationRepository;
import com.ev.notificationservice.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = mapToEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return mapToDTO(notification);
    }
    
    @Override
    @Transactional
    public NotificationDTO getNotificationById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
        return mapToDTO(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserId(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnsentNotificationsByUserId(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndSentFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByUserId(UUID userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByRelatedEntity(UUID relatedEntityId, String relatedEntityType) {
        List<Notification> notifications = notificationRepository.findByRelatedEntityIdAndRelatedEntityType(
                relatedEntityId, relatedEntityType);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Notification> notifications = notificationRepository.findByCreatedAtBetween(start, end);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByType(String type) {
        List<Notification> notifications = notificationRepository.findByType(type);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserIdAndType(UUID userId, String type) {
        List<Notification> notifications = notificationRepository.findByUserIdAndType(userId, type);
        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public NotificationDTO markAsSent(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
        
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        
        notification = notificationRepository.save(notification);
        return mapToDTO(notification);
    }
    
    @Override
    @Transactional
    public NotificationDTO markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
        
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        notification = notificationRepository.save(notification);
        return mapToDTO(notification);
    }
    
    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${notification.send.interval:30000}")
    public void sendNotifications() {
        List<Notification> unsentNotifications = notificationRepository.findBySentFalse();
        
        for (Notification notification : unsentNotifications) {
            try {
                // Here we would implement the actual sending logic based on channel
                // For example, email, SMS, push notifications
                boolean sent = sendNotificationByChannel(notification);
                
                if (sent) {
                    notification.setSent(true);
                    notification.setSentAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    log.info("Sent notification ID: {}, Type: {}, Channel: {}", 
                            notification.getId(), notification.getType(), notification.getChannel());
                }
            } catch (Exception e) {
                log.error("Failed to send notification ID: {}, Error: {}", notification.getId(), e.getMessage(), e);
                // Consider implementing retry mechanism or dead letter queue
            }
        }
    }
    
    @Override
    @Transactional
    public void deleteNotification(UUID id) {
        notificationRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void deleteNotificationsByUserId(UUID userId) {
        notificationRepository.deleteByUserId(userId);
    }
    
    private boolean sendNotificationByChannel(Notification notification) {
        String channel = notification.getChannel();
        
        switch (channel.toUpperCase()) {
            case "EMAIL":
                return sendEmail(notification);
            case "SMS":
                return sendSms(notification);
            case "PUSH":
                return sendPushNotification(notification);
            case "WEBHOOK":
                return callWebhook(notification);
            default:
                log.warn("Unsupported notification channel: {}", channel);
                return false;
        }
    }
    
    private boolean sendEmail(Notification notification) {
        // Implementation would connect to an email service
        log.info("Sending EMAIL to: {}", notification.getRecipient());
        // Simulate successful sending
        return true;
    }
    
    private boolean sendSms(Notification notification) {
        // Implementation would connect to an SMS service
        log.info("Sending SMS to: {}", notification.getRecipient());
        // Simulate successful sending
        return true;
    }
    
    private boolean sendPushNotification(Notification notification) {
        // Implementation would connect to a push notification service
        log.info("Sending PUSH notification to device: {}", notification.getRecipient());
        // Simulate successful sending
        return true;
    }
    
    private boolean callWebhook(Notification notification) {
        // Implementation would make HTTP request to webhook URL
        log.info("Calling webhook: {}", notification.getRecipient());
        // Simulate successful sending
        return true;
    }
    
    private Notification mapToEntity(NotificationDTO dto) {
        return Notification.builder()
                .id(dto.getId() != null ? dto.getId() : UUID.randomUUID())
                .userId(dto.getUserId())
                .type(dto.getType())
                .subject(dto.getSubject())
                .content(dto.getContent())
                .channel(dto.getChannel())
                .recipient(dto.getRecipient())
                .sent(dto.isSent())
                .sentAt(dto.getSentAt())
                .read(dto.isRead())
                .readAt(dto.getReadAt())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
                .relatedEntityId(dto.getRelatedEntityId())
                .relatedEntityType(dto.getRelatedEntityType())
                .build();
    }
    
    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .subject(notification.getSubject())
                .content(notification.getContent())
                .channel(notification.getChannel())
                .recipient(notification.getRecipient())
                .sent(notification.isSent())
                .sentAt(notification.getSentAt())
                .read(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .build();
    }
} 