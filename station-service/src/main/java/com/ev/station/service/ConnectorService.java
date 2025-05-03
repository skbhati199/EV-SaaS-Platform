package com.ev.station.service;

import com.ev.station.dto.ConnectorDto;
import com.ev.station.dto.CreateConnectorRequest;
import com.ev.station.dto.UpdateConnectorRequest;
import com.ev.station.model.StationStatus;

import java.util.List;
import java.util.UUID;

public interface ConnectorService {
    
    /**
     * Get a connector by its ID
     * @param id The connector ID
     * @return The connector DTO
     */
    ConnectorDto getConnectorById(UUID id);
    
    /**
     * Get all connectors for a station
     * @param stationId The station ID
     * @return List of connector DTOs
     */
    List<ConnectorDto> getConnectorsByStationId(UUID stationId);
    
    /**
     * Get a connector by station ID and connector ID (OCPP ID)
     * @param stationId The station ID
     * @param connectorId The connector ID (from OCPP)
     * @return The connector DTO
     */
    ConnectorDto getConnectorByStationIdAndConnectorId(UUID stationId, Integer connectorId);
    
    /**
     * Get connectors by station ID and status
     * @param stationId The station ID
     * @param status The status to filter by
     * @return List of connector DTOs
     */
    List<ConnectorDto> getConnectorsByStationIdAndStatus(UUID stationId, StationStatus status);
    
    /**
     * Create a new connector
     * @param stationId The station ID
     * @param connectorDto The connector data
     * @return The created connector DTO
     */
    ConnectorDto createConnector(UUID stationId, ConnectorDto connectorDto);
    
    /**
     * Update a connector
     * @param id The connector ID
     * @param connectorDto The connector data
     * @return The updated connector DTO
     */
    ConnectorDto updateConnector(UUID id, ConnectorDto connectorDto);
    
    /**
     * Delete a connector
     * @param id The connector ID
     */
    void deleteConnector(UUID id);
    
    /**
     * Update a connector's status
     * @param stationId The station identifier (serial number)
     * @param connectorId The connector ID (from OCPP)
     * @param status The new status
     * @return The updated connector DTO
     */
    ConnectorDto updateConnectorStatus(String stationId, int connectorId, String status);
    
    /**
     * Count available connectors for a station
     * @param stationId The station ID
     * @return The count of available connectors
     */
    int countAvailableConnectorsByStationId(UUID stationId);
    
    int countTotalConnectorsByStationId(UUID stationId);
} 