package com.ev.notificationservice.repository;

import com.ev.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    // Get all by user ID
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    // Simplified methods used by NotificationServiceImpl
    List<Notification> findByUserId(UUID userId);
    
    // Notification status methods
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);
    List<Notification> findByUserIdAndSentFalseOrderByCreatedAtDesc(UUID userId);
    
    // Simplified status methods used by NotificationServiceImpl
    List<Notification> findByUserIdAndReadFalse(UUID userId);
    List<Notification> findByUserIdAndSentFalse(UUID userId);
    List<Notification> findBySentFalse();
    
    // Type based queries
    List<Notification> findByTypeOrderByCreatedAtDesc(String type);
    List<Notification> findByType(String type);
    
    // User and type combined
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, String type);
    List<Notification> findByUserIdAndType(UUID userId, String type);
    
    // Related entity
    List<Notification> findByRelatedEntityIdAndRelatedEntityTypeOrderByCreatedAtDesc(UUID relatedEntityId, String relatedEntityType);
    List<Notification> findByRelatedEntityIdAndRelatedEntityType(UUID relatedEntityId, String relatedEntityType);
    
    // Date range
    List<Notification> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Security methods
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
    void deleteByIdAndUserId(UUID id, UUID userId);
    
    // User notification deletion
    void deleteByUserId(UUID userId);
} 