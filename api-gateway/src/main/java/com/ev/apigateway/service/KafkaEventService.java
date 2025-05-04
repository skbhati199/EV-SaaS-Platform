package com.ev.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Service that consumes Kafka events and forwards them to WebSocket clients
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Topic for station status events
    @KafkaListener(topics = "station-status-events", groupId = "api-gateway-group")
    public void consumeStationStatusEvents(String message) {
        try {
            // Parse the message
            JsonNode eventNode = objectMapper.readTree(message);
            
            // Create a simplified event object for the frontend
            Map<String, Object> event = new HashMap<>();
            event.put("type", "STATION_STATUS");
            event.put("stationId", eventNode.path("stationId").asText());
            event.put("newStatus", eventNode.path("newStatus").asText());
            event.put("timestamp", eventNode.path("timestamp").asText());
            
            // Send to all clients subscribed to the station status topic
            simpMessagingTemplate.convertAndSend("/topic/station-status", event);
            log.debug("Forwarded station status event to WebSocket clients");
        } catch (Exception e) {
            log.error("Error processing station status event: {}", e.getMessage(), e);
        }
    }

    // Topic for charging session events
    @KafkaListener(topics = "charging-session-events", groupId = "api-gateway-group")
    public void consumeChargingSessionEvents(String message) {
        try {
            // Parse the message
            JsonNode eventNode = objectMapper.readTree(message);
            
            // Create a simplified event object for the frontend
            Map<String, Object> event = new HashMap<>();
            event.put("type", "CHARGING_SESSION");
            event.put("eventType", eventNode.path("eventType").asText());
            event.put("sessionId", eventNode.path("sessionId").asText());
            event.put("stationId", eventNode.path("stationId").asText());
            event.put("connectorId", eventNode.path("connectorId").asText());
            event.put("userId", eventNode.path("userId").asText());
            event.put("timestamp", eventNode.path("timestamp").asText());
            
            // Send to all clients subscribed to the charging session topic
            simpMessagingTemplate.convertAndSend("/topic/charging-sessions", event);
            log.debug("Forwarded charging session event to WebSocket clients");
        } catch (Exception e) {
            log.error("Error processing charging session event: {}", e.getMessage(), e);
        }
    }

    // Topic for payment events
    @KafkaListener(topics = "payment-events", groupId = "api-gateway-group")
    public void consumePaymentEvents(String message) {
        try {
            // Parse the message
            JsonNode eventNode = objectMapper.readTree(message);
            
            // Create a simplified event object for the frontend
            Map<String, Object> event = new HashMap<>();
            event.put("type", "PAYMENT");
            event.put("eventType", eventNode.path("eventType").asText());
            event.put("paymentId", eventNode.path("paymentId").asText());
            event.put("userId", eventNode.path("userId").asText());
            event.put("amount", eventNode.path("amount").asText());
            event.put("currency", eventNode.path("currency").asText());
            event.put("status", eventNode.path("status").asText());
            event.put("timestamp", eventNode.path("timestamp").asText());
            
            // Send to all clients subscribed to the payment events topic
            simpMessagingTemplate.convertAndSend("/topic/payments", event);
            log.debug("Forwarded payment event to WebSocket clients");
        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }

    // Topic for invoice events
    @KafkaListener(topics = "invoice-events", groupId = "api-gateway-group")
    public void consumeInvoiceEvents(String message) {
        try {
            // Parse the message
            JsonNode eventNode = objectMapper.readTree(message);
            
            // Create a simplified event object for the frontend
            Map<String, Object> event = new HashMap<>();
            event.put("type", "INVOICE");
            event.put("eventType", eventNode.path("eventType").asText());
            event.put("invoiceId", eventNode.path("invoiceId").asText());
            event.put("userId", eventNode.path("userId").asText());
            event.put("invoiceNumber", eventNode.path("invoiceNumber").asText());
            event.put("totalAmount", eventNode.path("totalAmount").asText());
            event.put("currency", eventNode.path("currency").asText());
            event.put("status", eventNode.path("status").asText());
            event.put("timestamp", eventNode.path("timestamp").asText());
            
            // Send to all clients subscribed to the invoice events topic
            simpMessagingTemplate.convertAndSend("/topic/invoices", event);
            log.debug("Forwarded invoice event to WebSocket clients");
        } catch (Exception e) {
            log.error("Error processing invoice event: {}", e.getMessage(), e);
        }
    }
} 