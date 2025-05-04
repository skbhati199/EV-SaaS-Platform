package com.ev.roamingservice.service;

import com.ev.roamingservice.dto.event.CdrEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Service for handling CDR (Charge Detail Record) operations
 * Processes charging session events and creates/updates CDRs accordingly
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CdrService {

    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    /**
     * Process a charging session event and create/update a CDR if applicable
     * 
     * @param payload The charging session event payload
     */
    public void processChargingSessionEvent(String payload) {
        try {
            JsonNode event = objectMapper.readTree(payload);
            String eventType = event.get("eventType").asText();
            
            // Only generate CDRs for completed or stopped sessions
            if ("COMPLETED".equals(eventType) || "STOPPED".equals(eventType)) {
                generateCdrFromChargingSession(event);
            }
        } catch (Exception e) {
            log.error("Error processing charging session event for CDR: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Generate a CDR from a charging session event
     * 
     * @param session The charging session event as JsonNode
     */
    private void generateCdrFromChargingSession(JsonNode session) {
        try {
            String sessionId = session.get("sessionId").asText();
            log.info("Generating CDR for charging session: {}", sessionId);
            
            // Extract session data
            ZonedDateTime startTime = ZonedDateTime.parse(session.get("startTime").asText());
            ZonedDateTime endTime = ZonedDateTime.parse(session.get("endTime").asText());
            String stationId = session.get("stationId").asText();
            String evseId = session.get("evseId").asText();
            String connectorId = session.get("connectorId").asText();
            
            // Extract energy and cost data
            BigDecimal energyDelivered = new BigDecimal(session.get("energyDelivered").asText());
            BigDecimal totalCost = session.has("totalCost") ? 
                    new BigDecimal(session.get("totalCost").asText()) : BigDecimal.ZERO;
            String currency = session.has("currency") ? session.get("currency").asText() : "EUR";
            
            // Create CDR event
            CdrEvent cdrEvent = CdrEvent.builder()
                    .cdrId(UUID.randomUUID().toString())
                    .eventType(CdrEvent.CdrEventType.CREATED)
                    .sessionId(sessionId)
                    .startDateTime(startTime)
                    .endDateTime(endTime)
                    .locationId(stationId)
                    .evseId(evseId)
                    .connectorId(connectorId)
                    .totalEnergy(energyDelivered)
                    .totalCost(totalCost)
                    .currency(currency)
                    .status("OPEN")
                    .build();
            
            // Add auth info if available
            if (session.has("authorizationId")) {
                cdrEvent.setAuthorizationId(session.get("authorizationId").asText());
            }
            
            // Send CDR event to Kafka
            kafkaProducerService.sendCdrEvent(cdrEvent)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send CDR event: {}", exception.getMessage(), exception);
                        } else {
                            log.info("Successfully sent CDR event for session {}: {}", 
                                    sessionId, cdrEvent.getCdrId());
                        }
                    });
            
        } catch (Exception e) {
            log.error("Error generating CDR from charging session: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Update a CDR status
     * 
     * @param cdrId The CDR ID
     * @param newStatus The new status
     */
    public void updateCdrStatus(String cdrId, String newStatus) {
        try {
            log.info("Updating CDR status: {} -> {}", cdrId, newStatus);
            
            // In a real implementation, you would:
            // 1. Retrieve the CDR from the database
            // 2. Update its status
            // 3. Save it back to the database
            
            // Create a status update event
            CdrEvent cdrEvent = CdrEvent.builder()
                    .cdrId(cdrId)
                    .eventType(getCdrEventTypeForStatus(newStatus))
                    .status(newStatus)
                    .timestamp(ZonedDateTime.now())
                    .build();
            
            // Send CDR event to Kafka
            kafkaProducerService.sendCdrEvent(cdrEvent)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send CDR status update event: {}", 
                                    exception.getMessage(), exception);
                        } else {
                            log.info("Successfully sent CDR status update event: {} -> {}", 
                                    cdrId, newStatus);
                        }
                    });
            
        } catch (Exception e) {
            log.error("Error updating CDR status: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get the appropriate CDR event type based on the status
     * 
     * @param status The CDR status
     * @return The corresponding event type
     */
    private CdrEvent.CdrEventType getCdrEventTypeForStatus(String status) {
        switch (status.toUpperCase()) {
            case "SENT":
                return CdrEvent.CdrEventType.SENT;
            case "RECEIVED":
                return CdrEvent.CdrEventType.RECEIVED;
            case "SETTLED":
                return CdrEvent.CdrEventType.SETTLED;
            case "DISPUTED":
                return CdrEvent.CdrEventType.DISPUTED;
            case "CORRECTED":
                return CdrEvent.CdrEventType.CORRECTED;
            default:
                return CdrEvent.CdrEventType.UPDATED;
        }
    }
} 