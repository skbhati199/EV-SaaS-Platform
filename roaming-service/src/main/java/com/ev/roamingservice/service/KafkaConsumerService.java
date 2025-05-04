package com.ev.roamingservice.service;

import com.ev.roamingservice.config.KafkaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for consuming Kafka events from other services
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    
    /**
     * Listen for station status events
     * 
     * @param payload the event payload
     */
    @KafkaListener(
        topics = "station-status-events",
        groupId = KafkaConfig.ROAMING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeStationStatusEvents(String payload) {
        try {
            log.info("Received station status event: {}", payload);
            // Process station status events if needed
            // For example, update local cache or trigger other actions
        } catch (Exception e) {
            log.error("Error processing station status event: {}", payload, e);
        }
    }
    
    /**
     * Listen for charging session events
     * 
     * @param payload the event payload
     */
    @KafkaListener(
        topics = "charging-session-events",
        groupId = KafkaConfig.ROAMING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeChargingSessionEvents(String payload) {
        try {
            log.info("Received charging session event: {}", payload);
            // Process charging session events
            // This could be used to create CDRs for roaming partners
        } catch (Exception e) {
            log.error("Error processing charging session event: {}", payload, e);
        }
    }
    
    /**
     * Listen for payment events
     * 
     * @param payload the event payload
     */
    @KafkaListener(
        topics = "payment-events",
        groupId = KafkaConfig.ROAMING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentEvents(String payload) {
        try {
            log.info("Received payment event: {}", payload);
            // Process payment events if needed for roaming settlement
        } catch (Exception e) {
            log.error("Error processing payment event: {}", payload, e);
        }
    }
} 