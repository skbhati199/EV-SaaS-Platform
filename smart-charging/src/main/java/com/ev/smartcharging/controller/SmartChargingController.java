package com.ev.smartcharging.controller;

import com.ev.smartcharging.dto.ChargingStationDto;
import com.ev.smartcharging.model.SessionStatus;
import com.ev.smartcharging.service.KafkaProducerService;
import com.ev.smartcharging.service.SmartChargingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/smart-charging")
@RequiredArgsConstructor
public class SmartChargingController {

    private final SmartChargingService smartChargingService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/groups/{groupId}/allocate-power")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<Boolean> allocateGroupPower(@PathVariable UUID groupId) {
        boolean success = smartChargingService.allocateGroupPower(groupId);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/sessions/{sessionId}/adjust-power")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<Boolean> adjustSessionPower(
            @PathVariable UUID sessionId, @RequestParam Double powerKW) {
        boolean success = smartChargingService.adjustSessionPower(sessionId, powerKW);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/sessions/{sessionId}/status")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<Boolean> updateSessionStatus(
            @PathVariable UUID sessionId, @RequestParam SessionStatus status) {
        boolean success = smartChargingService.updateSessionStatus(sessionId, status);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/groups/{groupId}/optimal-power")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<List<ChargingStationDto>> calculateOptimalPowerAllocation(@PathVariable UUID groupId) {
        List<ChargingStationDto> stations = smartChargingService.calculateOptimalPowerAllocation(groupId);
        return ResponseEntity.ok(stations);
    }

    @PostMapping("/sessions/{sessionId}/started")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<Boolean> handleSessionStarted(
            @PathVariable UUID sessionId,
            @RequestParam UUID stationId,
            @RequestParam Integer connectorId,
            @RequestParam UUID userId) {
        boolean success = smartChargingService.handleSessionStarted(sessionId, stationId, connectorId, userId);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/sessions/{sessionId}/ended")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<Boolean> handleSessionEnded(@PathVariable UUID sessionId) {
        boolean success = smartChargingService.handleSessionEnded(sessionId);
        return ResponseEntity.ok(success);
    }
    
    /**
     * Endpoint for emergency power reduction for a station.
     * This will immediately reduce power to the specified limit for a duration.
     */
    @PostMapping("/stations/{stationId}/emergency-reduction")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<UUID> emergencyPowerReduction(
            @PathVariable UUID stationId,
            @RequestParam(required = false) Integer connectorId,
            @RequestParam Double powerLimitKW,
            @RequestParam(defaultValue = "300") Integer durationSeconds) {
        
        UUID eventId = kafkaProducerService.sendEmergencyPowerReduction(
                stationId, connectorId, powerLimitKW, durationSeconds);
        
        return ResponseEntity.ok(eventId);
    }
    
    /**
     * Endpoint for group-wide emergency power reduction.
     * This will reduce power for all stations in the group.
     */
    @PostMapping("/groups/{groupId}/emergency-reduction")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<List<UUID>> emergencyGroupPowerReduction(
            @PathVariable UUID groupId,
            @RequestParam Double powerReductionPercentage,
            @RequestParam(defaultValue = "300") Integer durationSeconds) {
        
        // Calculate reduced power for each station in the group
        List<ChargingStationDto> stations = smartChargingService.calculateOptimalPowerAllocation(groupId);
        
        List<UUID> eventIds = stations.stream()
                .map(station -> {
                    // Calculate reduced power (at most the specified percentage of max power)
                    double reducedPower = station.getMaxPowerKW() * (100 - powerReductionPercentage) / 100;
                    // Ensure it's not negative
                    reducedPower = Math.max(0.0, reducedPower);
                    
                    // Send emergency reduction command
                    return kafkaProducerService.sendEmergencyPowerReduction(
                            UUID.fromString(station.getId()), 
                            null, // Apply to whole station
                            reducedPower,
                            durationSeconds);
                })
                .toList();
        
        return ResponseEntity.ok(eventIds);
    }
} 