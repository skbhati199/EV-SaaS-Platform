package com.ev.station.service;

import com.ev.station.dto.EVSERegistrationRequest;
import com.ev.station.dto.EVSEResponse;
import com.ev.station.model.EVSE;
import com.ev.station.model.EVSEStatus;
import com.ev.station.repository.EVSERepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EVSEServiceImpl implements EVSEService {

    private final EVSERepository evseRepository;
    
    @Override
    @Transactional
    public EVSEResponse registerEVSE(EVSERegistrationRequest request, UUID ownerId) {
        // Check if EVSE with the same ID already exists
        if (evseRepository.existsByEvseId(request.getEvseId())) {
            throw new IllegalArgumentException("EVSE with ID " + request.getEvseId() + " already exists");
        }
        
        // Create new EVSE entity
        EVSE evse = new EVSE();
        evse.setEvseId(request.getEvseId());
        evse.setSerialNumber(request.getSerialNumber());
        evse.setModel(request.getModel());
        evse.setManufacturer(request.getManufacturer());
        evse.setStatus(EVSEStatus.OFFLINE); // Initial status is offline
        evse.setLocation(request.getLocation());
        evse.setLatitude(request.getLatitude());
        evse.setLongitude(request.getLongitude());
        evse.setMaxPower(request.getMaxPower());
        evse.setConnectorType(request.getConnectorType());
        evse.setOwnerId(ownerId);
        evse.setFirmwareVersion(request.getFirmwareVersion());
        evse.setLastHeartbeat(LocalDateTime.now());
        
        // Save EVSE to database
        EVSE savedEVSE = evseRepository.save(evse);
        log.info("EVSE registered successfully: {}", savedEVSE.getEvseId());
        
        return mapToEVSEResponse(savedEVSE);
    }
    
    @Override
    public Optional<EVSEResponse> getEVSEById(UUID id) {
        return evseRepository.findById(id)
                .map(this::mapToEVSEResponse);
    }
    
    @Override
    public Optional<EVSEResponse> getEVSEByEvseId(String evseId) {
        return evseRepository.findByEvseId(evseId)
                .map(this::mapToEVSEResponse);
    }
    
    @Override
    public List<EVSEResponse> getEVSEsByOwnerId(UUID ownerId) {
        return evseRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToEVSEResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Optional<EVSEResponse> updateHeartbeat(String evseId) {
        Optional<EVSE> evseOptional = evseRepository.findByEvseId(evseId);
        
        if (evseOptional.isPresent()) {
            EVSE evse = evseOptional.get();
            evse.setLastHeartbeat(LocalDateTime.now());
            
            // If EVSE was offline, set it to available when heartbeat is received
            if (evse.getStatus() == EVSEStatus.OFFLINE) {
                evse.setStatus(EVSEStatus.AVAILABLE);
                log.info("EVSE {} status changed from OFFLINE to AVAILABLE", evseId);
            }
            
            EVSE updatedEVSE = evseRepository.save(evse);
            log.debug("Updated heartbeat for EVSE: {}", evseId);
            
            return Optional.of(mapToEVSEResponse(updatedEVSE));
        }
        
        log.warn("Attempted to update heartbeat for non-existent EVSE: {}", evseId);
        return Optional.empty();
    }
    
    /**
     * Maps an EVSE entity to its DTO representation
     * @param evse The EVSE entity
     * @return EVSE response DTO
     */
    private EVSEResponse mapToEVSEResponse(EVSE evse) {
        return EVSEResponse.builder()
                .id(evse.getId())
                .evseId(evse.getEvseId())
                .serialNumber(evse.getSerialNumber())
                .model(evse.getModel())
                .manufacturer(evse.getManufacturer())
                .status(evse.getStatus())
                .location(evse.getLocation())
                .latitude(evse.getLatitude())
                .longitude(evse.getLongitude())
                .maxPower(evse.getMaxPower())
                .connectorType(evse.getConnectorType())
                .ownerId(evse.getOwnerId())
                .lastHeartbeat(evse.getLastHeartbeat())
                .firmwareVersion(evse.getFirmwareVersion())
                .createdAt(evse.getCreatedAt())
                .updatedAt(evse.getUpdatedAt())
                .build();
    }
}
