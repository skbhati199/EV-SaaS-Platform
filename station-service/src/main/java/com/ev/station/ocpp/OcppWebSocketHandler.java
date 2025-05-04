package com.ev.station.ocpp;

import com.ev.station.model.StationStatus;
import com.ev.station.service.ChargingStationService;
import com.ev.station.ocpp.request.SetChargingProfileRequest;
import com.ev.station.ocpp.response.SetChargingProfileResponse;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket handler for OCPP communication with charging stations.
 */
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
    
    // Map of message ID to pending request futures
    private final Map<String, CompletableFuture<OcppMessage>> pendingRequests = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String stationId = (String) session.getAttributes().get("stationId");
        if (stationId != null) {
            sessionToStationMap.put(session.getId(), stationId);
            stationToSessionMap.put(stationId, session);
            log.info("Station connected: {}", stationId);
        } else {
            log.error("No station ID found in session attributes");
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (IOException e) {
                log.error("Error closing session: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String stationId = sessionToStationMap.get(session.getId());
        if (stationId == null) {
            log.error("No station ID found in session attributes");
            return;
        }
        
        try {
            log.debug("Received message from station {}: {}", stationId, message.getPayload());
            
            // Parse the incoming message
            String payload = message.getPayload();
            JsonNode jsonNode = objectMapper.readTree(payload);
            OcppMessage ocppMessage = OcppMessage.fromJson(jsonNode, objectMapper);
            
            // If it's a response to our request
            if (ocppMessage.getMessageTypeId() == OcppMessage.MessageTypeId.CALLRESULT ||
                ocppMessage.getMessageTypeId() == OcppMessage.MessageTypeId.CALLERROR) {
                
                CompletableFuture<OcppMessage> future = pendingRequests.remove(ocppMessage.getMessageId());
                if (future != null) {
                    future.complete(ocppMessage);
                } else {
                    log.warn("Received response for unknown request: {}", ocppMessage.getMessageId());
                }
                return;
            }
            
            // Handle the message
            OcppMessage response = ocppMessageHandler.handleMessage(stationId, ocppMessage);
            
            // Send the response if needed
            if (response != null) {
                String responseJson = objectMapper.writeValueAsString(response.toJson());
                log.debug("Sending response to station {}: {}", stationId, responseJson);
                session.sendMessage(new TextMessage(responseJson));
            }
        } catch (Exception e) {
            log.error("Error handling message from station {}: {}", stationId, e.getMessage(), e);
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
            log.info("Station disconnected: {}, status: {}", stationId, status);
            
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

    /**
     * Send an OCPP request to a charging station
     * @param stationId The ID of the station
     * @param action The OCPP action
     * @param payload The request payload
     * @return A future that will complete with the response
     */
    public CompletableFuture<OcppMessage> sendRequest(String stationId, String action, Object payload) {
        WebSocketSession session = stationToSessionMap.get(stationId);
        if (session == null || !session.isOpen()) {
            log.warn("Station {} is not connected", stationId);
            CompletableFuture<OcppMessage> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("Station not connected"));
            return future;
        }
        
        try {
            // Generate a message ID
            String messageId = UUID.randomUUID().toString();
            
            // Create the OCPP message
            OcppMessage message = OcppMessage.createCallMessage(messageId, action, payload);
            
            // Convert to JSON
            String messageJson = objectMapper.writeValueAsString(message.toJson());
            
            // Create the future for the response
            CompletableFuture<OcppMessage> future = new CompletableFuture<>();
            
            // Register the future
            pendingRequests.put(messageId, future);
            
            // Set a timeout for the request
            CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS).execute(() -> {
                if (!future.isDone()) {
                    pendingRequests.remove(messageId);
                    future.completeExceptionally(new Exception("Request timed out"));
                }
            });
            
            // Send the message
            log.debug("Sending request to station {}: {}", stationId, messageJson);
            session.sendMessage(new TextMessage(messageJson));
            
            return future;
        } catch (Exception e) {
            log.error("Error sending request to station {}: {}", stationId, e.getMessage(), e);
            CompletableFuture<OcppMessage> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
    
    /**
     * Send a SetChargingProfile request to a charging station
     * @param stationId The ID of the station
     * @param request The SetChargingProfile request
     * @return A future that will complete with the SetChargingProfile response
     */
    public CompletableFuture<SetChargingProfileResponse> sendSetChargingProfileRequest(
            String stationId, SetChargingProfileRequest request) {
        return sendRequest(stationId, "SetChargingProfile", request)
                .thenApply(response -> {
                    try {
                        if (response.getMessageTypeId() == OcppMessage.MessageTypeId.CALLRESULT) {
                            return objectMapper.convertValue(response.getPayload(), SetChargingProfileResponse.class);
                        } else {
                            log.error("Received error response to SetChargingProfile: {}", response.getPayload());
                            throw new Exception("Error response: " + response.getPayload());
                        }
                    } catch (Exception e) {
                        log.error("Error parsing SetChargingProfile response: {}", e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                });
    }
} 