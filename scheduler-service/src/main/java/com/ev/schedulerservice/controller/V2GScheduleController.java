package com.ev.schedulerservice.controller;

import com.ev.schedulerservice.dto.V2GScheduleDto;
import com.ev.schedulerservice.service.V2GScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scheduler/v2g")
public class V2GScheduleController {

    @Autowired
    private V2GScheduleService v2gService;

    @PostMapping("/schedules")
    public ResponseEntity<V2GScheduleDto> createSchedule(@RequestBody V2GScheduleDto scheduleDto) {
        return new ResponseEntity<>(v2gService.createSchedule(scheduleDto), HttpStatus.CREATED);
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<V2GScheduleDto> updateSchedule(@PathVariable Long id, @RequestBody V2GScheduleDto scheduleDto) {
        return ResponseEntity.ok(v2gService.updateSchedule(id, scheduleDto));
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<V2GScheduleDto> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(v2gService.getScheduleById(id));
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<V2GScheduleDto>> getAllSchedules() {
        return ResponseEntity.ok(v2gService.getAllSchedules());
    }

    @GetMapping("/schedules/vehicle/{vehicleId}")
    public ResponseEntity<List<V2GScheduleDto>> getSchedulesByVehicleId(@PathVariable String vehicleId) {
        return ResponseEntity.ok(v2gService.getSchedulesByVehicleId(vehicleId));
    }

    @GetMapping("/schedules/station/{stationId}")
    public ResponseEntity<List<V2GScheduleDto>> getSchedulesByStationId(@PathVariable String stationId) {
        return ResponseEntity.ok(v2gService.getSchedulesByStationId(stationId));
    }

    @GetMapping("/schedules/user/{userId}")
    public ResponseEntity<List<V2GScheduleDto>> getSchedulesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(v2gService.getSchedulesByUserId(userId));
    }

    @GetMapping("/schedules/status/{status}")
    public ResponseEntity<List<V2GScheduleDto>> getSchedulesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(v2gService.getSchedulesByStatus(status));
    }

    @GetMapping("/schedules/time-range")
    public ResponseEntity<List<V2GScheduleDto>> getSchedulesInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(v2gService.getSchedulesInTimeRange(start, end));
    }

    @GetMapping("/schedules/station/{stationId}/time-range")
    public ResponseEntity<List<V2GScheduleDto>> getSchedulesForStationInTimeRange(
            @PathVariable String stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(v2gService.getSchedulesForStationInTimeRange(stationId, start, end));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        v2gService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/schedules/{id}/status/{status}")
    public ResponseEntity<Void> updateScheduleStatus(@PathVariable Long id, @PathVariable String status) {
        v2gService.updateScheduleStatus(id, status);
        return ResponseEntity.ok().build();
    }
} 