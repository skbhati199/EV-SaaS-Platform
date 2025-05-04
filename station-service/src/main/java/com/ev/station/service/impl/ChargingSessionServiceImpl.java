package com.ev.station.service.impl;

import com.ev.station.dto.ChargingSessionDto;
import com.ev.station.dto.StartChargingSessionRequest;
import com.ev.station.dto.StopChargingSessionRequest;
import com.ev.station.dto.event.ChargingSessionEvent;
import com.ev.station.model.ChargingSession;
import com.ev.station.model.Connector;
import com.ev.station.model.EVSEStatus;
import com.ev.station.model.SessionStatus;
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
    public ChargingSessionDto getSessionById(UUID id) {
        ChargingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charging session not found: " + id));
        return mapToDto(session);
    }

    @Override
    public List<ChargingSessionDto> getAllActiveSessions() {
        return sessionRepository.findByStatus(SessionStatus.IN_PROGRESS).stream()
                .map(this::mapToDto)
                .toList();
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
    public Page<ChargingSessionDto> getSessionsByStationIdPaginated(UUID stationId, Pageable pageable) {
        return sessionRepository.findByStationId(stationId, pageable)
                .map(this::mapToDto);
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
                .connectorId(connector.getId())
                .userId(request.getUserId())
                .idTag(request.getIdTag())
                .startTime(LocalDateTime.now())
                .meterStart(request.getMeterStart())
                .currentMeterValue(request.getMeterStart())
                .energyDelivered(BigDecimal.ZERO)
                .status(SessionStatus.IN_PROGRESS)
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
        session.setEndTime(LocalDateTime.now());
        
        // Calculate energy delivered
        BigDecimal energyDelivered = session.getCurrentMeterValue().subtract(session.getMeterStart());
        session.setEnergyDelivered(energyDelivered);
        
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
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .timestamp(LocalDateTime.now())
                .energyDeliveredKwh(session.getEnergyDelivered())
                .durationSeconds(
                    session.getEndTime() != null 
                        ? Duration.between(session.getStartTime(), session.getEndTime()).getSeconds() 
                        : Duration.between(session.getStartTime(), LocalDateTime.now()).getSeconds()
                )
                .meterStart(session.getMeterStart())
                .meterValue(session.getCurrentMeterValue())
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
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .meterStart(session.getMeterStart())
                .meterStop(session.getMeterStop())
                .currentMeterValue(session.getCurrentMeterValue())
                .energyDelivered(session.getEnergyDelivered())
                .status(session.getStatus())
                .build();
    }
} 