package com.ev.smartcharging.service.impl;

import com.ev.smartcharging.dto.ChargingStationDto;
import com.ev.smartcharging.dto.event.PowerDistributionEvent;
import com.ev.smartcharging.model.*;
import com.ev.smartcharging.repository.ChargingGroupRepository;
import com.ev.smartcharging.repository.ChargingSessionRepository;
import com.ev.smartcharging.repository.ChargingStationRepository;
import com.ev.smartcharging.repository.PowerProfileRepository;
import com.ev.smartcharging.service.KafkaProducerService;
import com.ev.smartcharging.service.SmartChargingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartChargingServiceImpl implements SmartChargingService {

    private final ChargingGroupRepository chargingGroupRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final PowerProfileRepository powerProfileRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public boolean allocateGroupPower(UUID groupId) {
        try {
            ChargingGroup group = chargingGroupRepository.findById(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("Charging group not found with id: " + groupId));

            if (!group.getActive()) {
                log.warn("Cannot allocate power to inactive group {}", groupId);
                return false;
            }

            List<ChargingStation> stations = chargingStationRepository.findByChargingGroupId(groupId);
            List<UUID> stationIds = stations.stream().map(ChargingStation::getId).collect(Collectors.toList());

            // Get all active sessions for stations in this group
            List<ChargingSession> activeSessions = new ArrayList<>();
            for (UUID stationId : stationIds) {
                activeSessions.addAll(chargingSessionRepository.findActiveSessionsByStationId(stationId));
            }

            if (activeSessions.isEmpty()) {
                log.info("No active sessions found for group {}", groupId);
                return true;
            }

            double totalPowerAllocated = 0.0;
            LoadBalancingStrategy strategy = group.getLoadBalancingStrategy();

            // Apply the appropriate load balancing strategy
            switch (strategy) {
                case ROUND_ROBIN:
                    totalPowerAllocated = allocateRoundRobin(group, activeSessions);
                    break;
                case FIRST_COME_FIRST_SERVE:
                    totalPowerAllocated = allocateFirstComeFirstServe(group, activeSessions);
                    break;
                case PRIORITY_BASED:
                    totalPowerAllocated = allocatePriorityBased(group, activeSessions);
                    break;
                case DYNAMIC:
                    totalPowerAllocated = allocateDynamic(group, activeSessions);
                    break;
                case TIME_OF_USE:
                    totalPowerAllocated = allocateTimeOfUse(group, activeSessions);
                    break;
                default:
                    log.warn("Unsupported load balancing strategy: {}", strategy);
                    return false;
            }

            // Update the group's current power
            group.setCurrentPowerKW(totalPowerAllocated);
            chargingGroupRepository.save(group);

            return true;
        } catch (Exception e) {
            log.error("Error allocating power to group {}: {}", groupId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean adjustSessionPower(UUID sessionId, Double powerKW) {
        try {
            ChargingSession session = chargingSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException("Charging session not found with id: " + sessionId));

            if (powerKW < 0) {
                log.warn("Cannot set negative power allocation for session {}", sessionId);
                return false;
            }

            // Get the station to check its maximum power
            ChargingStation station = chargingStationRepository.findById(session.getStationId())
                    .orElseThrow(() -> new EntityNotFoundException("Charging station not found with id: " + session.getStationId()));

            if (powerKW > station.getMaxPowerKW()) {
                log.warn("Requested power {} exceeds station maximum power {} for session {}", 
                        powerKW, station.getMaxPowerKW(), sessionId);
                powerKW = station.getMaxPowerKW();
            }

            // Set the new power allocation
            session.setAllocatedPowerKW(powerKW);
            
            // Update session status if needed
            if (powerKW == 0 && session.getSessionStatus() == SessionStatus.ACTIVE) {
                session.setSessionStatus(SessionStatus.PAUSED);
            } else if (powerKW > 0 && session.getSessionStatus() == SessionStatus.PAUSED) {
                session.setSessionStatus(SessionStatus.ACTIVE);
            } else if (powerKW < session.getMaxPowerKW() && powerKW > 0) {
                session.setSessionStatus(SessionStatus.POWER_REDUCED);
            }

            chargingSessionRepository.save(session);
            
            // Update the station's current power
            updateStationPower(station.getId());
            
            // If station is part of a group, update group power
            if (station.getChargingGroup() != null) {
                updateGroupPower(station.getChargingGroup().getId());
            }
            
            // Send power distribution event to the station via Kafka
            sendPowerAdjustmentEvent(station.getId(), session.getConnectorId(), powerKW, session.getId());
            
            return true;
        } catch (Exception e) {
            log.error("Error adjusting power for session {}: {}", sessionId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateSessionStatus(UUID sessionId, SessionStatus status) {
        try {
            ChargingSession session = chargingSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException("Charging session not found with id: " + sessionId));

            // Handle different status transitions
            if (status == SessionStatus.COMPLETED || status == SessionStatus.TERMINATED || status == SessionStatus.ERROR) {
                session.setEndTime(LocalDateTime.now());
            }

            // Set the new status
            session.setSessionStatus(status);
            
            // If pausing or stopping, set power to 0
            if (status == SessionStatus.PAUSED || status == SessionStatus.COMPLETED || 
                status == SessionStatus.TERMINATED || status == SessionStatus.ERROR) {
                session.setAllocatedPowerKW(0.0);
            }
            
            chargingSessionRepository.save(session);
            
            // Update station and group power if needed
            updateStationPower(session.getStationId());
            
            ChargingStation station = chargingStationRepository.findById(session.getStationId()).orElse(null);
            if (station != null && station.getChargingGroup() != null) {
                updateGroupPower(station.getChargingGroup().getId());
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error updating status for session {}: {}", sessionId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<ChargingStationDto> calculateOptimalPowerAllocation(UUID groupId) {
        ChargingGroup group = chargingGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Charging group not found with id: " + groupId));

        List<ChargingStation> stations = chargingStationRepository.findByChargingGroupId(groupId);
        List<ChargingStationDto> stationDtos = new ArrayList<>();

        // Calculate optimal power allocation based on group strategy
        double maxGroupPower = group.getMaxPowerKW();
        double remainingPower = maxGroupPower;
        
        // Sort stations based on priority
        stations.sort(Comparator.comparing(ChargingStation::getPriorityLevel, Comparator.nullsLast(Comparator.naturalOrder())));
        
        for (ChargingStation station : stations) {
            ChargingStationDto stationDto = convertToDto(station);
            
            // Get active sessions for this station
            List<ChargingSession> activeSessions = chargingSessionRepository.findActiveSessionsByStationId(station.getId());
            stationDto.setActiveSessionCount(activeSessions.size());
            
            if (activeSessions.isEmpty()) {
                stationDto.setCurrentPowerKW(0.0);
            } else {
                // Calculate optimal power for this station
                double optimalPower = Math.min(station.getMaxPowerKW(), remainingPower / (stations.size() - stationDtos.size()));
                stationDto.setCurrentPowerKW(optimalPower);
                remainingPower -= optimalPower;
            }
            
            stationDtos.add(stationDto);
        }
        
        return stationDtos;
    }

    @Override
    @Transactional
    public boolean handleSessionStarted(UUID sessionId, UUID stationId, Integer connectorId, UUID userId) {
        try {
            ChargingStation station = chargingStationRepository.findById(stationId)
                    .orElseThrow(() -> new EntityNotFoundException("Charging station not found with id: " + stationId));

            if (!station.getEnabled()) {
                log.warn("Cannot start session on disabled station {}", stationId);
                return false;
            }

            // Create new charging session
            ChargingSession session = ChargingSession.builder()
                    .id(sessionId)
                    .stationId(stationId)
                    .connectorId(connectorId)
                    .userId(userId)
                    .startTime(LocalDateTime.now())
                    .allocatedPowerKW(0.0) // Start with no power
                    .maxPowerKW(station.getMaxPowerKW())
                    .energyDeliveredKWh(0.0)
                    .priorityLevel(station.getPriorityLevel())
                    .sessionStatus(SessionStatus.PENDING)
                    .build();
            
            chargingSessionRepository.save(session);
            
            // If smart charging is enabled, allocate power
            if (station.getSmartChargingEnabled()) {
                // Determine initial power allocation
                double initialPower = determineInitialPowerAllocation(session);
                
                // Set session to active with allocated power
                session.setSessionStatus(SessionStatus.ACTIVE);
                session.setAllocatedPowerKW(initialPower);
                chargingSessionRepository.save(session);
                
                // Update station power
                updateStationPower(stationId);
                
                // Update group power if station is part of a group
                if (station.getChargingGroup() != null) {
                    allocateGroupPower(station.getChargingGroup().getId());
                }
            } else {
                // If smart charging is disabled, allocate full power
                session.setSessionStatus(SessionStatus.ACTIVE);
                session.setAllocatedPowerKW(station.getMaxPowerKW());
                chargingSessionRepository.save(session);
                
                // Update station power
                updateStationPower(stationId);
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error handling session start for session {}: {}", sessionId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean handleSessionEnded(UUID sessionId) {
        try {
            ChargingSession session = chargingSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException("Charging session not found with id: " + sessionId));

            // Only process if session is not already ended
            if (session.getSessionStatus() == SessionStatus.COMPLETED || 
                session.getSessionStatus() == SessionStatus.TERMINATED ||
                session.getSessionStatus() == SessionStatus.ERROR) {
                log.warn("Session {} is already ended with status {}", sessionId, session.getSessionStatus());
                return true;
            }
            
            // Mark session as completed
            session.setSessionStatus(SessionStatus.COMPLETED);
            session.setEndTime(LocalDateTime.now());
            session.setAllocatedPowerKW(0.0);
            chargingSessionRepository.save(session);
            
            // Update station power
            updateStationPower(session.getStationId());
            
            // Get the station
            ChargingStation station = chargingStationRepository.findById(session.getStationId()).orElse(null);
            
            // Reallocate power if station is part of a group
            if (station != null && station.getChargingGroup() != null && station.getSmartChargingEnabled()) {
                allocateGroupPower(station.getChargingGroup().getId());
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error handling session end for session {}: {}", sessionId, e.getMessage(), e);
            return false;
        }
    }

    // Helper methods for power allocation strategies
    
    private double allocateRoundRobin(ChargingGroup group, List<ChargingSession> sessions) {
        double totalPower = group.getMaxPowerKW();
        double powerPerSession = totalPower / sessions.size();
        double allocatedPower = 0.0;
        
        for (ChargingSession session : sessions) {
            // Limit power to the session's max power
            double sessionPower = Math.min(powerPerSession, session.getMaxPowerKW());
            
            // Update session
            session.setAllocatedPowerKW(sessionPower);
            session.setSessionStatus(SessionStatus.ACTIVE);
            chargingSessionRepository.save(session);
            
            allocatedPower += sessionPower;
        }
        
        return allocatedPower;
    }
    
    private double allocateFirstComeFirstServe(ChargingGroup group, List<ChargingSession> sessions) {
        double totalPower = group.getMaxPowerKW();
        double allocatedPower = 0.0;
        
        // Sort sessions by start time (earliest first)
        sessions.sort(Comparator.comparing(ChargingSession::getStartTime));
        
        for (ChargingSession session : sessions) {
            if (allocatedPower >= totalPower) {
                // No more power to allocate
                session.setAllocatedPowerKW(0.0);
                session.setSessionStatus(SessionStatus.PAUSED);
            } else {
                // Allocate up to the session's maximum or remaining power
                double remainingPower = totalPower - allocatedPower;
                double sessionPower = Math.min(remainingPower, session.getMaxPowerKW());
                
                session.setAllocatedPowerKW(sessionPower);
                session.setSessionStatus(sessionPower > 0 ? SessionStatus.ACTIVE : SessionStatus.PAUSED);
                
                allocatedPower += sessionPower;
            }
            
            chargingSessionRepository.save(session);
        }
        
        return allocatedPower;
    }
    
    private double allocatePriorityBased(ChargingGroup group, List<ChargingSession> sessions) {
        double totalPower = group.getMaxPowerKW();
        double allocatedPower = 0.0;
        
        // Sort sessions by priority (lowest number = highest priority)
        sessions.sort(Comparator.comparing(ChargingSession::getPriorityLevel, 
                Comparator.nullsLast(Comparator.naturalOrder())));
        
        for (ChargingSession session : sessions) {
            if (allocatedPower >= totalPower) {
                // No more power to allocate
                session.setAllocatedPowerKW(0.0);
                session.setSessionStatus(SessionStatus.PAUSED);
            } else {
                // Allocate up to the session's maximum or remaining power
                double remainingPower = totalPower - allocatedPower;
                double sessionPower = Math.min(remainingPower, session.getMaxPowerKW());
                
                session.setAllocatedPowerKW(sessionPower);
                session.setSessionStatus(sessionPower > 0 ? SessionStatus.ACTIVE : SessionStatus.PAUSED);
                
                allocatedPower += sessionPower;
            }
            
            chargingSessionRepository.save(session);
        }
        
        return allocatedPower;
    }
    
    private double allocateDynamic(ChargingGroup group, List<ChargingSession> sessions) {
        // This is a placeholder for a more complex dynamic allocation strategy
        // In a real implementation, this would take into account grid conditions, 
        // energy prices, and other real-time factors
        
        // For now, default to priority-based allocation
        return allocatePriorityBased(group, sessions);
    }
    
    private double allocateTimeOfUse(ChargingGroup group, List<ChargingSession> sessions) {
        double totalPower = group.getMaxPowerKW();
        double allocatedPower = 0.0;
        
        // Get current time
        LocalTime currentTime = LocalTime.now();
        DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
        String dayOfWeekValue = String.valueOf(currentDay.getValue());
        
        // Find active power profiles for this group
        List<PowerProfile> activeProfiles = powerProfileRepository.findActiveProfiles(
                null, group.getId(), dayOfWeekValue, currentTime);
        
        // If no active profiles, fall back to priority-based
        if (activeProfiles.isEmpty()) {
            return allocatePriorityBased(group, sessions);
        }
        
        // Use the first applicable power profile
        PowerProfile activeProfile = activeProfiles.get(0);
        
        // Modify total power based on profile
        if (activeProfile.getMaxPowerKW() != null) {
            totalPower = Math.min(totalPower, activeProfile.getMaxPowerKW());
        }
        
        // Sort sessions by priority
        sessions.sort(Comparator.comparing(ChargingSession::getPriorityLevel, 
                Comparator.nullsLast(Comparator.naturalOrder())));
        
        for (ChargingSession session : sessions) {
            if (allocatedPower >= totalPower) {
                // No more power to allocate
                session.setAllocatedPowerKW(0.0);
                session.setSessionStatus(SessionStatus.PAUSED);
            } else {
                // Allocate up to the session's maximum or remaining power
                double remainingPower = totalPower - allocatedPower;
                double sessionPower = Math.min(remainingPower, session.getMaxPowerKW());
                
                // Ensure minimum power if specified in profile
                if (activeProfile.getMinPowerKW() != null && sessionPower > 0) {
                    sessionPower = Math.max(sessionPower, activeProfile.getMinPowerKW());
                }
                
                session.setAllocatedPowerKW(sessionPower);
                session.setSessionStatus(sessionPower > 0 ? SessionStatus.ACTIVE : SessionStatus.PAUSED);
                
                allocatedPower += sessionPower;
            }
            
            chargingSessionRepository.save(session);
        }
        
        return allocatedPower;
    }
    
    // Additional helper methods
    
    private double determineInitialPowerAllocation(ChargingSession session) {
        ChargingStation station = chargingStationRepository.findById(session.getStationId()).orElse(null);
        
        if (station == null) {
            return 0.0;
        }
        
        // If station is part of a group, allocate power based on group strategy
        if (station.getChargingGroup() != null) {
            // For now, simply return 0 and let the group allocation handle it
            return 0.0;
        } else {
            // If not part of a group, allocate full station power
            return station.getMaxPowerKW();
        }
    }
    
    private void updateStationPower(UUID stationId) {
        // Get active sessions for this station
        List<ChargingSession> activeSessions = chargingSessionRepository.findActiveSessionsByStationId(stationId);
        
        // Calculate total power
        double totalPower = activeSessions.stream()
                .mapToDouble(ChargingSession::getAllocatedPowerKW)
                .sum();
        
        // Update station
        ChargingStation station = chargingStationRepository.findById(stationId).orElse(null);
        if (station != null) {
            station.setCurrentPowerKW(totalPower);
            chargingStationRepository.save(station);
        }
    }
    
    private void updateGroupPower(UUID groupId) {
        // Get all stations in the group
        List<ChargingStation> stations = chargingStationRepository.findByChargingGroupId(groupId);
        
        // Calculate total power
        double totalPower = stations.stream()
                .mapToDouble(station -> station.getCurrentPowerKW() != null ? station.getCurrentPowerKW() : 0.0)
                .sum();
        
        // Update group
        ChargingGroup group = chargingGroupRepository.findById(groupId).orElse(null);
        if (group != null) {
            group.setCurrentPowerKW(totalPower);
            chargingGroupRepository.save(group);
        }
    }
    
    // Utility method to convert station to DTO
    private ChargingStationDto convertToDto(ChargingStation station) {
        String groupName = station.getChargingGroup() != null ? station.getChargingGroup().getName() : null;
        UUID groupId = station.getChargingGroup() != null ? station.getChargingGroup().getId() : null;
        
        return ChargingStationDto.builder()
                .id(station.getId())
                .chargingGroupId(groupId)
                .chargingGroupName(groupName)
                .maxPowerKW(station.getMaxPowerKW())
                .currentPowerKW(station.getCurrentPowerKW())
                .priorityLevel(station.getPriorityLevel())
                .enabled(station.getEnabled())
                .smartChargingEnabled(station.getSmartChargingEnabled())
                .build();
    }

    /**
     * Send a power adjustment event to a charging station.
     * 
     * @param stationId The ID of the charging station
     * @param connectorId The ID of the connector
     * @param powerKW The power limit to set
     * @param sessionId The session ID related to this power adjustment
     * @return The event ID of the sent event
     */
    private UUID sendPowerAdjustmentEvent(UUID stationId, Integer connectorId, Double powerKW, UUID sessionId) {
        log.info("Sending power adjustment event to station {} connector {}: {} kW", 
                stationId, connectorId, powerKW);
                
        PowerDistributionEvent.PowerAdjustmentReason reason = determineAdjustmentReason(stationId, sessionId);
        
        return kafkaProducerService.sendPowerAdjustmentCommand(
                stationId,
                connectorId,
                powerKW,
                reason,
                false, // Not temporary unless it's an emergency
                null,  // No duration for persistent changes
                sessionId
        );
    }
    
    /**
     * Determine the reason for power adjustment based on system state.
     */
    private PowerDistributionEvent.PowerAdjustmentReason determineAdjustmentReason(UUID stationId, UUID sessionId) {
        try {
            ChargingStation station = chargingStationRepository.findById(stationId).orElse(null);
            
            // If station is part of a group with near max capacity, it's load balancing
            if (station != null && station.getChargingGroup() != null) {
                ChargingGroup group = station.getChargingGroup();
                if (group.getCurrentPowerKW() >= group.getMaxPowerKW() * 0.9) {
                    return PowerDistributionEvent.PowerAdjustmentReason.LOAD_BALANCING;
                }
            }
            
            // Check if we have time-of-use pricing active
            LocalDateTime now = LocalDateTime.now();
            String dayOfWeek = now.getDayOfWeek().toString();
            List<PowerProfile> activeProfiles = powerProfileRepository.findActiveProfiles(
                    stationId, 
                    station != null && station.getChargingGroup() != null ? station.getChargingGroup().getId() : null, 
                    dayOfWeek, 
                    now.toLocalTime());
                    
            if (!activeProfiles.isEmpty()) {
                return PowerDistributionEvent.PowerAdjustmentReason.SCHEDULED_PROFILE;
            }
            
            // Default to optimization
            return PowerDistributionEvent.PowerAdjustmentReason.OPTIMIZATION;
        } catch (Exception e) {
            log.error("Error determining adjustment reason", e);
            return PowerDistributionEvent.PowerAdjustmentReason.OPTIMIZATION;
        }
    }
} 