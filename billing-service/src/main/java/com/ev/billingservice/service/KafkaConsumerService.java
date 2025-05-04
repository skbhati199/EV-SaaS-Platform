package com.ev.billingservice.service;

import com.ev.billingservice.config.KafkaConfig;
import com.ev.billingservice.dto.event.ChargingSessionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for consuming events from Kafka topics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final TransactionService transactionService;

    /**
     * Consume charging session events from Kafka
     * 
     * @param event The charging session event
     * @param acknowledgment Acknowledgment for manual commits
     */
    @KafkaListener(
        topics = KafkaConfig.CHARGING_SESSION_TOPIC,
        groupId = KafkaConfig.BILLING_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeChargingSessionEvent(ChargingSessionEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received charging session event: type={}, sessionId={}", 
                    event.getEventType(), event.getSessionId());
            
            switch (event.getEventType()) {
                case "STARTED":
                    handleSessionStarted(event);
                    break;
                case "UPDATED":
                    handleSessionUpdated(event);
                    break;
                case "ENDED":
                    handleSessionEnded(event);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
            
            // Acknowledge the message after successful processing
            acknowledgment.acknowledge();
            log.debug("Acknowledged charging session event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing charging session event: {}", event.getEventId(), e);
            // Don't acknowledge - the message will be redelivered
            // If this fails multiple times, the DefaultErrorHandler will handle it
            throw e;
        }
    }
    
    /**
     * Handle a session started event
     */
    private void handleSessionStarted(ChargingSessionEvent event) {
        log.info("Handling session started event for session: {}", event.getSessionId());
        
        // Create a pending transaction for the charging session
        transactionService.createPendingTransaction(
            event.getSessionId(),
            event.getUserId(),
            event.getStationId(),
            event.getConnectorId(),
            event.getStartTime()
        );
    }
    
    /**
     * Handle a session updated event
     */
    private void handleSessionUpdated(ChargingSessionEvent event) {
        log.info("Handling session updated event for session: {}", event.getSessionId());
        
        // Update the pending transaction with current values
        transactionService.updatePendingTransaction(
            event.getSessionId(),
            event.getEnergyDeliveredKwh(),
            event.getDurationSeconds(),
            event.getMeterValue()
        );
    }
    
    /**
     * Handle a session ended event
     */
    private void handleSessionEnded(ChargingSessionEvent event) {
        log.info("Handling session ended event for session: {}", event.getSessionId());
        
        // Complete the transaction and generate an invoice
        transactionService.completeTransaction(
            event.getSessionId(),
            event.getEndTime(),
            event.getEnergyDeliveredKwh(),
            event.getDurationSeconds()
        );
    }
}