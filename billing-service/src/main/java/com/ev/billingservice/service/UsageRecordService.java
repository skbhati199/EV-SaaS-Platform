package com.ev.billingservice.service;

import com.ev.billingservice.dto.UsageRecordDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UsageRecordService {
    
    UsageRecordDTO createUsageRecord(UsageRecordDTO usageRecordDTO);
    
    UsageRecordDTO getUsageRecordById(UUID id);
    
    List<UsageRecordDTO> getUsageRecordsBySubscriptionId(UUID subscriptionId);
    
    List<UsageRecordDTO> getUsageRecordsBySubscriptionIdAndMeterType(UUID subscriptionId, String meterType);
    
    List<UsageRecordDTO> getUnprocessedUsageRecordsBySubscriptionId(UUID subscriptionId);
    
    List<UsageRecordDTO> getUsageRecordsByDateRange(LocalDateTime start, LocalDateTime end);
    
    List<UsageRecordDTO> getUsageRecordsBySubscriptionIdAndDateRange(UUID subscriptionId, LocalDateTime start, LocalDateTime end);
    
    UsageRecordDTO markUsageRecordAsProcessed(UUID id);
    
    void processUsageRecordsForBilling();
} 