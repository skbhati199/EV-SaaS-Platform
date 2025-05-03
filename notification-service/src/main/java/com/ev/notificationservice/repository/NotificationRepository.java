package com.ev.notificationservice.repository;

import com.ev.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    List<Notification> findByUserId(UUID userId);
    
    List<Notification> findByUserIdAndSentFalse(UUID userId);
    
    List<Notification> findByUserIdAndReadFalse(UUID userId);
    
    List<Notification> findByRelatedEntityIdAndRelatedEntityType(UUID relatedEntityId, String relatedEntityType);
    
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Notification> findByType(String type);
    
    List<Notification> findByUserIdAndType(UUID userId, String type);
    
    List<Notification> findBySentFalse();
} 