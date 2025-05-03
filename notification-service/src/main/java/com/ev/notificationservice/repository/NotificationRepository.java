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
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);
    
    List<Notification> findByUserIdAndSentFalseOrderByCreatedAtDesc(UUID userId);
    
    List<Notification> findByTypeOrderByCreatedAtDesc(String type);
    
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, String type);
    
    List<Notification> findByRelatedEntityIdAndRelatedEntityTypeOrderByCreatedAtDesc(UUID relatedEntityId, String relatedEntityType);
    
    List<Notification> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
    
    void deleteByIdAndUserId(UUID id, UUID userId);
} 