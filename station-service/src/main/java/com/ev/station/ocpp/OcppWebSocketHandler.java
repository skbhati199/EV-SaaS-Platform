package com.ev.station.ocpp;

import com.ev.station.model.StationStatus;
import com.ev.station.service.ChargingStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcppWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChargingStationService stationService;
    private final OcppMessageHandler ocppMessageHandler;
    
    // WebSocketSession ID -> ChargingStation ID
    private final Map<String, String> sessionToStationMap = new ConcurrentHashMap<>();
    // ChargingStation ID -> WebSocketSession
    private final Map<String, WebSocketSession> stationToSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String stationId = (String) session.getAttributes().get("stationId");
        log.info("WebSocket connection established with charging station: {}, session: {}", 
                 stationId, session.getId());
        
        sessionToStationMap.put(session.getId(), stationId);
        stationToSessionMap.put(stationId, session);
        
        // Send a BootNotification.req to the station
        // This won't be needed in normal operations since stations will send this automatically
        // but helps with testing
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String stationId = sessionToStationMap.get(session.getId());
        try {
            log.debug("Received message from station {}: {}", stationId, message.getPayload());
            
            // Parse the OCPP message
            OcppMessage ocppMessage = objectMapper.readValue(message.getPayload(), OcppMessage.class);
            
            // Process the message and get the response
            OcppMessage response = ocppMessageHandler.handleMessage(stationId, ocppMessage);
            
            // Send the response back to the station
            if (response != null) {
                String responseJson = objectMapper.writeValueAsString(response);
                session.sendMessage(new TextMessage(responseJson));
                log.debug("Sent response to station {}: {}", stationId, responseJson);
            }
        } catch (Exception e) {
            log.error("Error processing message from station {}: {}", stationId, e.getMessage(), e);
            try {
                // Send error response
                OcppMessage errorResponse = OcppMessage.createCallErrorMessage(
                        UUID.randomUUID().toString(),
                        "InternalError",
                        "Error processing message: " + e.getMessage(),
                        null
                );
                String errorJson = objectMapper.writeValueAsString(errorResponse);
                session.sendMessage(new TextMessage(errorJson));
            } catch (IOException ioe) {
                log.error("Failed to send error response to station {}: {}", 
                         stationId, ioe.getMessage(), ioe);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String stationId = sessionToStationMap.remove(session.getId());
        if (stationId != null) {
            stationToSessionMap.remove(stationId);
            log.info("WebSocket connection closed with station: {}, status: {}", stationId, status);
            
            // Update station status to offline
            try {
                stationService.updateStationStatus(stationId, StationStatus.OFFLINE);
            } catch (Exception e) {
                log.error("Error updating station status: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String stationId = sessionToStationMap.get(session.getId());
        log.error("Transport error for station {}: {}", stationId, exception.getMessage(), exception);
    }
    
    /**
     * Sends a message to a specific charging station
     * @param stationId The ID of the charging station
     * @param message The OCPP message to send
     * @return true if the message was sent successfully, false otherwise
     */
    public boolean sendMessageToStation(String stationId, OcppMessage message) {
        WebSocketSession session = stationToSessionMap.get(stationId);
        if (session != null && session.isOpen()) {
            try {
                String messageJson = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
                return true;
            } catch (IOException e) {
                log.error("Error sending message to station {}: {}", stationId, e.getMessage(), e);
            }
        } else {
            log.warn("Cannot send message to station {}. Session is not open.", stationId);
        }
        return false;
    }
    
    /**
     * Checks if a station is connected
     * @param stationId The ID of the charging station
     * @return true if the station is connected, false otherwise
     */
    public boolean isStationConnected(String stationId) {
        WebSocketSession session = stationToSessionMap.get(stationId);
        return session != null && session.isOpen();
    }
} 