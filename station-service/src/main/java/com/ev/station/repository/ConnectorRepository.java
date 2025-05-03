package com.ev.station.repository;

import com.ev.station.model.Connector;
import com.ev.station.model.StationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectorRepository extends JpaRepository<Connector, UUID> {
    
    List<Connector> findByStationId(UUID stationId);
    
    Optional<Connector> findByStationIdAndConnectorId(UUID stationId, Integer connectorId);
    
    List<Connector> findByStationIdAndStatus(UUID stationId, StationStatus status);
    
    int countByStationIdAndStatus(UUID stationId, StationStatus status);
} 