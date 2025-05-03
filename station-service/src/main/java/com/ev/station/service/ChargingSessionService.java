package com.ev.station.service;

import com.ev.station.dto.ChargingSessionDto;
import com.ev.station.dto.StartChargingSessionRequest;
import com.ev.station.dto.StopChargingSessionRequest;
import com.ev.station.model.SessionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
} 