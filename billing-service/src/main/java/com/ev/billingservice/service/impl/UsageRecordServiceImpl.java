package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.UsageRecordDTO;
import com.ev.billingservice.exception.ResourceNotFoundException;
import com.ev.billingservice.model.Subscription;
import com.ev.billingservice.model.UsageRecord;
import com.ev.billingservice.repository.SubscriptionRepository;
import com.ev.billingservice.repository.UsageRecordRepository;
import com.ev.billingservice.service.UsageRecordService;
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
public class UsageRecordServiceImpl implements UsageRecordService {

    private final UsageRecordRepository usageRecordRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public UsageRecordDTO createUsageRecord(UsageRecordDTO usageRecordDTO) {
        // Verify subscription exists
        subscriptionRepository.findById(usageRecordDTO.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", usageRecordDTO.getSubscriptionId()));
        
        UsageRecord usageRecord = mapToEntity(usageRecordDTO);
        if (usageRecord.getTimestamp() == null) {
            usageRecord.setTimestamp(LocalDateTime.now());
        }
        
        UsageRecord savedUsageRecord = usageRecordRepository.save(usageRecord);
        log.info("Created usage record with ID: {} for subscription: {}", 
                savedUsageRecord.getId(), savedUsageRecord.getSubscriptionId());
        
        return mapToDTO(savedUsageRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public UsageRecordDTO getUsageRecordById(UUID id) {
        UsageRecord usageRecord = findUsageRecordById(id);
        return mapToDTO(usageRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsageRecordDTO> getUsageRecordsBySubscriptionId(UUID subscriptionId) {
        // Verify subscription exists
        subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        List<UsageRecord> usageRecords = usageRecordRepository.findBySubscriptionId(subscriptionId);
        return usageRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsageRecordDTO> getUsageRecordsBySubscriptionIdAndMeterType(UUID subscriptionId, String meterType) {
        // Verify subscription exists
        subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        List<UsageRecord> usageRecords = usageRecordRepository.findBySubscriptionIdAndMeterType(subscriptionId, meterType);
        return usageRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsageRecordDTO> getUnprocessedUsageRecordsBySubscriptionId(UUID subscriptionId) {
        // Verify subscription exists
        subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        List<UsageRecord> usageRecords = usageRecordRepository.findBySubscriptionIdAndProcessed(subscriptionId, false);
        return usageRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsageRecordDTO> getUsageRecordsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<UsageRecord> usageRecords = usageRecordRepository.findByTimestampBetween(start, end);
        return usageRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsageRecordDTO> getUsageRecordsBySubscriptionIdAndDateRange(UUID subscriptionId, LocalDateTime start, LocalDateTime end) {
        // Verify subscription exists
        subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", subscriptionId));
        
        List<UsageRecord> usageRecords = usageRecordRepository.findBySubscriptionIdAndTimestampBetween(subscriptionId, start, end);
        return usageRecords.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsageRecordDTO markUsageRecordAsProcessed(UUID id) {
        UsageRecord usageRecord = findUsageRecordById(id);
        usageRecord.setProcessed(true);
        UsageRecord updatedUsageRecord = usageRecordRepository.save(usageRecord);
        log.info("Marked usage record with ID: {} as processed", updatedUsageRecord.getId());
        
        return mapToDTO(updatedUsageRecord);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${billing.usageRecord.processing.cron:0 0 1 * * ?}") // Default: every day at 1 AM
    public void processUsageRecordsForBilling() {
        // Get all active subscriptions
        List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus(Subscription.SubscriptionStatus.ACTIVE);
        
        for (Subscription subscription : activeSubscriptions) {
            List<UsageRecord> unprocessedRecords = usageRecordRepository.findBySubscriptionIdAndProcessed(
                    subscription.getId(), false);
            
            if (!unprocessedRecords.isEmpty()) {
                log.info("Processing {} usage records for subscription ID: {}", 
                        unprocessedRecords.size(), subscription.getId());
                
                // In a real system, we would:
                // 1. Calculate charges based on usage and subscription plan
                // 2. Create invoice items or update existing ones
                // 3. Mark usage records as processed
                
                // For now, just mark them as processed
                for (UsageRecord record : unprocessedRecords) {
                    record.setProcessed(true);
                }
                
                usageRecordRepository.saveAll(unprocessedRecords);
                log.info("Processed {} usage records for subscription ID: {}", 
                        unprocessedRecords.size(), subscription.getId());
            }
        }
    }
    
    private UsageRecord findUsageRecordById(UUID id) {
        return usageRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usage record", "id", id));
    }
    
    private UsageRecord mapToEntity(UsageRecordDTO dto) {
        return UsageRecord.builder()
                .subscriptionId(dto.getSubscriptionId())
                .meterType(dto.getMeterType())
                .quantity(dto.getQuantity())
                .timestamp(dto.getTimestamp())
                .processed(dto.isProcessed())
                .build();
    }
    
    private UsageRecordDTO mapToDTO(UsageRecord entity) {
        return UsageRecordDTO.builder()
                .id(entity.getId())
                .subscriptionId(entity.getSubscriptionId())
                .meterType(entity.getMeterType())
                .quantity(entity.getQuantity())
                .timestamp(entity.getTimestamp())
                .processed(entity.isProcessed())
                .build();
    }
} 