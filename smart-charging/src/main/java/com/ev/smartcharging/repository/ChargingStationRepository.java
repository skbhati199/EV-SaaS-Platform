package com.ev.smartcharging.repository;

import com.ev.smartcharging.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
    List<ChargingStation> findByChargingGroupId(UUID groupId);
    List<ChargingStation> findByEnabled(Boolean enabled);
    List<ChargingStation> findBySmartChargingEnabled(Boolean smartChargingEnabled);
} 