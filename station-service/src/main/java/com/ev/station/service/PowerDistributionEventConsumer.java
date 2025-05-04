package com.ev.station.service;

import com.ev.station.config.KafkaConsumerConfig;
import com.ev.station.dto.event.PowerDistributionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for power distribution events coming from the Smart Charging service.
 * Processes power control commands and applies them to charging stations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PowerDistributionEventConsumer {
    
    private final PowerControlService powerControlService;
    
    /**
     * Listen for power distribution events from the Smart Charging service.
     * 
     * @param event The power distribution event to process
     * @param acknowledgment The Kafka acknowledgment callback
     */
    @KafkaListener(
            topics = KafkaConsumerConfig.POWER_DISTRIBUTION_EVENTS_TOPIC,
            groupId = KafkaConsumerConfig.STATION_CONSUMER_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePowerDistributionEvent(PowerDistributionEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received power distribution event: {} for station: {}", 
                    event.getEventId(), event.getStationId());
            
            // Process the event and apply power control to the station
            boolean success = powerControlService.processPowerDistributionEvent(event);
            
            if (success) {
                log.info("Successfully applied power distribution event: {}", event.getEventId());
                // Acknowledge the message so Kafka knows it's been processed
                acknowledgment.acknowledge();
            } else {
                log.warn("Failed to apply power distribution event: {}", event.getEventId());
                // Don't acknowledge to allow retry
            }
        } catch (Exception e) {
            log.error("Error processing power distribution event: {}", e.getMessage(), e);
            // Don't acknowledge to allow retry
        }
    }
} 