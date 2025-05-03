package com.ev.station.repository;

import com.ev.station.model.ConnectorMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConnectorMetricsRepository extends JpaRepository<ConnectorMetrics, UUID> {
    
    List<ConnectorMetrics> findByStationIdAndConnectorId(UUID stationId, Integer connectorId);
    
    Page<ConnectorMetrics> findByStationIdAndConnectorId(UUID stationId, Integer connectorId, Pageable pageable);
    
    List<ConnectorMetrics> findByStationIdAndConnectorIdAndTimestampBetween(UUID stationId, Integer connectorId, LocalDateTime start, LocalDateTime end);
    
    ConnectorMetrics findTopByStationIdAndConnectorIdOrderByTimestampDesc(UUID stationId, Integer connectorId);
    
    @Query("SELECT SUM(cm.meterValue) FROM ConnectorMetrics cm WHERE cm.stationId = ?1 AND cm.timestamp BETWEEN ?2 AND ?3")
    Double sumMeterValueByStationIdAndTimestampBetween(UUID stationId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT AVG(cm.powerKw) FROM ConnectorMetrics cm WHERE cm.stationId = ?1 AND cm.connectorId = ?2 AND cm.timestamp BETWEEN ?3 AND ?4")
    Double avgPowerByStationIdAndConnectorIdAndTimestampBetween(UUID stationId, Integer connectorId, LocalDateTime start, LocalDateTime end);
} 