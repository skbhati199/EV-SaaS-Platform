package com.ev.smartcharging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationDto {
    private UUID id;
    private UUID chargingGroupId;
    private String chargingGroupName;
    private Double maxPowerKW;
    private Double currentPowerKW;
    private Integer priorityLevel;
    private Boolean enabled;
    private Boolean smartChargingEnabled;
    private Integer activeSessionCount;
} 