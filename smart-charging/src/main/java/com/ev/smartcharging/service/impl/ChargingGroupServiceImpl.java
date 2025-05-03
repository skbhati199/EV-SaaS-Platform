package com.ev.smartcharging.service.impl;

import com.ev.smartcharging.dto.ChargingGroupDto;
import com.ev.smartcharging.model.ChargingGroup;
import com.ev.smartcharging.model.ChargingStation;
import com.ev.smartcharging.model.LoadBalancingStrategy;
import com.ev.smartcharging.repository.ChargingGroupRepository;
import com.ev.smartcharging.repository.ChargingStationRepository;
import com.ev.smartcharging.service.ChargingGroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargingGroupServiceImpl implements ChargingGroupService {

    private final ChargingGroupRepository chargingGroupRepository;
    private final ChargingStationRepository chargingStationRepository;

    @Override
    public List<ChargingGroupDto> getAllChargingGroups() {
        return chargingGroupRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingGroupDto getChargingGroupById(UUID id) {
        ChargingGroup group = chargingGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging group not found with id: " + id));
        return convertToDto(group);
    }

    @Override
    @Transactional
    public ChargingGroupDto createChargingGroup(ChargingGroupDto groupDto) {
        ChargingGroup group = ChargingGroup.builder()
                .name(groupDto.getName())
                .maxPowerKW(groupDto.getMaxPowerKW())
                .currentPowerKW(0.0) // Initialize with 0
                .active(groupDto.getActive() != null ? groupDto.getActive() : true)
                .loadBalancingStrategy(groupDto.getLoadBalancingStrategy())
                .build();
        
        ChargingGroup savedGroup = chargingGroupRepository.save(group);
        return convertToDto(savedGroup);
    }

    @Override
    @Transactional
    public ChargingGroupDto updateChargingGroup(UUID id, ChargingGroupDto groupDto) {
        ChargingGroup existingGroup = chargingGroupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging group not found with id: " + id));
        
        existingGroup.setName(groupDto.getName());
        existingGroup.setMaxPowerKW(groupDto.getMaxPowerKW());
        existingGroup.setActive(groupDto.getActive());
        existingGroup.setLoadBalancingStrategy(groupDto.getLoadBalancingStrategy());
        
        ChargingGroup updatedGroup = chargingGroupRepository.save(existingGroup);
        return convertToDto(updatedGroup);
    }

    @Override
    @Transactional
    public void deleteChargingGroup(UUID id) {
        if (!chargingGroupRepository.existsById(id)) {
            throw new EntityNotFoundException("Charging group not found with id: " + id);
        }
        
        // Remove all stations from the group first
        List<ChargingStation> stations = chargingStationRepository.findByChargingGroupId(id);
        stations.forEach(station -> {
            station.setChargingGroup(null);
            chargingStationRepository.save(station);
        });
        
        chargingGroupRepository.deleteById(id);
    }

    @Override
    public List<ChargingGroupDto> getActiveChargingGroups() {
        return chargingGroupRepository.findByActive(true).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChargingGroupDto> getChargingGroupsByStrategy(LoadBalancingStrategy strategy) {
        return chargingGroupRepository.findByLoadBalancingStrategy(strategy.toString()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addStationToGroup(UUID groupId, UUID stationId) {
        ChargingGroup group = chargingGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Charging group not found with id: " + groupId));
        
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Charging station not found with id: " + stationId));
        
        station.setChargingGroup(group);
        chargingStationRepository.save(station);
    }

    @Override
    @Transactional
    public void removeStationFromGroup(UUID groupId, UUID stationId) {
        ChargingGroup group = chargingGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Charging group not found with id: " + groupId));
        
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Charging station not found with id: " + stationId));
        
        if (station.getChargingGroup() != null && station.getChargingGroup().getId().equals(groupId)) {
            station.setChargingGroup(null);
            chargingStationRepository.save(station);
        } else {
            throw new IllegalStateException("Station does not belong to the specified group");
        }
    }
    
    private ChargingGroupDto convertToDto(ChargingGroup group) {
        int stationCount = group.getStations() != null ? group.getStations().size() : 0;
        
        return ChargingGroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .maxPowerKW(group.getMaxPowerKW())
                .currentPowerKW(group.getCurrentPowerKW())
                .active(group.getActive())
                .loadBalancingStrategy(group.getLoadBalancingStrategy())
                .stationCount(stationCount)
                .build();
    }
} 