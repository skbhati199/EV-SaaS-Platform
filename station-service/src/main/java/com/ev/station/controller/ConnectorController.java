package com.ev.station.controller;

import com.ev.station.dto.ConnectorDto;
import com.ev.station.dto.CreateConnectorRequest;
import com.ev.station.dto.UpdateConnectorRequest;
import com.ev.station.model.StationStatus;
import com.ev.station.service.ConnectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stations/{stationId}/connectors")
@RequiredArgsConstructor
public class ConnectorController {
    
    private final ConnectorService connectorService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<List<ConnectorDto>> getConnectorsByStationId(@PathVariable UUID stationId) {
        return ResponseEntity.ok(connectorService.getConnectorsByStationId(stationId));
    }
    
    @GetMapping("/{connectorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<ConnectorDto> getConnectorByStationIdAndConnectorId(
            @PathVariable UUID stationId,
            @PathVariable Integer connectorId) {
        return ResponseEntity.ok(connectorService.getConnectorByStationIdAndConnectorId(stationId, connectorId));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<List<ConnectorDto>> getConnectorsByStationIdAndStatus(
            @PathVariable UUID stationId,
            @PathVariable StationStatus status) {
        return ResponseEntity.ok(connectorService.getConnectorsByStationIdAndStatus(stationId, status));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and @stationAuthorizationService.isStationOwnedByCpo(#stationId, principal.id)")
    public ResponseEntity<ConnectorDto> createConnector(
            @PathVariable UUID stationId,
            @Valid @RequestBody CreateConnectorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(connectorService.createConnector(stationId, request));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and @stationAuthorizationService.isStationOwnedByCpo(#stationId, principal.id)")
    public ResponseEntity<ConnectorDto> updateConnector(
            @PathVariable UUID stationId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateConnectorRequest request) {
        return ResponseEntity.ok(connectorService.updateConnector(id, request));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and @stationAuthorizationService.isStationOwnedByCpo(#stationId, principal.id)")
    public ResponseEntity<Void> deleteConnector(
            @PathVariable UUID stationId,
            @PathVariable UUID id) {
        connectorService.deleteConnector(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CPO') and @stationAuthorizationService.isStationOwnedByCpo(#stationId, principal.id)")
    public ResponseEntity<ConnectorDto> updateConnectorStatus(
            @PathVariable UUID stationId,
            @PathVariable UUID id,
            @RequestParam StationStatus status) {
        return ResponseEntity.ok(connectorService.updateConnectorStatus(id, status));
    }
} 