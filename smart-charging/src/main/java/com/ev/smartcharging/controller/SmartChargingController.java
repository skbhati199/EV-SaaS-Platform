package com.ev.smartcharging.controller;

import com.ev.smartcharging.dto.ChargingStationDto;
import com.ev.smartcharging.model.SessionStatus;
import com.ev.smartcharging.service.SmartChargingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/smart-charging")
@RequiredArgsConstructor
public class SmartChargingController {

    private final SmartChargingService smartChargingService;

    @PostMapping("/groups/{groupId}/allocate-power")
    public ResponseEntity<Boolean> allocateGroupPower(@PathVariable UUID groupId) {
        boolean success = smartChargingService.allocateGroupPower(groupId);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/sessions/{sessionId}/adjust-power")
    public ResponseEntity<Boolean> adjustSessionPower(
            @PathVariable UUID sessionId, @RequestParam Double powerKW) {
        boolean success = smartChargingService.adjustSessionPower(sessionId, powerKW);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/sessions/{sessionId}/status")
    public ResponseEntity<Boolean> updateSessionStatus(
            @PathVariable UUID sessionId, @RequestParam SessionStatus status) {
        boolean success = smartChargingService.updateSessionStatus(sessionId, status);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/groups/{groupId}/optimal-power")
    public ResponseEntity<List<ChargingStationDto>> calculateOptimalPowerAllocation(@PathVariable UUID groupId) {
        List<ChargingStationDto> stations = smartChargingService.calculateOptimalPowerAllocation(groupId);
        return ResponseEntity.ok(stations);
    }

    @PostMapping("/sessions/{sessionId}/started")
    public ResponseEntity<Boolean> handleSessionStarted(
            @PathVariable UUID sessionId,
            @RequestParam UUID stationId,
            @RequestParam Integer connectorId,
            @RequestParam UUID userId) {
        boolean success = smartChargingService.handleSessionStarted(sessionId, stationId, connectorId, userId);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/sessions/{sessionId}/ended")
    public ResponseEntity<Boolean> handleSessionEnded(@PathVariable UUID sessionId) {
        boolean success = smartChargingService.handleSessionEnded(sessionId);
        return ResponseEntity.ok(success);
    }
} 