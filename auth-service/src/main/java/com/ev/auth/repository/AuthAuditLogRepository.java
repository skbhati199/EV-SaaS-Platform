package com.ev.auth.repository;

import com.ev.auth.model.AuthAuditLog;
import com.ev.auth.model.AuthAuditLog.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, UUID> {
    
    List<AuthAuditLog> findByUserId(UUID userId);
    
    Page<AuthAuditLog> findByUserId(UUID userId, Pageable pageable);
    
    List<AuthAuditLog> findByEventType(EventType eventType);
    
    Page<AuthAuditLog> findByEventType(EventType eventType, Pageable pageable);
    
    List<AuthAuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    Page<AuthAuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    List<AuthAuditLog> findByUserIdAndEventType(UUID userId, EventType eventType);
    
    Page<AuthAuditLog> findByUserIdAndEventType(UUID userId, EventType eventType, Pageable pageable);
} 