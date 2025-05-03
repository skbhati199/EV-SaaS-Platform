package com.ev.station.service;

import com.ev.station.dto.ChargingStationDto;
import com.ev.station.dto.CreateStationRequest;
import com.ev.station.dto.HeartbeatRequest;
import com.ev.station.dto.UpdateStationRequest;
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
} 