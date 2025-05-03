package com.ev.smartcharging.repository;

import com.ev.smartcharging.model.ChargingSession;
import com.ev.smartcharging.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, UUID> {
    List<ChargingSession> findByStationId(UUID stationId);
    List<ChargingSession> findByUserId(UUID userId);
    List<ChargingSession> findBySessionStatus(SessionStatus status);
    
    @Query("SELECT s FROM ChargingSession s WHERE s.stationId = :stationId AND " +
           "s.sessionStatus IN ('ACTIVE', 'POWER_REDUCED', 'PAUSED')")
    List<ChargingSession> findActiveSessionsByStationId(UUID stationId);
    
    @Query("SELECT SUM(s.allocatedPowerKW) FROM ChargingSession s WHERE " +
           "s.stationId IN :stationIds AND " +
           "s.sessionStatus IN ('ACTIVE', 'POWER_REDUCED')")
    Double getTotalAllocatedPower(List<UUID> stationIds);
    
    List<ChargingSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
} 