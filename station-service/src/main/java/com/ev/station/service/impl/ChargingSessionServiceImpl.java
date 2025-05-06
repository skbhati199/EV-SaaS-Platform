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
        return startSession(request);
    }

    @Override
    public ChargingSessionDto stopChargingSession(UUID stationId, StopChargingSessionRequest request) {
        return endSession(request.getSessionId(), request.getStopReason());
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
        
        Connector connector = connectorRepository.findById(request.getConnectorId())
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + request.getConnectorId()));
        
        if (connector.getStatus() != EVSEStatus.AVAILABLE) {
            throw new IllegalStateException("Connector is not available: " + connector.getStatus());
        }
        
        // Create new session
        ChargingSession session = ChargingSession.builder()
                .stationId(connector.getStationId())
                .connectorId(request.getConnectorId())
                .userId(request.getUserId())
                .idTag(request.getIdTag())
                .startTimestamp(LocalDateTime.now())
                .transactionId(UUID.randomUUID().toString())
                .meterStart(request.getMeterStart())
                .status(SessionStatus.IN_PROGRESS)
                .startReason(request.getStartReason())
                .build();
        
        // Update connector status
        connector.setStatus(EVSEStatus.CHARGING);
        connectorRepository.save(connector);
        
        // Save session
        session = sessionRepository.save(session);
        
        // Send Kafka event
        sendChargingSessionEvent(session, "STARTED", null);
        
        return mapToDto(session);
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
        Connector connector = connectorRepository.findById(session.getConnectorId())
                .orElseThrow(() -> new IllegalArgumentException("Connector not found: " + session.getConnectorId()));
        
        connector.setStatus(EVSEStatus.AVAILABLE);
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
                .startTime(session.getStartTimestamp())
                .endTime(session.getStopTimestamp())
                .meterStart(session.getMeterStart())
                .meterStop(session.getMeterStop())
                .energyDelivered(session.getTotalEnergyKwh())
                .status(session.getStatus())
                .startReason(session.getStartReason())
                .stopReason(session.getStopReason())
                .transactionId(session.getTransactionId())
                .totalCost(session.getTotalCost())
                .currency(session.getCurrency())
                .build();
    }
} 