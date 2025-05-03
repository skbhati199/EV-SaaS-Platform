package com.ev.smartcharging.repository;

import com.ev.smartcharging.model.ChargingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChargingGroupRepository extends JpaRepository<ChargingGroup, UUID> {
    List<ChargingGroup> findByActive(Boolean active);
    List<ChargingGroup> findByLoadBalancingStrategy(String strategy);
} 