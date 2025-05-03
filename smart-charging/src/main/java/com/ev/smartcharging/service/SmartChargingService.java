package com.ev.smartcharging.service;

import com.ev.smartcharging.dto.ChargingStationDto;
import com.ev.smartcharging.model.SessionStatus;

import java.util.List;
import java.util.UUID;

public interface SmartChargingService {
    /**
     * Distributes available power among active charging sessions in a group
     * @param groupId The ID of the charging group
     * @return True if power allocation was successful
     */
    boolean allocateGroupPower(UUID groupId);
    
    /**
     * Adjusts the power of a specific charging session
     * @param sessionId The ID of the charging session
     * @param powerKW The power to allocate in kW
     * @return True if adjustment was successful
     */
    boolean adjustSessionPower(UUID sessionId, Double powerKW);
    
    /**
     * Update the status of a charging session
     * @param sessionId The ID of the charging session
     * @param status The new status
     * @return True if the update was successful
     */
    boolean updateSessionStatus(UUID sessionId, SessionStatus status);
    
    /**
     * Calculates the optimal power allocation for a charging group
     * @param groupId The ID of the charging group
     * @return A list of charging stations with updated power allocations
     */
    List<ChargingStationDto> calculateOptimalPowerAllocation(UUID groupId);
    
    /**
     * Handles a new charging session started notification
     * @param sessionId The ID of the new charging session
     * @param stationId The ID of the charging station
     * @param connectorId The ID of the connector
     * @param userId The ID of the user
     * @return True if the session was successfully processed
     */
    boolean handleSessionStarted(UUID sessionId, UUID stationId, Integer connectorId, UUID userId);
    
    /**
     * Handles a charging session ended notification
     * @param sessionId The ID of the ended charging session
     * @return True if the session end was successfully processed
     */
    boolean handleSessionEnded(UUID sessionId);
} 