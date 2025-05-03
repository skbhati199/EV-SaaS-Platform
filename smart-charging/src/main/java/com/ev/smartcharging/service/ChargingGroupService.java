package com.ev.smartcharging.service;

import com.ev.smartcharging.dto.ChargingGroupDto;
import com.ev.smartcharging.model.LoadBalancingStrategy;

import java.util.List;
import java.util.UUID;

public interface ChargingGroupService {
    List<ChargingGroupDto> getAllChargingGroups();
    ChargingGroupDto getChargingGroupById(UUID id);
    ChargingGroupDto createChargingGroup(ChargingGroupDto groupDto);
    ChargingGroupDto updateChargingGroup(UUID id, ChargingGroupDto groupDto);
    void deleteChargingGroup(UUID id);
    List<ChargingGroupDto> getActiveChargingGroups();
    List<ChargingGroupDto> getChargingGroupsByStrategy(LoadBalancingStrategy strategy);
    void addStationToGroup(UUID groupId, UUID stationId);
    void removeStationFromGroup(UUID groupId, UUID stationId);
} 