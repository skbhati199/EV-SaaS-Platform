package com.ev.station.service.impl;

import com.ev.station.dto.ConnectorDto;
import com.ev.station.model.ChargingStation;
import com.ev.station.model.Connector;
import com.ev.station.model.ConnectorType;
import com.ev.station.model.PowerType;
import com.ev.station.model.StationStatus;
import com.ev.station.repository.ChargingStationRepository;
import com.ev.station.repository.ConnectorRepository;
import com.ev.station.service.ConnectorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectorServiceImpl implements ConnectorService {

    private final ChargingStationRepository stationRepository;
    private final ConnectorRepository connectorRepository;

    @Override
    @Transactional(readOnly = true)
    public ConnectorDto getConnectorById(UUID id) {
        Connector connector = connectorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Connector not found with id: " + id));
        return mapToDto(connector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectorDto> getConnectorsByStationId(UUID stationId) {
        List<Connector> connectors = connectorRepository.findByStationId(stationId);
        return connectors.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConnectorDto getConnectorByStationIdAndConnectorId(UUID stationId, Integer connectorId) {
        Connector connector = connectorRepository.findByStationIdAndConnectorId(stationId, connectorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Connector not found with stationId: " + stationId + " and connectorId: " + connectorId));
        return mapToDto(connector);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectorDto> getConnectorsByStationIdAndStatus(UUID stationId, StationStatus status) {
        List<Connector> connectors = connectorRepository.findByStationIdAndStatus(stationId, status);
        return connectors.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConnectorDto createConnector(UUID stationId, ConnectorDto connectorDto) {
        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + stationId));
        
        Connector connector = Connector.builder()
                .station(station)
                .connectorId(connectorDto.getConnectorId())
                .connectorType(connectorDto.getConnectorType())
                .powerType(connectorDto.getPowerType())
                .maxVoltage(connectorDto.getMaxVoltage())
                .maxAmperage(connectorDto.getMaxAmperage())
                .maxPowerKw(connectorDto.getMaxPowerKw())
                .status(connectorDto.getStatus())
                .build();
        
        connector = connectorRepository.save(connector);
        return mapToDto(connector);
    }

    @Override
    @Transactional
    public ConnectorDto updateConnector(UUID id, ConnectorDto connectorDto) {
        Connector connector = connectorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Connector not found with id: " + id));
        
        connector.setConnectorType(connectorDto.getConnectorType());
        connector.setPowerType(connectorDto.getPowerType());
        connector.setMaxVoltage(connectorDto.getMaxVoltage());
        connector.setMaxAmperage(connectorDto.getMaxAmperage());
        connector.setMaxPowerKw(connectorDto.getMaxPowerKw());
        connector.setStatus(connectorDto.getStatus());
        
        connector = connectorRepository.save(connector);
        return mapToDto(connector);
    }

    @Override
    @Transactional
    public void deleteConnector(UUID id) {
        if (!connectorRepository.existsById(id)) {
            throw new EntityNotFoundException("Connector not found with id: " + id);
        }
        connectorRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ConnectorDto updateConnectorStatus(String stationId, int connectorId, String status) {
        // Find the station first
        ChargingStation station = stationRepository.findBySerialNumber(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with serial number: " + stationId));
        
        // Find the connector
        Connector connector = connectorRepository.findByStationIdAndConnectorId(station.getId(), connectorId)
                .orElseGet(() -> {
                    // Auto-create connector if not found
                    log.info("Connector {} not found for station {}, creating new connector", connectorId, stationId);
                    
                    Connector newConnector = Connector.builder()
                            .station(station)
                            .connectorId(connectorId)
                            .connectorType(ConnectorType.UNKNOWN) // Default, will be updated later
                            .powerType(PowerType.AC) // Default, will be updated later
                            .status(StationStatus.AVAILABLE)
                            .lastStatusUpdate(LocalDateTime.now())
                            .build();
                    
                    return connectorRepository.save(newConnector);
                });
        
        // Map OCPP status to StationStatus
        StationStatus newStatus;
        try {
            newStatus = mapOcppStatusToStationStatus(status);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown OCPP status: {}, defaulting to UNKNOWN", status);
            newStatus = StationStatus.UNKNOWN;
        }
        
        // Update connector status
        log.info("Updating connector {} status for station {} from {} to {}", 
                connectorId, stationId, connector.getStatus(), newStatus);
        
        connector.setStatus(newStatus);
        connector.setLastStatusUpdate(LocalDateTime.now());
        connector = connectorRepository.save(connector);
        
        return mapToDto(connector);
    }

    @Override
    @Transactional(readOnly = true)
    public int countAvailableConnectorsByStationId(UUID stationId) {
        return connectorRepository.countByStationIdAndStatus(stationId, StationStatus.AVAILABLE);
    }
    
    /**
     * Maps OCPP status string to StationStatus enum
     * @param ocppStatus The OCPP status string
     * @return The corresponding StationStatus
     */
    private StationStatus mapOcppStatusToStationStatus(String ocppStatus) {
        if (ocppStatus == null) {
            return StationStatus.UNKNOWN;
        }
        
        switch (ocppStatus.toUpperCase()) {
            case "AVAILABLE":
                return StationStatus.AVAILABLE;
            case "OCCUPIED":
            case "CHARGING":
                return StationStatus.OCCUPIED;
            case "RESERVED":
                return StationStatus.RESERVED;
            case "UNAVAILABLE":
                return StationStatus.UNAVAILABLE;
            case "FAULTED":
            case "INOPERATIVE":
                return StationStatus.FAULTED;
            default:
                return StationStatus.UNKNOWN;
        }
    }
    
    /**
     * Maps a Connector entity to a ConnectorDto
     * @param connector The Connector entity
     * @return The ConnectorDto
     */
    private ConnectorDto mapToDto(Connector connector) {
        return ConnectorDto.builder()
                .id(connector.getId())
                .stationId(connector.getStation().getId())
                .connectorId(connector.getConnectorId())
                .connectorType(connector.getConnectorType())
                .powerType(connector.getPowerType())
                .maxVoltage(connector.getMaxVoltage())
                .maxAmperage(connector.getMaxAmperage())
                .maxPowerKw(connector.getMaxPowerKw())
                .status(connector.getStatus())
                .lastStatusUpdate(connector.getLastStatusUpdate())
                .build();
    }
} 