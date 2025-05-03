package com.ev.station.service;

import com.ev.station.dto.ConnectorDto;
import com.ev.station.dto.CreateConnectorRequest;
import com.ev.station.dto.UpdateConnectorRequest;
import com.ev.station.model.StationStatus;

import java.util.List;
import java.util.UUID;

public interface ConnectorService {
    
    List<ConnectorDto> getConnectorsByStationId(UUID stationId);
    
    ConnectorDto getConnectorById(UUID id);
    
    ConnectorDto getConnectorByStationIdAndConnectorId(UUID stationId, Integer connectorId);
    
    List<ConnectorDto> getConnectorsByStationIdAndStatus(UUID stationId, StationStatus status);
    
    ConnectorDto createConnector(UUID stationId, CreateConnectorRequest request);
    
    ConnectorDto updateConnector(UUID id, UpdateConnectorRequest request);
    
    void deleteConnector(UUID id);
    
    ConnectorDto updateConnectorStatus(UUID id, StationStatus status);
    
    int countAvailableConnectorsByStationId(UUID stationId);
    
    int countTotalConnectorsByStationId(UUID stationId);
} 