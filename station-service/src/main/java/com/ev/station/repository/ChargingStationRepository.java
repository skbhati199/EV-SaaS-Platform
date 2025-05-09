package com.ev.station.repository;

import com.ev.station.model.ChargingStation;
import com.ev.station.model.StationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
    
    Optional<ChargingStation> findBySerialNumber(String serialNumber);
    
    List<ChargingStation> findByStatus(StationStatus status);
    
    List<ChargingStation> findByCpoId(UUID cpoId);
    
    Page<ChargingStation> findByCpoId(UUID cpoId, Pageable pageable);
    
    @Query(value = "SELECT * FROM charging_stations s WHERE " +
           "ST_DWithin(ST_MakePoint(s.location_longitude, s.location_latitude)::geography, " +
           "ST_MakePoint(?1, ?2)::geography, ?3)", nativeQuery = true)
    List<ChargingStation> findStationsWithinRadius(Double longitude, Double latitude, Double radiusInMeters);
    
    boolean existsBySerialNumber(String serialNumber);
} 