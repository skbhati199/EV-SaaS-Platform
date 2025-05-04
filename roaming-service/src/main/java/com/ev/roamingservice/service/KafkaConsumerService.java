package com.ev.roamingservice.service;

import com.ev.roamingservice.config.KafkaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Service for consuming Kafka events from other services
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final CdrService cdrService;
    
    /**
     * Listen for station status events
     * 
     * @param payload the event payload
     * @param ack the acknowledgment to manually commit the offset
     */
    @KafkaListener(
        topics = "station-status-events",
        groupId = KafkaConfig.ROAMING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeStationStatusEvents(String payload, Acknowledgment ack) {
        try {
            log.info("Received station status event: {}", payload);
            // Process station status events if needed
            // For example, update local cache or trigger other actions
            
            // Acknowledge successful processing
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing station status event: {}", payload, e);
            // In case of an error, don't acknowledge to allow for retry
        }
    }
    
    /**
     * Listen for charging session events
     * 
     * @param payload the event payload
     * @param ack the acknowledgment to manually commit the offset
     */
    @KafkaListener(
        topics = "charging-session-events",
        groupId = KafkaConfig.ROAMING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeChargingSessionEvents(String payload, Acknowledgment ack) {
        try {
            log.info("Received charging session event: {}", payload);
            
            // Process charging session for CDR generation
            cdrService.processChargingSessionEvent(payload);
            
            // Acknowledge successful processing
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing charging session event: {}", payload, e);
            // In case of an error, don't acknowledge to allow for retry
        }
    }
    
    /**
     * Listen for payment events
     * 
     * @param payload the event payload
     * @param ack the acknowledgment to manually commit the offset
     */
    @KafkaListener(
        topics = "payment-events",
        groupId = KafkaConfig.ROAMING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentEvents(String payload, Acknowledgment ack) {
        try {
            log.info("Received payment event: {}", payload);
            // Process payment events if needed for roaming settlement
            
            // Acknowledge successful processing
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing payment event: {}", payload, e);
            // In case of an error, don't acknowledge to allow for retry
        }
    }
    
    /**
     * Listen for invoice events
     * 
     * @param payload the event payload
     * @param ack the acknowledgment to manually commit the offset
     */
    @KafkaListener(
        topics = "invoice-events",
        groupId = KafkaConfig.ROAMING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeInvoiceEvents(String payload, Acknowledgment ack) {
        try {
            log.info("Received invoice event: {}", payload);
            // Process invoice events if needed for roaming operations
            
            // Acknowledge successful processing
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing invoice event: {}", payload, e);
            // In case of an error, don't acknowledge to allow for retry
        }
    }
} 