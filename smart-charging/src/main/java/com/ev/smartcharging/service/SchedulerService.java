package com.ev.smartcharging.service;

import com.ev.smartcharging.model.ChargingGroup;
import com.ev.smartcharging.repository.ChargingGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    
    private final SmartChargingService smartChargingService;
    private final ChargingGroupRepository chargingGroupRepository;
    
    /**
     * Reallocate power for all active charging groups every minute
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void reallocateGroupPower() {
        log.debug("Running scheduled power reallocation...");
        
        List<ChargingGroup> activeGroups = chargingGroupRepository.findByActive(true);
        
        for (ChargingGroup group : activeGroups) {
            try {
                boolean success = smartChargingService.allocateGroupPower(group.getId());
                if (!success) {
                    log.warn("Failed to reallocate power for group: {}", group.getId());
                }
            } catch (Exception e) {
                log.error("Error during scheduled power reallocation for group {}: {}", 
                        group.getId(), e.getMessage(), e);
            }
        }
    }
} 