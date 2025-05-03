package com.ev.station.repository;

import com.ev.station.model.ChargingSession;
import com.ev.station.model.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, UUID> {
    
    Optional<ChargingSession> findByTransactionId(String transactionId);
    
    List<ChargingSession> findByStationId(UUID stationId);
    
    Page<ChargingSession> findByStationId(UUID stationId, Pageable pageable);
    
    List<ChargingSession> findByUserId(UUID userId);
    
    Page<ChargingSession> findByUserId(UUID userId, Pageable pageable);
    
    List<ChargingSession> findByStationIdAndStatus(UUID stationId, SessionStatus status);
    
    Optional<ChargingSession> findByStationIdAndConnectorIdAndStatus(UUID stationId, Integer connectorId, SessionStatus status);
    
    List<ChargingSession> findByStartTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<ChargingSession> findByUserIdAndStartTimestampBetween(UUID userId, LocalDateTime start, LocalDateTime end);
} 