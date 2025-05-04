package com.ev.smartcharging.service.impl;

import com.ev.smartcharging.config.KafkaConfig;
import com.ev.smartcharging.dto.event.PowerDistributionEvent;
import com.ev.smartcharging.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of KafkaProducerService for sending smart charging events to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public CompletableFuture<Void> sendPowerDistributionEvent(PowerDistributionEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        
        String key = event.getStationId().toString();
        
        log.info("Sending power distribution event {} to station {}", event.getEventId(), event.getStationId());
        
        return kafkaTemplate.send(KafkaConfig.POWER_DISTRIBUTION_EVENTS_TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent power distribution event {} to partition {}", 
                                event.getEventId(), result.getRecordMetadata().partition());
                    } else {
                        log.error("Failed to send power distribution event {}", event.getEventId(), ex);
                    }
                })
                .thenApply(result -> null);
    }

    @Override
    public UUID sendPowerAdjustmentCommand(
            UUID stationId,
            Integer connectorId,
            Double powerLimitKW,
            PowerDistributionEvent.PowerAdjustmentReason reason,
            boolean temporary,
            Integer durationSeconds,
            UUID transactionId) {
        
        UUID eventId = UUID.randomUUID();
        
        PowerDistributionEvent event = PowerDistributionEvent.builder()
                .eventId(eventId)
                .stationId(stationId)
                .connectorId(connectorId)
                .powerLimitKW(powerLimitKW)
                .reason(reason)
                .temporary(temporary)
                .durationSeconds(durationSeconds)
                .transactionId(transactionId)
                .timestamp(LocalDateTime.now())
                .priority(calculatePriority(reason))
                .build();
        
        sendPowerDistributionEvent(event);
        return eventId;
    }

    @Override
    public UUID sendEmergencyPowerReduction(UUID stationId, Integer connectorId, Double powerLimitKW, Integer durationSeconds) {
        return sendPowerAdjustmentCommand(
                stationId,
                connectorId,
                powerLimitKW,
                PowerDistributionEvent.PowerAdjustmentReason.EMERGENCY_REDUCTION,
                true,
                durationSeconds,
                null);
    }
    
    /**
     * Calculate priority based on the reason type.
     * Emergency reductions have the highest priority.
     */
    private int calculatePriority(PowerDistributionEvent.PowerAdjustmentReason reason) {
        switch (reason) {
            case EMERGENCY_REDUCTION:
                return 100; // Highest priority
            case GRID_CONSTRAINT:
                return 90;
            case SYSTEM_MAINTENANCE:
                return 80;
            case LOAD_BALANCING:
                return 70;
            case SCHEDULED_PROFILE:
                return 60;
            case DYNAMIC_PRICING:
                return 50;
            case OPTIMIZATION:
                return 40;
            case USER_REQUEST:
                return 30; // Lowest priority - user requests can be overridden
            default:
                return 0;
        }
    }
} 