package com.ev.schedulerservice.service.impl;

import com.ev.schedulerservice.dto.V2GScheduleDto;
import com.ev.schedulerservice.model.V2GSchedule;
import com.ev.schedulerservice.repository.V2GScheduleRepository;
import com.ev.schedulerservice.service.V2GScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class V2GScheduleServiceImpl implements V2GScheduleService {

    @Autowired
    private V2GScheduleRepository v2gRepository;

    @Override
    public V2GScheduleDto createSchedule(V2GScheduleDto scheduleDto) {
        V2GSchedule schedule = convertToEntity(scheduleDto);
        schedule.setStatus("SCHEDULED");
        schedule.setLastUpdated(LocalDateTime.now());
        schedule = v2gRepository.save(schedule);
        return convertToDto(schedule);
    }

    @Override
    public V2GScheduleDto updateSchedule(Long id, V2GScheduleDto scheduleDto) {
        V2GSchedule existingSchedule = v2gRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("V2G Schedule not found with id: " + id));
        
        BeanUtils.copyProperties(scheduleDto, existingSchedule, "id", "status", "lastUpdated");
        existingSchedule.setLastUpdated(LocalDateTime.now());
        existingSchedule = v2gRepository.save(existingSchedule);
        return convertToDto(existingSchedule);
    }

    @Override
    public V2GScheduleDto getScheduleById(Long id) {
        V2GSchedule schedule = v2gRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("V2G Schedule not found with id: " + id));
        return convertToDto(schedule);
    }

    @Override
    public List<V2GScheduleDto> getAllSchedules() {
        return v2gRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<V2GScheduleDto> getSchedulesByVehicleId(String vehicleId) {
        return v2gRepository.findByVehicleId(vehicleId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<V2GScheduleDto> getSchedulesByStationId(String stationId) {
        return v2gRepository.findByStationId(stationId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<V2GScheduleDto> getSchedulesByUserId(String userId) {
        return v2gRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<V2GScheduleDto> getSchedulesByStatus(String status) {
        return v2gRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<V2GScheduleDto> getSchedulesInTimeRange(LocalDateTime start, LocalDateTime end) {
        return v2gRepository.findSchedulesInTimeRange(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<V2GScheduleDto> getSchedulesForStationInTimeRange(String stationId, LocalDateTime start, LocalDateTime end) {
        return v2gRepository.findSchedulesForStationInTimeRange(stationId, start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSchedule(Long id) {
        v2gRepository.deleteById(id);
    }

    @Override
    public void updateScheduleStatus(Long id, String status) {
        V2GSchedule schedule = v2gRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("V2G Schedule not found with id: " + id));
        schedule.setStatus(status);
        schedule.setLastUpdated(LocalDateTime.now());
        v2gRepository.save(schedule);
    }
    
    private V2GScheduleDto convertToDto(V2GSchedule schedule) {
        V2GScheduleDto dto = new V2GScheduleDto();
        BeanUtils.copyProperties(schedule, dto);
        return dto;
    }
    
    private V2GSchedule convertToEntity(V2GScheduleDto dto) {
        V2GSchedule entity = new V2GSchedule();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
} 