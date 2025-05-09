package com.ev.smartcharging.controller;

import com.ev.smartcharging.dto.PowerProfileDto;
import com.ev.smartcharging.model.PriceTier;
import com.ev.smartcharging.service.PowerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/smart-charging/power-profiles")
@RequiredArgsConstructor
public class PowerProfileController {

    private final PowerProfileService powerProfileService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<List<PowerProfileDto>> getAllPowerProfiles() {
        return ResponseEntity.ok(powerProfileService.getAllPowerProfiles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<PowerProfileDto> getPowerProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(powerProfileService.getPowerProfileById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<PowerProfileDto> createPowerProfile(@RequestBody PowerProfileDto profileDto) {
        PowerProfileDto created = powerProfileService.createPowerProfile(profileDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<PowerProfileDto> updatePowerProfile(
            @PathVariable UUID id, @RequestBody PowerProfileDto profileDto) {
        return ResponseEntity.ok(powerProfileService.updatePowerProfile(id, profileDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<Void> deletePowerProfile(@PathVariable UUID id) {
        powerProfileService.deletePowerProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/station/{stationId}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<List<PowerProfileDto>> getProfilesByStationId(@PathVariable UUID stationId) {
        return ResponseEntity.ok(powerProfileService.getProfilesByStationId(stationId));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<List<PowerProfileDto>> getProfilesByGroupId(@PathVariable UUID groupId) {
        return ResponseEntity.ok(powerProfileService.getProfilesByGroupId(groupId));
    }

    @GetMapping("/price-tier/{priceTier}")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<List<PowerProfileDto>> getProfilesByPriceTier(@PathVariable PriceTier priceTier) {
        return ResponseEntity.ok(powerProfileService.getProfilesByPriceTier(priceTier));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ROLE_admin', 'ROLE_operator')")
    public ResponseEntity<List<PowerProfileDto>> getActiveProfiles(
            @RequestParam(required = false) UUID stationId,
            @RequestParam(required = false) UUID groupId) {
        return ResponseEntity.ok(powerProfileService.getActiveProfiles(stationId, groupId));
    }
} 