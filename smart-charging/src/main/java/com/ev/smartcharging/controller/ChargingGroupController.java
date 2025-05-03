package com.ev.smartcharging.controller;

import com.ev.smartcharging.dto.ChargingGroupDto;
import com.ev.smartcharging.model.LoadBalancingStrategy;
import com.ev.smartcharging.service.ChargingGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/smart-charging/groups")
@RequiredArgsConstructor
public class ChargingGroupController {

    private final ChargingGroupService chargingGroupService;

    @GetMapping
    public ResponseEntity<List<ChargingGroupDto>> getAllChargingGroups() {
        return ResponseEntity.ok(chargingGroupService.getAllChargingGroups());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ChargingGroupDto>> getActiveChargingGroups() {
        return ResponseEntity.ok(chargingGroupService.getActiveChargingGroups());
    }

    @GetMapping("/strategy/{strategy}")
    public ResponseEntity<List<ChargingGroupDto>> getChargingGroupsByStrategy(
            @PathVariable LoadBalancingStrategy strategy) {
        return ResponseEntity.ok(chargingGroupService.getChargingGroupsByStrategy(strategy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingGroupDto> getChargingGroupById(@PathVariable UUID id) {
        return ResponseEntity.ok(chargingGroupService.getChargingGroupById(id));
    }

    @PostMapping
    public ResponseEntity<ChargingGroupDto> createChargingGroup(@RequestBody ChargingGroupDto groupDto) {
        ChargingGroupDto created = chargingGroupService.createChargingGroup(groupDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingGroupDto> updateChargingGroup(
            @PathVariable UUID id, @RequestBody ChargingGroupDto groupDto) {
        return ResponseEntity.ok(chargingGroupService.updateChargingGroup(id, groupDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargingGroup(@PathVariable UUID id) {
        chargingGroupService.deleteChargingGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/stations/{stationId}")
    public ResponseEntity<Void> addStationToGroup(
            @PathVariable UUID groupId, @PathVariable UUID stationId) {
        chargingGroupService.addStationToGroup(groupId, stationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/stations/{stationId}")
    public ResponseEntity<Void> removeStationFromGroup(
            @PathVariable UUID groupId, @PathVariable UUID stationId) {
        chargingGroupService.removeStationFromGroup(groupId, stationId);
        return ResponseEntity.noContent().build();
    }
} 