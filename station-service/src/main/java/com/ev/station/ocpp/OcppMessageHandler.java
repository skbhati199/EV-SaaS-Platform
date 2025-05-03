package com.ev.station.ocpp;

import com.ev.station.model.ChargingStation;
import com.ev.station.model.Connector;
import com.ev.station.model.StationStatus;
import com.ev.station.ocpp.request.*;
import com.ev.station.ocpp.response.*;
import com.ev.station.service.ChargingStationService;
import com.ev.station.service.ConnectorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class OcppMessageHandler {

    private final ObjectMapper objectMapper;
    private final ChargingStationService stationService;
    private final ConnectorService connectorService;
    
    // Map of message ID to pending request futures
    private final Map<String, CompletableFuture<OcppMessage>> pendingRequests = new ConcurrentHashMap<>();
    
    // Handler registry for different OCPP operations
    private final Map<String, Function<Object, Object>> messageHandlers = new HashMap<>();
    
    /**
     * Handles an incoming OCPP message
     * @param stationId The ID of the charging station
     * @param message The OCPP message to handle
     * @return The response OCPP message or null if no response is needed
     */
    public OcppMessage handleMessage(String stationId, OcppMessage message) {
        switch (message.getMessageTypeId()) {
            case CALL:
                return handleCallMessage(stationId, message);
            case CALLRESULT:
                return handleCallResultMessage(message);
            case CALLERROR:
                return handleCallErrorMessage(message);
            default:
                log.error("Unsupported message type: {}", message.getMessageTypeId());
                return OcppMessage.createCallErrorMessage(
                        message.getMessageId(),
                        "NotSupported",
                        "Message type not supported",
                        null);
        }
    }
    
    /**
     * Handles a CALL message from the charging station
     * @param stationId The ID of the charging station
     * @param message The CALL message
     * @return The CALLRESULT or CALLERROR message
     */
    private OcppMessage handleCallMessage(String stationId, OcppMessage message) {
        String action = message.getAction();
        String messageId = message.getMessageId();
        Object payload = message.getPayload();
        
        log.debug("Handling {} from station {}, message ID: {}", action, stationId, messageId);
        
        try {
            Object response = null;
            
            // Handle different OCPP operations
            switch (action) {
                case "BootNotification":
                    BootNotificationRequest bootRequest = objectMapper.convertValue(payload, BootNotificationRequest.class);
                    response = handleBootNotification(stationId, bootRequest);
                    break;
                    
                case "Heartbeat":
                    response = handleHeartbeat(stationId);
                    break;
                    
                case "StatusNotification":
                    StatusNotificationRequest statusRequest = objectMapper.convertValue(payload, StatusNotificationRequest.class);
                    response = handleStatusNotification(stationId, statusRequest);
                    break;
                    
                case "StartTransaction":
                    StartTransactionRequest startRequest = objectMapper.convertValue(payload, StartTransactionRequest.class);
                    response = handleStartTransaction(stationId, startRequest);
                    break;
                    
                case "StopTransaction":
                    StopTransactionRequest stopRequest = objectMapper.convertValue(payload, StopTransactionRequest.class);
                    response = handleStopTransaction(stationId, stopRequest);
                    break;
                    
                case "MeterValues":
                    MeterValuesRequest meterRequest = objectMapper.convertValue(payload, MeterValuesRequest.class);
                    response = handleMeterValues(stationId, meterRequest);
                    break;
                    
                default:
                    log.warn("Unsupported action: {}", action);
                    return OcppMessage.createCallErrorMessage(
                            messageId,
                            "NotImplemented",
                            "Action not implemented: " + action,
                            null);
            }
            
            return OcppMessage.createCallResultMessage(messageId, response);
            
        } catch (Exception e) {
            log.error("Error handling {} from station {}: {}", action, stationId, e.getMessage(), e);
            return OcppMessage.createCallErrorMessage(
                    messageId,
                    "InternalError",
                    "Error processing request: " + e.getMessage(),
                    null);
        }
    }
    
    /**
     * Handles a CALLRESULT message
     * @param message The CALLRESULT message
     * @return Typically null as CALLRESULT messages don't usually require a response
     */
    private OcppMessage handleCallResultMessage(OcppMessage message) {
        String messageId = message.getMessageId();
        CompletableFuture<OcppMessage> future = pendingRequests.remove(messageId);
        
        if (future != null) {
            future.complete(message);
        } else {
            log.warn("Received CALLRESULT for unknown request: {}", messageId);
        }
        
        return null; // No response needed
    }
    
    /**
     * Handles a CALLERROR message
     * @param message The CALLERROR message
     * @return Typically null as CALLERROR messages don't usually require a response
     */
    private OcppMessage handleCallErrorMessage(OcppMessage message) {
        String messageId = message.getMessageId();
        CompletableFuture<OcppMessage> future = pendingRequests.remove(messageId);
        
        if (future != null) {
            future.completeExceptionally(new OcppException(
                    message.getMessageId(),
                    message.getAction(),
                    message.getPayload().toString()));
        } else {
            log.warn("Received CALLERROR for unknown request: {}", messageId);
        }
        
        return null; // No response needed
    }
    
    /**
     * Handles a BootNotification request from a charging station
     * @param stationId The ID of the charging station
     * @param request The BootNotification request
     * @return The BootNotification response
     */
    private BootNotificationResponse handleBootNotification(String stationId, BootNotificationRequest request) {
        log.info("Received BootNotification from station {}: model={}, vendor={}, firmware={}",
                stationId, request.getChargePointModel(), request.getChargePointVendor(), request.getFirmwareVersion());
        
        // Update station information
        ChargingStation station = stationService.registerOrUpdateStation(stationId);
        
        // If this is first registration or station was offline, update fields
        if (station.getStatus() == StationStatus.PENDING || station.getStatus() == StationStatus.OFFLINE) {
            // Update station details with information from boot notification
            station.setModel(request.getChargePointModel());
            station.setVendor(request.getChargePointVendor());
            station.setFirmwareVersion(request.getFirmwareVersion());
            station.setStatus(StationStatus.AVAILABLE);
            stationService.updateStationById(station.getId(), station);
        }
        
        // Return boot confirmation
        return BootNotificationResponse.builder()
                .status(RegistrationStatus.ACCEPTED)
                .currentTime(LocalDateTime.now())
                .interval(300) // Heartbeat interval in seconds
                .build();
    }
    
    /**
     * Handles a Heartbeat request from a charging station
     * @param stationId The ID of the charging station
     * @return The Heartbeat response
     */
    private HeartbeatResponse handleHeartbeat(String stationId) {
        log.debug("Received Heartbeat from station {}", stationId);
        stationService.updateHeartbeat(stationId);
        
        return HeartbeatResponse.builder()
                .currentTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * Handles a StatusNotification request from a charging station
     * @param stationId The ID of the charging station
     * @param request The StatusNotification request
     * @return The StatusNotification response
     */
    private StatusNotificationResponse handleStatusNotification(String stationId, StatusNotificationRequest request) {
        log.info("Received StatusNotification from station {}: connector={}, status={}, errorCode={}",
                stationId, request.getConnectorId(), request.getStatus(), request.getErrorCode());
        
        // Update connector status
        connectorService.updateConnectorStatus(stationId, request.getConnectorId(), request.getStatus());
        
        return StatusNotificationResponse.builder()
                .build();
    }
    
    /**
     * Handles a StartTransaction request from a charging station
     * @param stationId The ID of the charging station
     * @param request The StartTransaction request
     * @return The StartTransaction response
     */
    private StartTransactionResponse handleStartTransaction(String stationId, StartTransactionRequest request) {
        log.info("Received StartTransaction from station {}: connector={}, idTag={}, meterStart={}",
                stationId, request.getConnectorId(), request.getIdTag(), request.getMeterStart());
        
        // Validate the ID tag (RFID card)
        IdTagInfo idTagInfo = validateIdTag(request.getIdTag());
        
        // If ID tag is valid, start the transaction
        if (idTagInfo.getStatus() == AuthorizationStatus.ACCEPTED) {
            // Start the charging session
            // In a real system, we would check that the user has an active account, sufficient funds, etc.
            int transactionId = createTransaction(stationId, request.getConnectorId(), request.getIdTag(), request.getMeterStart());
            
            return StartTransactionResponse.builder()
                    .idTagInfo(idTagInfo)
                    .transactionId(transactionId)
                    .build();
        } else {
            // ID tag not valid
            return StartTransactionResponse.builder()
                    .idTagInfo(idTagInfo)
                    .transactionId(0) // 0 indicates no transaction started
                    .build();
        }
    }
    
    /**
     * Handles a StopTransaction request from a charging station
     * @param stationId The ID of the charging station
     * @param request The StopTransaction request
     * @return The StopTransaction response
     */
    private StopTransactionResponse handleStopTransaction(String stationId, StopTransactionRequest request) {
        log.info("Received StopTransaction from station {}: transactionId={}, meterStop={}",
                stationId, request.getTransactionId(), request.getMeterStop());
        
        // Validate the ID tag (RFID card) if provided
        IdTagInfo idTagInfo = request.getIdTag() != null ? validateIdTag(request.getIdTag()) : null;
        
        // Stop the transaction
        stopTransaction(request.getTransactionId(), request.getMeterStop(), request.getTimestamp());
        
        return StopTransactionResponse.builder()
                .idTagInfo(idTagInfo)
                .build();
    }
    
    /**
     * Handles a MeterValues request from a charging station
     * @param stationId The ID of the charging station
     * @param request The MeterValues request
     * @return The MeterValues response
     */
    private MeterValuesResponse handleMeterValues(String stationId, MeterValuesRequest request) {
        log.debug("Received MeterValues from station {}: connector={}, transactionId={}",
                stationId, request.getConnectorId(), request.getTransactionId());
        
        // Process meter values
        // In a real system, these would be stored in a time-series database for billing and analytics
        for (MeterValue value : request.getMeterValue()) {
            for (SampledValue sampledValue : value.getSampledValue()) {
                log.debug("MeterValue: timestamp={}, value={}, context={}, format={}, measurand={}, phase={}, unit={}",
                        value.getTimestamp(), sampledValue.getValue(), sampledValue.getContext(),
                        sampledValue.getFormat(), sampledValue.getMeasurand(), sampledValue.getPhase(),
                        sampledValue.getUnit());
                
                // Store meter value in database
                // For now, we're just logging it
            }
        }
        
        return MeterValuesResponse.builder()
                .build();
    }
    
    /**
     * Validates an ID tag (RFID card)
     * @param idTag The ID tag to validate
     * @return The ID tag info
     */
    private IdTagInfo validateIdTag(String idTag) {
        // In a real system, this would check a database of authorized RFID cards
        // For simplicity, we're accepting all ID tags
        return IdTagInfo.builder()
                .status(AuthorizationStatus.ACCEPTED)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .parentIdTag(null)
                .build();
    }
    
    /**
     * Creates a new charging transaction
     * @param stationId The ID of the charging station
     * @param connectorId The ID of the connector
     * @param idTag The ID tag of the user
     * @param meterStart The initial meter reading
     * @return The transaction ID
     */
    private int createTransaction(String stationId, int connectorId, String idTag, int meterStart) {
        // In a real system, this would create a record in the database
        // For simplicity, we're just generating a random transaction ID
        return Math.abs(UUID.randomUUID().hashCode());
    }
    
    /**
     * Stops an existing charging transaction
     * @param transactionId The ID of the transaction to stop
     * @param meterStop The final meter reading
     * @param timestamp The time the transaction was stopped
     */
    private void stopTransaction(int transactionId, int meterStop, LocalDateTime timestamp) {
        // In a real system, this would update the transaction record in the database
        // For simplicity, we're just logging the stop
        log.info("Transaction {} stopped: meterStop={}, timestamp={}", transactionId, meterStop, timestamp);
    }
} 