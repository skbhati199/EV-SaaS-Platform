package com.ev.station.repository;

import com.ev.station.model.StationHeartbeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StationHeartbeatRepository extends JpaRepository<StationHeartbeat, UUID> {
    
    List<StationHeartbeat> findByStationId(UUID stationId);
    
    Page<StationHeartbeat> findByStationId(UUID stationId, Pageable pageable);
    
    List<StationHeartbeat> findByStationIdAndTimestampBetween(UUID stationId, LocalDateTime start, LocalDateTime end);
    
    StationHeartbeat findTopByStationIdOrderByTimestampDesc(UUID stationId);
} 