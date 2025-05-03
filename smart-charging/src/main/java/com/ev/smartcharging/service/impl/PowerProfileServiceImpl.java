package com.ev.smartcharging.service.impl;

import com.ev.smartcharging.dto.PowerProfileDto;
import com.ev.smartcharging.model.PowerProfile;
import com.ev.smartcharging.model.PriceTier;
import com.ev.smartcharging.repository.PowerProfileRepository;
import com.ev.smartcharging.service.PowerProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PowerProfileServiceImpl implements PowerProfileService {

    private final PowerProfileRepository powerProfileRepository;

    @Override
    public List<PowerProfileDto> getAllPowerProfiles() {
        return powerProfileRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PowerProfileDto getPowerProfileById(UUID id) {
        return powerProfileRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Power profile not found with id: " + id));
    }

    @Override
    public PowerProfileDto createPowerProfile(PowerProfileDto powerProfileDto) {
        PowerProfile powerProfile = convertToEntity(powerProfileDto);
        PowerProfile savedProfile = powerProfileRepository.save(powerProfile);
        return convertToDto(savedProfile);
    }

    @Override
    public PowerProfileDto updatePowerProfile(UUID id, PowerProfileDto powerProfileDto) {
        PowerProfile existingProfile = powerProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Power profile not found with id: " + id));
        
        // Update fields
        existingProfile.setStationId(powerProfileDto.getStationId());
        existingProfile.setGroupId(powerProfileDto.getGroupId());
        existingProfile.setStartTime(powerProfileDto.getStartTime());
        existingProfile.setEndTime(powerProfileDto.getEndTime());
        existingProfile.setMaxPowerKW(powerProfileDto.getMaxPowerKW());
        existingProfile.setMinPowerKW(powerProfileDto.getMinPowerKW());
        existingProfile.setDayOfWeek(powerProfileDto.getDayOfWeek());
        existingProfile.setPriceTier(powerProfileDto.getPriceTier());
        
        PowerProfile updatedProfile = powerProfileRepository.save(existingProfile);
        return convertToDto(updatedProfile);
    }

    @Override
    public void deletePowerProfile(UUID id) {
        if (!powerProfileRepository.existsById(id)) {
            throw new EntityNotFoundException("Power profile not found with id: " + id);
        }
        powerProfileRepository.deleteById(id);
    }

    @Override
    public List<PowerProfileDto> getProfilesByStationId(UUID stationId) {
        return powerProfileRepository.findByStationId(stationId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PowerProfileDto> getProfilesByGroupId(UUID groupId) {
        return powerProfileRepository.findByGroupId(groupId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PowerProfileDto> getProfilesByPriceTier(PriceTier priceTier) {
        return powerProfileRepository.findByPriceTier(priceTier)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PowerProfileDto> getActiveProfiles(UUID stationId, UUID groupId) {
        // Get current day of week (1-7, where 1 is Monday)
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        String dayOfWeekStr = String.valueOf(dayOfWeek);
        
        // Get current time
        LocalTime currentTime = LocalTime.now();
        
        return powerProfileRepository.findActiveProfiles(stationId, groupId, dayOfWeekStr, currentTime)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private PowerProfileDto convertToDto(PowerProfile powerProfile) {
        return PowerProfileDto.builder()
                .id(powerProfile.getId())
                .stationId(powerProfile.getStationId())
                .groupId(powerProfile.getGroupId())
                .startTime(powerProfile.getStartTime())
                .endTime(powerProfile.getEndTime())
                .maxPowerKW(powerProfile.getMaxPowerKW())
                .minPowerKW(powerProfile.getMinPowerKW())
                .dayOfWeek(powerProfile.getDayOfWeek())
                .priceTier(powerProfile.getPriceTier())
                .build();
    }
    
    private PowerProfile convertToEntity(PowerProfileDto dto) {
        return PowerProfile.builder()
                .id(dto.getId())
                .stationId(dto.getStationId())
                .groupId(dto.getGroupId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .maxPowerKW(dto.getMaxPowerKW())
                .minPowerKW(dto.getMinPowerKW())
                .dayOfWeek(dto.getDayOfWeek())
                .priceTier(dto.getPriceTier())
                .build();
    }
} 