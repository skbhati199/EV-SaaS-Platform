package com.ev.station.controller;

import com.ev.station.dto.ChargingStationDto;
import com.ev.station.dto.CreateStationRequest;
import com.ev.station.dto.HeartbeatRequest;
import com.ev.station.dto.UpdateStationRequest;
import com.ev.station.model.StationStatus;
import com.ev.station.service.ChargingStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class ChargingStationController {
    
    private final ChargingStationService stationService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<List<ChargingStationDto>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<ChargingStationDto> getStationById(@PathVariable UUID id) {
        return ResponseEntity.ok(stationService.getStationById(id));
    }
    
    @GetMapping("/serial/{serialNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<ChargingStationDto> getStationBySerialNumber(@PathVariable String serialNumber) {
        return ResponseEntity.ok(stationService.getStationBySerialNumber(serialNumber));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<List<ChargingStationDto>> getStationsByStatus(@PathVariable StationStatus status) {
        return ResponseEntity.ok(stationService.getStationsByStatus(status));
    }
    
    @GetMapping("/cpo/{cpoId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and principal.id == #cpoId")
    public ResponseEntity<List<ChargingStationDto>> getStationsByCpoId(@PathVariable UUID cpoId) {
        return ResponseEntity.ok(stationService.getStationsByCpoId(cpoId));
    }
    
    @GetMapping("/nearby")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<List<ChargingStationDto>> getStationsNearLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusInKm) {
        return ResponseEntity.ok(stationService.getStationsNearLocation(latitude, longitude, radiusInKm));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO')")
    public ResponseEntity<ChargingStationDto> createStation(@Valid @RequestBody CreateStationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.createStation(request));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and @stationAuthorizationService.isStationOwnedByCpo(#id, principal.id)")
    public ResponseEntity<ChargingStationDto> updateStation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStationRequest request) {
        return ResponseEntity.ok(stationService.updateStation(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and @stationAuthorizationService.isStationOwnedByCpo(#id, principal.id)")
    public ResponseEntity<Void> deleteStation(@PathVariable UUID id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/heartbeat")
    // No security on heartbeat endpoint since it's used by stations
    public ResponseEntity<ChargingStationDto> processHeartbeat(
            @PathVariable UUID id,
            @Valid @RequestBody HeartbeatRequest request) {
        return ResponseEntity.ok(stationService.processHeartbeat(id, request));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and @stationAuthorizationService.isStationOwnedByCpo(#id, principal.id)")
    public ResponseEntity<ChargingStationDto> updateStationStatus(
            @PathVariable UUID id,
            @RequestParam StationStatus status) {
        return ResponseEntity.ok(stationService.updateStationStatus(id, status));
    }
} 