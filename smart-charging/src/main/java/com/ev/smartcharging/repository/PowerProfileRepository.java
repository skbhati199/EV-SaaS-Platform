package com.ev.smartcharging.repository;

import com.ev.smartcharging.model.PowerProfile;
import com.ev.smartcharging.model.PriceTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PowerProfileRepository extends JpaRepository<PowerProfile, UUID> {
    List<PowerProfile> findByStationId(UUID stationId);
    List<PowerProfile> findByGroupId(UUID groupId);
    List<PowerProfile> findByPriceTier(PriceTier priceTier);
    
    @Query("SELECT p FROM PowerProfile p WHERE " +
           "(p.stationId = :stationId OR p.groupId = :groupId) AND " +
           "p.dayOfWeek LIKE %:dayOfWeek% AND " +
           ":currentTime BETWEEN p.startTime AND p.endTime")
    List<PowerProfile> findActiveProfiles(UUID stationId, UUID groupId, String dayOfWeek, LocalTime currentTime);
} 