package com.ev.station.service;

import com.ev.station.dto.ChargingSessionDto;
import com.ev.station.dto.StartChargingSessionRequest;
import com.ev.station.dto.StopChargingSessionRequest;
import com.ev.station.model.SessionStatus;
import com.ev.station.dto.event.ChargingSessionEvent;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface ChargingSessionService {
    
    List<ChargingSessionDto> getAllSessions();
    
    ChargingSessionDto getSessionById(UUID id);
    
    ChargingSessionDto getSessionByTransactionId(String transactionId);
    
    List<ChargingSessionDto> getSessionsByStationId(UUID stationId);
    
    List<ChargingSessionDto> getSessionsByUserId(UUID userId);
    
    List<ChargingSessionDto> getSessionsByStationIdAndStatus(UUID stationId, SessionStatus status);
    
    List<ChargingSessionDto> getSessionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<ChargingSessionDto> getSessionsByUserIdAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
    
    ChargingSessionDto startChargingSession(UUID stationId, StartChargingSessionRequest request);
    
    ChargingSessionDto stopChargingSession(UUID stationId, StopChargingSessionRequest request);
    
    ChargingSessionDto updateSessionStatus(UUID id, SessionStatus status);

    /**
     * Start a new charging session
     */
    @Transactional
    ChargingSessionDto startSession(StartChargingSessionRequest request);

    /**
     * Update an existing charging session
     */
    @Transactional
    ChargingSessionDto updateSession(UUID sessionId, SessionStatus status);

    /**
     * End a charging session
     */
    @Transactional
    ChargingSessionDto endSession(UUID sessionId, String stopReason);
} 