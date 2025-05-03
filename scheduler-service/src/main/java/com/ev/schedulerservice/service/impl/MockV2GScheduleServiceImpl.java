package com.ev.schedulerservice.service.impl;

import com.ev.schedulerservice.dto.V2GScheduleDto;
import com.ev.schedulerservice.service.V2GScheduleService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Profile("docker")
@Primary
public class MockV2GScheduleServiceImpl implements V2GScheduleService {
    
    private final Map<Long, V2GScheduleDto> schedulesMap = new HashMap<>();
    private Long nextId = 1L;
    
    // Initialize with some sample data
    public MockV2GScheduleServiceImpl() {
        // Create some sample schedules
        V2GScheduleDto schedule1 = new V2GScheduleDto();
        schedule1.setId(nextId++);
        schedule1.setUserId("user-001");
        schedule1.setVehicleId("vehicle-001");
        schedule1.setStationId("station-001");
        schedule1.setStartTime(LocalDateTime.now().plusHours(1));
        schedule1.setEndTime(LocalDateTime.now().plusHours(3));
        schedule1.setEnergyRequested(25.0);
        schedule1.setEnergyDelivered(0.0);
        schedule1.setStatus("SCHEDULED");
        schedule1.setPaymentAmount(0.0);
        schedule1.setPaymentStatus("PENDING");
        schedule1.setLastUpdated(LocalDateTime.now().minusHours(2));
        
        V2GScheduleDto schedule2 = new V2GScheduleDto();
        schedule2.setId(nextId++);
        schedule2.setUserId("user-002");
        schedule2.setVehicleId("vehicle-002");
        schedule2.setStationId("station-002");
        schedule2.setStartTime(LocalDateTime.now().minusHours(1));
        schedule2.setEndTime(LocalDateTime.now().plusHours(1));
        schedule2.setEnergyRequested(15.0);
        schedule2.setEnergyDelivered(7.5);
        schedule2.setStatus("ACTIVE");
        schedule2.setPaymentAmount(0.0);
        schedule2.setPaymentStatus("PENDING");
        schedule2.setLastUpdated(LocalDateTime.now().minusMinutes(30));
        
        V2GScheduleDto schedule3 = new V2GScheduleDto();
        schedule3.setId(nextId++);
        schedule3.setUserId("user-003");
        schedule3.setVehicleId("vehicle-003");
        schedule3.setStationId("station-001");
        schedule3.setStartTime(LocalDateTime.now().minusHours(3));
        schedule3.setEndTime(LocalDateTime.now().minusHours(1));
        schedule3.setEnergyRequested(30.0);
        schedule3.setEnergyDelivered(30.0);
        schedule3.setStatus("COMPLETED");
        schedule3.setPaymentAmount(15.0);
        schedule3.setPaymentStatus("PAID");
        schedule3.setLastUpdated(LocalDateTime.now().minusHours(1));
        
        schedulesMap.put(schedule1.getId(), schedule1);
        schedulesMap.put(schedule2.getId(), schedule2);
        schedulesMap.put(schedule3.getId(), schedule3);
    }
    
    @Override
    public V2GScheduleDto createSchedule(V2GScheduleDto scheduleDto) {
        scheduleDto.setId(nextId++);
        scheduleDto.setStatus("SCHEDULED");
        scheduleDto.setLastUpdated(LocalDateTime.now());
        schedulesMap.put(scheduleDto.getId(), scheduleDto);
        return scheduleDto;
    }
    
    @Override
    public V2GScheduleDto updateSchedule(Long id, V2GScheduleDto scheduleDto) {
        if (!schedulesMap.containsKey(id)) {
            throw new RuntimeException("V2G Schedule not found with id: " + id);
        }
        
        V2GScheduleDto existingSchedule = schedulesMap.get(id);
        existingSchedule.setVehicleId(scheduleDto.getVehicleId());
        existingSchedule.setStationId(scheduleDto.getStationId());
        existingSchedule.setStartTime(scheduleDto.getStartTime());
        existingSchedule.setEndTime(scheduleDto.getEndTime());
        existingSchedule.setEnergyRequested(scheduleDto.getEnergyRequested());
        existingSchedule.setLastUpdated(LocalDateTime.now());
        
        return existingSchedule;
    }
    
    @Override
    public V2GScheduleDto getScheduleById(Long id) {
        if (!schedulesMap.containsKey(id)) {
            throw new RuntimeException("V2G Schedule not found with id: " + id);
        }
        return schedulesMap.get(id);
    }
    
    @Override
    public List<V2GScheduleDto> getAllSchedules() {
        return new ArrayList<>(schedulesMap.values());
    }
    
    @Override
    public List<V2GScheduleDto> getSchedulesByVehicleId(String vehicleId) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getVehicleId().equals(vehicleId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GScheduleDto> getSchedulesByStationId(String stationId) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getStationId().equals(stationId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GScheduleDto> getSchedulesByUserId(String userId) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GScheduleDto> getSchedulesByStatus(String status) {
        return schedulesMap.values().stream()
                .filter(schedule -> schedule.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GScheduleDto> getSchedulesInTimeRange(LocalDateTime start, LocalDateTime end) {
        return schedulesMap.values().stream()
                .filter(schedule -> 
                    (schedule.getStartTime().isAfter(start) || schedule.getStartTime().isEqual(start)) && 
                    (schedule.getEndTime().isBefore(end) || schedule.getEndTime().isEqual(end)))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<V2GScheduleDto> getSchedulesForStationInTimeRange(String stationId, LocalDateTime start, LocalDateTime end) {
        return schedulesMap.values().stream()
                .filter(schedule -> 
                    schedule.getStationId().equals(stationId) &&
                    (schedule.getStartTime().isAfter(start) || schedule.getStartTime().isEqual(start)) && 
                    (schedule.getEndTime().isBefore(end) || schedule.getEndTime().isEqual(end)))
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteSchedule(Long id) {
        if (!schedulesMap.containsKey(id)) {
            throw new RuntimeException("V2G Schedule not found with id: " + id);
        }
        schedulesMap.remove(id);
    }
    
    @Override
    public void updateScheduleStatus(Long id, String status) {
        if (!schedulesMap.containsKey(id)) {
            throw new RuntimeException("V2G Schedule not found with id: " + id);
        }
        V2GScheduleDto schedule = schedulesMap.get(id);
        schedule.setStatus(status);
        schedule.setLastUpdated(LocalDateTime.now());
    }
} 