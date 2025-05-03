package com.ev.billingservice.repository;

import com.ev.billingservice.model.BillingAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BillingAuditLogRepository extends JpaRepository<BillingAuditLog, UUID> {
    
    List<BillingAuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);
    
    Page<BillingAuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);
    
    List<BillingAuditLog> findByUserId(UUID userId);
    
    Page<BillingAuditLog> findByUserId(UUID userId, Pageable pageable);
    
    List<BillingAuditLog> findByAction(String action);
    
    List<BillingAuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    Page<BillingAuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
} 