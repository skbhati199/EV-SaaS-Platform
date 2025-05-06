package com.ev.station.service.impl;

import com.ev.station.dto.ChargingSessionDto;
import com.ev.station.dto.StartChargingSessionRequest;
import com.ev.station.dto.StopChargingSessionRequest;
import com.ev.station.dto.event.ChargingSessionEvent;
import com.ev.station.model.ChargingSession;
import com.ev.station.model.Connector;
import com.ev.station.model.EVSEStatus;
import com.ev.station.model.SessionStatus;
import com.ev.station.model.StationStatus;
import com.ev.station.repository.ChargingSessionRepository;
import com.ev.station.repository.ConnectorRepository;
import com.ev.station.service.ChargingSessionService;
import com.ev.station.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChargingSessionServiceImpl implements ChargingSessionService {

    private final ChargingSessionRepository sessionRepository;
    private final ConnectorRepository connectorRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public List<ChargingSessionDto> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ChargingSessionDto getSessionById(UUID id) {
        ChargingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charging session not found: " + id));
        return mapToDto(session);
    }

    @Override
    public ChargingSessionDto getSessionByTransactionId(String transactionId) {
        ChargingSession session = sessionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Charging session not found: " + transactionId));
        return mapToDto(session);
    }

    @Override
    public List<ChargingSessionDto> getSessionsByStationId(UUID stationId) {
        return sessionRepository.findByStationId(stationId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ChargingSessionDto> getSessionsByUserId(UUID userId) {
        return sessionRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ChargingSessionDto> getSessionsByStationIdAndStatus(UUID stationId, SessionStatus status) {
        return sessionRepository.findByStationIdAndStatus(stationId, status).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ChargingSessionDto> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return sessionRepository.findByStartTimestampBetween(startDate, endDate).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ChargingSessionDto> getSessionsByUserIdAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return sessionRepository.findByUserIdAndStartTimestampBetween(userId, startDate, endDate).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ChargingSessionDto startChargingSession(UUID stationId, StartChargingSessionRequest request) {
        // Just delegate to startSession - the stationId is already passed to the connector query
        return startSession(request);
    }

    @Override
    public ChargingSessionDto stopChargingSession(UUID stationId, StopChargingSessionRequest request) {
        // Find the session by transaction ID since StopChargingSessionRequest doesn't have sessionId
        ChargingSession session = sessionRepository.findByTransactionId(request.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Charging session not found with transaction ID: " + request.getTransactionId()));
        
        return endSession(session.getId(), request.getStopReason());
    }

    @Override
    public ChargingSessionDto updateSessionStatus(UUID id, SessionStatus status) {
        ChargingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charging session not found: " + id));
        
        session.setStatus(status);
        session = sessionRepository.save(session);
        
        // Send Kafka event for status update
        sendChargingSessionEvent(session, "UPDATED", null);
        
        return mapToDto(session);
    }

    @Override
    @Transactional
    public ChargingSessionDto startSession(StartChargingSessionRequest request) {
        log.info("Starting charging session for connector: {}", request.getConnectorId());
        
        // Get station ID from the startChargingSession method parameter
        UUID stationId = null;
        if (request.getConnectorId() != null) {
            // Find connector first - we'll need to query by ID
            List<Connector> connectors = connectorRepository.findAll();
            Connector connector = connectors.stream()
                    .filter(c -> c.getConnectorId().equals(request.getConnectorId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + request.getConnectorId()));
            
            if (connector.getStatus() != StationStatus.AVAILABLE) {
                throw new IllegalStateException("Connector is not available: " + connector.getStatus());
            }
            
            stationId = connector.getStation().getId();
            
            // Create new session
            ChargingSession session = ChargingSession.builder()
                    .stationId(stationId)
                    .connectorId(request.getConnectorId())
                    .userId(request.getUserId())
                    .idTag(request.getIdTag())
                    .startTimestamp(LocalDateTime.now())
                    .transactionId(request.getTransactionId())
                    .meterStart(request.getMeterStart())
                    .status(SessionStatus.IN_PROGRESS)
                    .startReason(request.getStartReason())
                    .build();
            
            // Update connector status
            connector.setStatus(StationStatus.CHARGING);
            connectorRepository.save(connector);
            
            // Save session
            session = sessionRepository.save(session);
            
            // Send Kafka event
            sendChargingSessionEvent(session, "STARTED", null);
            
            return mapToDto(session);
        } else {
            throw new IllegalArgumentException("Connector ID is required");
        }
    }

    @Override
    @Transactional
    public ChargingSessionDto updateSession(UUID sessionId, SessionStatus status) {
        log.info("Updating charging session: {} to status: {}", sessionId, status);
        
        ChargingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Charging session not found: " + sessionId));
        
        session.setStatus(status);
        session = sessionRepository.save(session);
        
        // Send Kafka event
        sendChargingSessionEvent(session, "UPDATED", null);
        
        return mapToDto(session);
    }

    @Override
    @Transactional
    public ChargingSessionDto endSession(UUID sessionId, String stopReason) {
        log.info("Ending charging session: {}, reason: {}", sessionId, stopReason);
        
        ChargingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Charging session not found: " + sessionId));
        
        // Can only end sessions that are in progress
        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Session is not in progress: " + session.getStatus());
        }
        
        // Update session
        session.setStatus(SessionStatus.COMPLETED);
        session.setStopTimestamp(LocalDateTime.now());
        session.setStopReason(stopReason);
        
        // If meterStop is provided in the future, it should be set here
        
        // Calculate energy delivered if meter stop is available
        if (session.getMeterStop() != null && session.getMeterStart() != null) {
            BigDecimal totalEnergy = BigDecimal.valueOf(session.getMeterStop() - session.getMeterStart())
                    .divide(BigDecimal.valueOf(1000)); // Convert Wh to kWh
            session.setTotalEnergyKwh(totalEnergy);
        }
        
        session = sessionRepository.save(session);
        
        // Update connector status
        // Ensure that stationId is a UUID and connectorId is an Integer
        UUID stationId = session.getStationId(); // This is already a UUID
        Integer connectorId = session.getConnectorId(); // This is already an Integer
        
        Connector connector = connectorRepository.findByStationIdAndConnectorId(stationId, connectorId)
                .orElseThrow(() -> new IllegalArgumentException("Connector not found for station ID: " + 
                    stationId + " and connector ID: " + connectorId));
        
        connector.setStatus(StationStatus.AVAILABLE);
        connectorRepository.save(connector);
        
        // Send Kafka event
        sendChargingSessionEvent(session, "ENDED", stopReason);
        
        return mapToDto(session);
    }
    
    /**
     * Helper method to send charging session events to Kafka
     */
    private void sendChargingSessionEvent(ChargingSession session, String eventType, String stopReason) {
        try {
            ChargingSessionEvent event = ChargingSessionEvent.builder()
                .eventId(UUID.randomUUID())
                .sessionId(session.getId())
                .stationId(session.getStationId())
                .connectorId(session.getConnectorId())
                .eventType(eventType)
                .userId(session.getUserId())
                .idToken(session.getIdTag())
                .sessionStatus(session.getStatus())
                .startTime(session.getStartTimestamp())
                .endTime(session.getStopTimestamp())
                .timestamp(LocalDateTime.now())
                .energyDeliveredKwh(session.getTotalEnergyKwh())
                .durationSeconds(
                    session.getStopTimestamp() != null 
                        ? Duration.between(session.getStartTimestamp(), session.getStopTimestamp()).getSeconds() 
                        : Duration.between(session.getStartTimestamp(), LocalDateTime.now()).getSeconds()
                )
                .meterStart(session.getMeterStart())
                .meterStop(session.getMeterStop())
                .stopReason(stopReason)
                .build();

            kafkaProducerService.sendChargingSessionEvent(event);
            log.debug("Sent charging session event to Kafka: {}", eventType);
        } catch (Exception e) {
            log.error("Failed to send charging session event for session {}: {}", session.getId(), e.getMessage(), e);
            // Don't throw exception - we want to continue even if the event sending fails
        }
    }
    
    /**
     * Map entity to DTO
     */
    private ChargingSessionDto mapToDto(ChargingSession session) {
        return ChargingSessionDto.builder()
                .id(session.getId())
                .stationId(session.getStationId())
                .connectorId(session.getConnectorId())
                .userId(session.getUserId())
                .idTag(session.getIdTag())
                .startTimestamp(session.getStartTimestamp())
                .stopTimestamp(session.getStopTimestamp())
                .meterStart(session.getMeterStart())
                .meterStop(session.getMeterStop())
                .totalEnergyKwh(session.getTotalEnergyKwh())
                .status(session.getStatus())
                .startReason(session.getStartReason())
                .stopReason(session.getStopReason())
                .transactionId(session.getTransactionId())
                .totalCost(session.getTotalCost())
                .currency(session.getCurrency())
                .build();
    }
} 