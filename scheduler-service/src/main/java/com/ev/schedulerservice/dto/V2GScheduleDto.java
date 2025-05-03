package com.ev.schedulerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2GScheduleDto {
    private Long id;
    private String userId;
    private String vehicleId;
    private String stationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double energyRequested; // in kWh
    private Double energyDelivered; // in kWh
    private String status; // SCHEDULED, ACTIVE, COMPLETED, CANCELLED
    private Double paymentAmount;
    private String paymentStatus;
    private LocalDateTime lastUpdated;
} 