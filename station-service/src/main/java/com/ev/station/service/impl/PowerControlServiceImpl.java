package com.ev.station.service.impl;

import com.ev.station.dto.event.PowerDistributionEvent;
import com.ev.station.model.ChargingStation;
import com.ev.station.model.StationStatus;
import com.ev.station.ocpp.OcppMessage;
import com.ev.station.ocpp.OcppWebSocketHandler;
import com.ev.station.ocpp.request.SetChargingProfileRequest;
import com.ev.station.ocpp.response.SetChargingProfileResponse;
import com.ev.station.service.ChargingStationService;
import com.ev.station.service.PowerControlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PowerControlService to process power distribution events 
 * and send charging profiles to stations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PowerControlServiceImpl implements PowerControlService {

    private final ChargingStationService stationService;
    private final OcppWebSocketHandler ocppWebSocketHandler;
    private final ObjectMapper objectMapper;
    
    // Cache of active power control profiles to track and manage them
    // Map: stationId -> connectorId -> profileId -> expiryTime
    private final Map<String, Map<Integer, Map<Integer, LocalDateTime>>> activeProfiles = new ConcurrentHashMap<>();
    
    // Base profile ID for power control profiles
    private static final int POWER_CONTROL_PROFILE_BASE_ID = 1000000;
    
    @Override
    public boolean processPowerDistributionEvent(PowerDistributionEvent event) {
        try {
            log.info("Processing power distribution event: {} for station: {}", 
                    event.getEventId(), event.getStationId());
            
            // Get station ID
            String stationId = event.getStationId().toString();
            
            // Determine connector ID (use 0 for station-wide control if not specified)
            int connectorId = event.getConnectorId() != null ? event.getConnectorId() : 0;
            
            // Verify station exists and is online
            ChargingStation station = stationService.getStationByUUID(event.getStationId());
            
            if (station == null) {
                log.warn("Station not found for ID: {}", stationId);
                return false;
            }
            
            if (station.getStatus() != StationStatus.AVAILABLE && station.getStatus() != StationStatus.OCCUPIED) {
                log.warn("Station {} is not available (status: {})", stationId, station.getStatus());
                return false;
            }
            
            // Calculate profile ID for this event
            int profileId = calculateProfileId(event);
            
            // Set connector power limit
            CompletableFuture<Boolean> result = setConnectorPowerLimit(
                    stationId,
                    connectorId,
                    event.getPowerLimitKW(),
                    event.isTemporary() ? event.getDurationSeconds() : null,
                    profileId);
            
            // If this is a temporary limit, schedule its expiration
            if (event.isTemporary() && event.getDurationSeconds() != null) {
                LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(event.getDurationSeconds());
                
                // Store in active profiles map
                activeProfiles.computeIfAbsent(stationId, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(connectorId, k -> new ConcurrentHashMap<>())
                        .put(profileId, expiryTime);
                
                // Log profile activation
                log.info("Temporary power limit profile {} set for station {} connector {} until {}", 
                        profileId, stationId, connectorId, expiryTime);
            }
            
            return result.join(); // Wait for the result
            
        } catch (Exception e) {
            log.error("Error processing power distribution event: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public CompletableFuture<Boolean> setConnectorPowerLimit(
            String stationId, int connectorId, double powerLimitKW, Integer durationSeconds, Integer profileId) {
        
        try {
            log.info("Setting power limit of {} kW on station {} connector {}", 
                    powerLimitKW, stationId, connectorId);
            
            // Convert to watts (OCPP expects watts, not kilowatts)
            float powerLimitW = (float) (powerLimitKW * 1000);
            
            // Build the charging profile
            SetChargingProfileRequest request = buildChargingProfileRequest(
                    connectorId, powerLimitW, durationSeconds, profileId);
            
            // Send to station and return the future
            return sendOcppRequest(stationId, "SetChargingProfile", request)
                    .thenApply(response -> {
                        try {
                            // Parse the response
                            SetChargingProfileResponse profileResponse = 
                                    objectMapper.convertValue(response.getPayload(), SetChargingProfileResponse.class);
                            
                            // Check if it was accepted
                            boolean success = profileResponse.getStatus() == 
                                    SetChargingProfileResponse.ChargingProfileStatus.Accepted;
                            
                            if (success) {
                                log.info("Successfully set power limit on station {} connector {}", 
                                        stationId, connectorId);
                            } else {
                                log.warn("Failed to set power limit on station {} connector {}: {}", 
                                        stationId, connectorId, profileResponse.getStatus());
                            }
                            
                            return success;
                        } catch (Exception e) {
                            log.error("Error processing charging profile response: {}", e.getMessage(), e);
                            return false;
                        }
                    })
                    .exceptionally(e -> {
                        log.error("Error setting power limit on station {} connector {}: {}", 
                                stationId, connectorId, e.getMessage(), e);
                        return false;
                    });
        } catch (Exception e) {
            log.error("Error preparing power limit request: {}", e.getMessage(), e);
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.complete(false);
            return future;
        }
    }

    @Override
    public CompletableFuture<Boolean> clearConnectorPowerLimit(String stationId, int connectorId, int profileId) {
        log.info("Clearing power limit profile {} on station {} connector {}", 
                profileId, stationId, connectorId);
                
        try {
            // Remove from active profiles map
            Map<Integer, Map<Integer, LocalDateTime>> stationProfiles = activeProfiles.get(stationId);
            if (stationProfiles != null) {
                Map<Integer, LocalDateTime> connectorProfiles = stationProfiles.get(connectorId);
                if (connectorProfiles != null) {
                    connectorProfiles.remove(profileId);
                }
            }
            
            // To clear a profile, we send a SetChargingProfile with no schedule periods
            SetChargingProfileRequest request = buildClearChargingProfileRequest(connectorId, profileId);
            
            // Send to station and return the future
            return sendOcppRequest(stationId, "SetChargingProfile", request)
                    .thenApply(response -> {
                        try {
                            // Parse the response
                            SetChargingProfileResponse profileResponse = 
                                    objectMapper.convertValue(response.getPayload(), SetChargingProfileResponse.class);
                            
                            // Check if it was accepted
                            boolean success = profileResponse.getStatus() == 
                                    SetChargingProfileResponse.ChargingProfileStatus.Accepted;
                            
                            if (success) {
                                log.info("Successfully cleared power limit on station {} connector {}", 
                                        stationId, connectorId);
                            } else {
                                log.warn("Failed to clear power limit on station {} connector {}: {}", 
                                        stationId, connectorId, profileResponse.getStatus());
                            }
                            
                            return success;
                        } catch (Exception e) {
                            log.error("Error processing clear profile response: {}", e.getMessage(), e);
                            return false;
                        }
                    })
                    .exceptionally(e -> {
                        log.error("Error clearing power limit on station {} connector {}: {}", 
                                stationId, connectorId, e.getMessage(), e);
                        return false;
                    });
        } catch (Exception e) {
            log.error("Error preparing clear power limit request: {}", e.getMessage(), e);
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.complete(false);
            return future;
        }
    }

    @Override
    public int calculateProfileId(PowerDistributionEvent event) {
        // Create a unique profile ID based on the event type and priority
        // This ensures that higher priority events override lower priority ones
        int priorityComponent = event.getPriority() * 1000;
        int reasonComponent = event.getReason().ordinal() * 100;
        int uniqueComponent;
        
        if (event.getConnectorId() != null) {
            // For connector-specific profiles
            uniqueComponent = event.getConnectorId();
        } else {
            // For station-wide profiles
            uniqueComponent = 0;
        }
        
        return POWER_CONTROL_PROFILE_BASE_ID + priorityComponent + reasonComponent + uniqueComponent;
    }
    
    /**
     * Build a charging profile request to set a power limit
     */
    private SetChargingProfileRequest buildChargingProfileRequest(
            int connectorId, float powerLimitW, Integer durationSeconds, Integer profileId) {
        
        // Create a schedule period starting now with the specified limit
        SetChargingProfileRequest.ChargingSchedulePeriod period = 
                SetChargingProfileRequest.ChargingSchedulePeriod.builder()
                        .startPeriod(0)
                        .limit(powerLimitW)
                        .build();
        
        // Build the charging schedule
        SetChargingProfileRequest.ChargingSchedule schedule = 
                SetChargingProfileRequest.ChargingSchedule.builder()
                        .duration(durationSeconds)
                        .chargingRateUnit(SetChargingProfileRequest.ChargingRateUnitType.W)
                        .chargingSchedulePeriod(new SetChargingProfileRequest.ChargingSchedulePeriod[]{period})
                        .build();
        
        // Build the charging profile
        SetChargingProfileRequest.ChargingProfile profile = 
                SetChargingProfileRequest.ChargingProfile.builder()
                        .chargingProfileId(profileId != null ? profileId : POWER_CONTROL_PROFILE_BASE_ID)
                        .stackLevel(0)
                        .chargingProfilePurpose(SetChargingProfileRequest.ChargingProfilePurposeType.TxDefaultProfile)
                        .chargingProfileKind(SetChargingProfileRequest.ChargingProfileKindType.Absolute)
                        .chargingSchedule(schedule)
                        .validFrom(DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()))
                        .build();
        
        // Build the request
        return SetChargingProfileRequest.builder()
                .connectorId(connectorId)
                .csChargingProfiles(profile)
                .build();
    }
    
    /**
     * Build a charging profile request to clear a power limit
     */
    private SetChargingProfileRequest buildClearChargingProfileRequest(int connectorId, int profileId) {
        // Create an empty schedule
        SetChargingProfileRequest.ChargingSchedule schedule = 
                SetChargingProfileRequest.ChargingSchedule.builder()
                        .chargingRateUnit(SetChargingProfileRequest.ChargingRateUnitType.W)
                        .chargingSchedulePeriod(new SetChargingProfileRequest.ChargingSchedulePeriod[]{})
                        .build();
        
        // Build the charging profile to clear
        SetChargingProfileRequest.ChargingProfile profile = 
                SetChargingProfileRequest.ChargingProfile.builder()
                        .chargingProfileId(profileId)
                        .stackLevel(0)
                        .chargingProfilePurpose(SetChargingProfileRequest.ChargingProfilePurposeType.TxDefaultProfile)
                        .chargingProfileKind(SetChargingProfileRequest.ChargingProfileKindType.Absolute)
                        .chargingSchedule(schedule)
                        .build();
        
        // Build the request
        return SetChargingProfileRequest.builder()
                .connectorId(connectorId)
                .csChargingProfiles(profile)
                .build();
    }
    
    /**
     * Send an OCPP request to a charging station
     */
    private CompletableFuture<OcppMessage> sendOcppRequest(String stationId, String action, Object payload) {
        return ocppWebSocketHandler.sendRequest(stationId, action, payload);
    }
} 