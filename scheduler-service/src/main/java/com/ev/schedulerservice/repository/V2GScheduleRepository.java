package com.ev.schedulerservice.repository;

import com.ev.schedulerservice.model.V2GSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface V2GScheduleRepository extends JpaRepository<V2GSchedule, Long> {
    
    List<V2GSchedule> findByVehicleId(String vehicleId);
    
    List<V2GSchedule> findByStationId(String stationId);
    
    List<V2GSchedule> findByUserId(String userId);
    
    List<V2GSchedule> findByStatus(String status);
    
    @Query("SELECT v FROM V2GSchedule v WHERE v.startTime >= ?1 AND v.endTime <= ?2")
    List<V2GSchedule> findSchedulesInTimeRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT v FROM V2GSchedule v WHERE v.stationId = ?1 AND v.startTime >= ?2 AND v.endTime <= ?3")
    List<V2GSchedule> findSchedulesForStationInTimeRange(String stationId, LocalDateTime start, LocalDateTime end);
} 