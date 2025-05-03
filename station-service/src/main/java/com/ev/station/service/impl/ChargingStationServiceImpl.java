package com.ev.station.service.impl;

import com.ev.station.dto.ChargingStationDto;
import com.ev.station.dto.CreateStationRequest;
import com.ev.station.dto.HeartbeatRequest;
import com.ev.station.dto.UpdateStationRequest;
import com.ev.station.model.ChargingStation;
import com.ev.station.model.StationHeartbeat;
import com.ev.station.model.StationStatus;
import com.ev.station.repository.ChargingStationRepository;
import com.ev.station.repository.StationHeartbeatRepository;
import com.ev.station.service.ChargingStationService;
import com.ev.station.service.ConnectorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargingStationServiceImpl implements ChargingStationService {

    private final ChargingStationRepository stationRepository;
    private final StationHeartbeatRepository heartbeatRepository;
    private final ConnectorService connectorService;
    
    @Override
    public List<ChargingStationDto> getAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingStationDto getStationById(UUID id) {
        return stationRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));
    }

    @Override
    public ChargingStationDto getStationBySerialNumber(String serialNumber) {
        return stationRepository.findBySerialNumber(serialNumber)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with serial number: " + serialNumber));
    }

    @Override
    public List<ChargingStationDto> getStationsByStatus(StationStatus status) {
        return stationRepository.findByStatus(status)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChargingStationDto> getStationsByCpoId(UUID cpoId) {
        return stationRepository.findByCpoId(cpoId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChargingStationDto> getStationsNearLocation(Double latitude, Double longitude, Double radiusInKm) {
        // Convert km to meters for the query
        double radiusInMeters = radiusInKm * 1000;
        return stationRepository.findStationsWithinRadius(longitude, latitude, radiusInMeters)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChargingStationDto createStation(CreateStationRequest request) {
        if (stationRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new IllegalArgumentException("A station with serial number " + request.getSerialNumber() + " already exists");
        }
        
        ChargingStation station = ChargingStation.builder()
                .name(request.getName())
                .serialNumber(request.getSerialNumber())
                .model(request.getModel())
                .vendor(request.getVendor())
                .firmwareVersion(request.getFirmwareVersion())
                .locationLatitude(request.getLocationLatitude())
                .locationLongitude(request.getLocationLongitude())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .cpoId(request.getCpoId())
                .status(StationStatus.OFFLINE) // New stations start as offline
                .registrationDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        ChargingStation savedStation = stationRepository.save(station);
        return mapToDto(savedStation);
    }

    @Override
    @Transactional
    public ChargingStationDto updateStation(UUID id, UpdateStationRequest request) {
        ChargingStation station = stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));
        
        if (request.getName() != null) {
            station.setName(request.getName());
        }
        
        if (request.getModel() != null) {
            station.setModel(request.getModel());
        }
        
        if (request.getVendor() != null) {
            station.setVendor(request.getVendor());
        }
        
        if (request.getFirmwareVersion() != null) {
            station.setFirmwareVersion(request.getFirmwareVersion());
        }
        
        if (request.getLocationLatitude() != null) {
            station.setLocationLatitude(request.getLocationLatitude());
        }
        
        if (request.getLocationLongitude() != null) {
            station.setLocationLongitude(request.getLocationLongitude());
        }
        
        if (request.getAddress() != null) {
            station.setAddress(request.getAddress());
        }
        
        if (request.getCity() != null) {
            station.setCity(request.getCity());
        }
        
        if (request.getState() != null) {
            station.setState(request.getState());
        }
        
        if (request.getCountry() != null) {
            station.setCountry(request.getCountry());
        }
        
        if (request.getPostalCode() != null) {
            station.setPostalCode(request.getPostalCode());
        }
        
        if (request.getStatus() != null) {
            station.setStatus(request.getStatus());
        }
        
        station.setUpdatedAt(LocalDateTime.now());
        
        ChargingStation updatedStation = stationRepository.save(station);
        return mapToDto(updatedStation);
    }

    @Override
    @Transactional
    public void deleteStation(UUID id) {
        if (!stationRepository.existsById(id)) {
            throw new EntityNotFoundException("Station not found with id: " + id);
        }
        
        stationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ChargingStationDto processHeartbeat(UUID id, HeartbeatRequest request) {
        ChargingStation station = stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));
        
        // Record heartbeat
        StationHeartbeat heartbeat = StationHeartbeat.builder()
                .stationId(id)
                .timestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                .status(request.getStatus().toString())
                .build();
        
        heartbeatRepository.save(heartbeat);
        
        // Update station status and last heartbeat time
        station.setStatus(request.getStatus());
        station.setLastHeartbeat(LocalDateTime.now());
        station.setUpdatedAt(LocalDateTime.now());
        
        ChargingStation updatedStation = stationRepository.save(station);
        return mapToDto(updatedStation);
    }

    @Override
    @Transactional
    public ChargingStationDto updateStationStatus(UUID id, StationStatus status) {
        ChargingStation station = stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));
        
        station.setStatus(status);
        station.setUpdatedAt(LocalDateTime.now());
        
        ChargingStation updatedStation = stationRepository.save(station);
        return mapToDto(updatedStation);
    }
    
    @Override
    @Transactional
    public ChargingStation registerOrUpdateStation(String stationId) {
        Optional<ChargingStation> stationOpt = stationRepository.findBySerialNumber(stationId);
        
        if (stationOpt.isPresent()) {
            ChargingStation station = stationOpt.get();
            // Update status if offline
            if (station.getStatus() == StationStatus.OFFLINE) {
                station.setStatus(StationStatus.AVAILABLE);
                station = stationRepository.save(station);
            }
            return station;
        } else {
            // Create new station with basic information
            ChargingStation newStation = ChargingStation.builder()
                    .serialNumber(stationId)
                    .name("Station " + stationId)
                    .status(StationStatus.PENDING)
                    .registrationDate(LocalDateTime.now())
                    .lastHeartbeat(LocalDateTime.now())
                    .build();
            
            return stationRepository.save(newStation);
        }
    }
    
    @Override
    @Transactional
    public ChargingStation updateHeartbeat(String stationId) {
        ChargingStation station = stationRepository.findBySerialNumber(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with serial number: " + stationId));
        
        LocalDateTime now = LocalDateTime.now();
        station.setLastHeartbeat(now);
        
        // If station was offline, change status to available
        if (station.getStatus() == StationStatus.OFFLINE) {
            station.setStatus(StationStatus.AVAILABLE);
        }
        
        // Create heartbeat record
        StationHeartbeat heartbeat = StationHeartbeat.builder()
                .stationId(station.getId())
                .timestamp(now)
                .build();
        
        heartbeatRepository.save(heartbeat);
        return stationRepository.save(station);
    }
    
    @Override
    @Transactional
    public ChargingStation updateStationById(UUID id, ChargingStation station) {
        if (!stationRepository.existsById(id)) {
            throw new EntityNotFoundException("Station not found with id: " + id);
        }
        
        station.setId(id); // Ensure ID is set correctly
        return stationRepository.save(station);
    }
    
    @Override
    @Transactional
    public ChargingStationDto updateStationStatus(String stationId, StationStatus status) {
        ChargingStation station = stationRepository.findBySerialNumber(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with serial number: " + stationId));
        
        station.setStatus(status);
        station = stationRepository.save(station);
        
        // Log status change
        log.info("Updated status for station {}: {} -> {}", stationId, station.getStatus(), status);
        
        return mapToDto(station);
    }
    
    private ChargingStationDto mapToDto(ChargingStation station) {
        return ChargingStationDto.builder()
                .id(station.getId())
                .name(station.getName())
                .serialNumber(station.getSerialNumber())
                .model(station.getModel())
                .vendor(station.getVendor())
                .firmwareVersion(station.getFirmwareVersion())
                .locationLatitude(station.getLocationLatitude())
                .locationLongitude(station.getLocationLongitude())
                .address(station.getAddress())
                .city(station.getCity())
                .state(station.getState())
                .country(station.getCountry())
                .postalCode(station.getPostalCode())
                .cpoId(station.getCpoId())
                .status(station.getStatus())
                .lastHeartbeat(station.getLastHeartbeat())
                .registrationDate(station.getRegistrationDate())
                .availableConnectors(connectorService.countAvailableConnectorsByStationId(station.getId()))
                .totalConnectors(connectorService.countTotalConnectorsByStationId(station.getId()))
                .build();
    }
} 