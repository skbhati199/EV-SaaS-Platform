package com.ev.smartcharging.service;

import com.ev.smartcharging.dto.PowerProfileDto;
import com.ev.smartcharging.model.PriceTier;

import java.util.List;
import java.util.UUID;

public interface PowerProfileService {
    List<PowerProfileDto> getAllPowerProfiles();
    PowerProfileDto getPowerProfileById(UUID id);
    PowerProfileDto createPowerProfile(PowerProfileDto powerProfileDto);
    PowerProfileDto updatePowerProfile(UUID id, PowerProfileDto powerProfileDto);
    void deletePowerProfile(UUID id);
    List<PowerProfileDto> getProfilesByStationId(UUID stationId);
    List<PowerProfileDto> getProfilesByGroupId(UUID groupId);
    List<PowerProfileDto> getProfilesByPriceTier(PriceTier priceTier);
    List<PowerProfileDto> getActiveProfiles(UUID stationId, UUID groupId);
} 