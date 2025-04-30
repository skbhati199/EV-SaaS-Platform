package com.ev.station.service;

import com.ev.station.dto.EVSERegistrationRequest;
import com.ev.station.dto.EVSEResponse;
import com.ev.station.model.EVSE;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EVSEService {
    
    /**
     * Register a new EVSE in the system
     * @param request EVSE registration details
     * @param ownerId ID of the CPO registering the EVSE
     * @return Registered EVSE information
     */
    EVSEResponse registerEVSE(EVSERegistrationRequest request, UUID ownerId);
    
    /**
     * Get an EVSE by its ID
     * @param id UUID of the EVSE
     * @return EVSE information if found
     */
    Optional<EVSEResponse> getEVSEById(UUID id);
    
    /**
     * Get an EVSE by its unique identifier
     * @param evseId Unique identifier of the EVSE
     * @return EVSE information if found
     */
    Optional<EVSEResponse> getEVSEByEvseId(String evseId);
    
    /**
     * Get all EVSEs owned by a specific operator
     * @param ownerId UUID of the owner (CPO)
     * @return List of EVSEs owned by the specified operator
     */
    List<EVSEResponse> getEVSEsByOwnerId(UUID ownerId);
    
    /**
     * Update EVSE heartbeat timestamp
     * @param evseId Unique identifier of the EVSE
     * @return Updated EVSE information
     */
    Optional<EVSEResponse> updateHeartbeat(String evseId);
}
