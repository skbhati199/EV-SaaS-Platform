package com.ev.schedulerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "v2g_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class V2GSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleId;

    @Column(nullable = false)
    private String stationId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Double powerKw; // Positive for charging, negative for discharging

    @Column
    private String scheduleType; // PEAK_SHAVING, GRID_BALANCING, DEMAND_RESPONSE

    @Column
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, FAILED

    @Column
    private LocalDateTime lastUpdated;
    
    @Column
    private String userId; // The user who owns the vehicle
} 