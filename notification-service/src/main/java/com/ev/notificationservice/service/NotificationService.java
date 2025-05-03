package com.ev.notificationservice.service;

import com.ev.notificationservice.dto.NotificationDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    
    NotificationDTO createNotification(NotificationDTO notificationDTO);
    
    NotificationDTO getNotificationById(UUID id);
    
    List<NotificationDTO> getNotificationsByUserId(UUID userId);
    
    List<NotificationDTO> getUnsentNotificationsByUserId(UUID userId);
    
    List<NotificationDTO> getUnreadNotificationsByUserId(UUID userId);
    
    List<NotificationDTO> getNotificationsByRelatedEntity(UUID relatedEntityId, String relatedEntityType);
    
    List<NotificationDTO> getNotificationsByDateRange(LocalDateTime start, LocalDateTime end);
    
    List<NotificationDTO> getNotificationsByType(String type);
    
    List<NotificationDTO> getNotificationsByUserIdAndType(UUID userId, String type);
    
    NotificationDTO markAsRead(UUID id);
    
    NotificationDTO markAsSent(UUID id);
    
    void sendNotifications();
    
    void deleteNotification(UUID id);
    
    void deleteNotificationsByUserId(UUID userId);
} 