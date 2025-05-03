package com.ev.smartcharging.dto;

import com.ev.smartcharging.model.LoadBalancingStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingGroupDto {
    private UUID id;
    private String name;
    private Double maxPowerKW;
    private Double currentPowerKW;
    private Boolean active;
    private LoadBalancingStrategy loadBalancingStrategy;
    private Integer stationCount;
} 