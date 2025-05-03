package com.ev.station.service;

import com.ev.station.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StationAuthorizationService {
    
    private final ChargingStationRepository stationRepository;
    
    public boolean isStationOwnedByCpo(UUID stationId, UUID cpoId) {
        return stationRepository.findById(stationId)
                .map(station -> station.getCpoId().equals(cpoId))
                .orElse(false);
    }
} 