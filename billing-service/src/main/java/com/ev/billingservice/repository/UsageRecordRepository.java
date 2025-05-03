package com.ev.billingservice.repository;

import com.ev.billingservice.model.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, UUID> {
    
    List<UsageRecord> findBySubscriptionId(UUID subscriptionId);
    
    List<UsageRecord> findBySubscriptionIdAndMeterType(UUID subscriptionId, String meterType);
    
    List<UsageRecord> findBySubscriptionIdAndProcessed(UUID subscriptionId, boolean processed);
    
    List<UsageRecord> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<UsageRecord> findBySubscriptionIdAndTimestampBetween(UUID subscriptionId, LocalDateTime start, LocalDateTime end);
} 