package com.ev.smartcharging.dto;

import com.ev.smartcharging.model.PriceTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerProfileDto {
    private UUID id;
    private UUID stationId;
    private UUID groupId;
    private String stationName;
    private String groupName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double maxPowerKW;
    private Double minPowerKW;
    private String dayOfWeek;
    private PriceTier priceTier;
} 