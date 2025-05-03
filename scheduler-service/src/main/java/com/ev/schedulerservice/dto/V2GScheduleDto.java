package com.ev.schedulerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class V2GScheduleDto {
    private Long id;
    private String vehicleId;
    private String stationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double powerKw;
    private String scheduleType;
    private String status;
    private LocalDateTime lastUpdated;
    private String userId;
} 