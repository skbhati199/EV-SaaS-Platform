package com.ev.smartcharging.controller;

import com.ev.smartcharging.dto.PowerProfileDto;
import com.ev.smartcharging.model.PriceTier;
import com.ev.smartcharging.service.PowerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/smart-charging/power-profiles")
@RequiredArgsConstructor
public class PowerProfileController {

    private final PowerProfileService powerProfileService;

    @GetMapping
    public ResponseEntity<List<PowerProfileDto>> getAllPowerProfiles() {
        return ResponseEntity.ok(powerProfileService.getAllPowerProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PowerProfileDto> getPowerProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(powerProfileService.getPowerProfileById(id));
    }

    @PostMapping
    public ResponseEntity<PowerProfileDto> createPowerProfile(@RequestBody PowerProfileDto profileDto) {
        PowerProfileDto created = powerProfileService.createPowerProfile(profileDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PowerProfileDto> updatePowerProfile(
            @PathVariable UUID id, @RequestBody PowerProfileDto profileDto) {
        return ResponseEntity.ok(powerProfileService.updatePowerProfile(id, profileDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePowerProfile(@PathVariable UUID id) {
        powerProfileService.deletePowerProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<PowerProfileDto>> getProfilesByStationId(@PathVariable UUID stationId) {
        return ResponseEntity.ok(powerProfileService.getProfilesByStationId(stationId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<PowerProfileDto>> getProfilesByGroupId(@PathVariable UUID groupId) {
        return ResponseEntity.ok(powerProfileService.getProfilesByGroupId(groupId));
    }

    @GetMapping("/price-tier/{priceTier}")
    public ResponseEntity<List<PowerProfileDto>> getProfilesByPriceTier(@PathVariable PriceTier priceTier) {
        return ResponseEntity.ok(powerProfileService.getProfilesByPriceTier(priceTier));
    }

    @GetMapping("/active")
    public ResponseEntity<List<PowerProfileDto>> getActiveProfiles(
            @RequestParam(required = false) UUID stationId,
            @RequestParam(required = false) UUID groupId) {
        return ResponseEntity.ok(powerProfileService.getActiveProfiles(stationId, groupId));
    }
} 