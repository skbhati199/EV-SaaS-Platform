package com.ev.station.service;

import com.ev.station.dto.ChargingStationDto;
import com.ev.station.dto.CreateStationRequest;
import com.ev.station.dto.HeartbeatRequest;
import com.ev.station.dto.UpdateStationRequest;
import com.ev.station.model.ChargingStation;
import com.ev.station.model.StationStatus;

import java.util.List;
import java.util.UUID;

public interface ChargingStationService {
    
    List<ChargingStationDto> getAllStations();
    
    ChargingStationDto getStationById(UUID id);
    
    ChargingStationDto getStationBySerialNumber(String serialNumber);
    
    List<ChargingStationDto> getStationsByStatus(StationStatus status);
    
    List<ChargingStationDto> getStationsByCpoId(UUID cpoId);
    
    List<ChargingStationDto> getStationsNearLocation(Double latitude, Double longitude, Double radiusInKm);
    
    ChargingStationDto createStation(CreateStationRequest request);
    
    ChargingStationDto updateStation(UUID id, UpdateStationRequest request);
    
    void deleteStation(UUID id);
    
    ChargingStationDto processHeartbeat(UUID id, HeartbeatRequest request);
    
    ChargingStationDto updateStationStatus(UUID id, StationStatus status);
    
    /**
     * Registers a new charging station or updates an existing one based on its identifier.
     * This is used during OCPP WebSocket connection establishment.
     *
     * @param stationId The unique identifier of the charging station
     * @return The charging station entity
     */
    ChargingStation registerOrUpdateStation(String stationId);
    
    /**
     * Updates a station's heartbeat timestamp
     *
     * @param stationId The charging station identifier
     * @return The updated charging station entity
     */
    ChargingStation updateHeartbeat(String stationId);
    
    /**
     * Updates a station by its ID using the provided entity
     *
     * @param id The station ID to update
     * @param station The station entity with updated values
     * @return The updated charging station entity
     */
    ChargingStation updateStationById(UUID id, ChargingStation station);
    
    /**
     * Updates a station's status by its identifier
     *
     * @param stationId The charging station identifier (serial number)
     * @param status The new status
     * @return The updated charging station DTO
     */
    ChargingStationDto updateStationStatus(String stationId, StationStatus status);
} 