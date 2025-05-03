package com.ev.schedulerservice.service;

import com.ev.schedulerservice.dto.V2GScheduleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface V2GScheduleService {
    
    V2GScheduleDto createSchedule(V2GScheduleDto scheduleDto);
    
    V2GScheduleDto updateSchedule(Long id, V2GScheduleDto scheduleDto);
    
    V2GScheduleDto getScheduleById(Long id);
    
    List<V2GScheduleDto> getAllSchedules();
    
    List<V2GScheduleDto> getSchedulesByVehicleId(String vehicleId);
    
    List<V2GScheduleDto> getSchedulesByStationId(String stationId);
    
    List<V2GScheduleDto> getSchedulesByUserId(String userId);
    
    List<V2GScheduleDto> getSchedulesByStatus(String status);
    
    List<V2GScheduleDto> getSchedulesInTimeRange(LocalDateTime start, LocalDateTime end);
    
    List<V2GScheduleDto> getSchedulesForStationInTimeRange(String stationId, LocalDateTime start, LocalDateTime end);
    
    void deleteSchedule(Long id);
    
    void updateScheduleStatus(Long id, String status);
} 