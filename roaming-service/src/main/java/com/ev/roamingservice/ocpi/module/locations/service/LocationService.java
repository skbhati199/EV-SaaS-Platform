package com.ev.roamingservice.ocpi.module.locations.service;

import com.ev.roamingservice.ocpi.module.locations.dto.Location;

import java.util.List;

/**
 * Service for handling OCPI location operations
 */
public interface LocationService {
    
    /**
     * Get a list of all locations
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param offset Pagination offset
     * @param limit Pagination limit
     * @return List of locations
     */
    List<Location> getLocations(String countryCode, String partyId, Integer offset, Integer limit);
    
    /**
     * Get a specific location
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @return Location object if found
     */
    Location getLocation(String countryCode, String partyId, String locationId);
    
    /**
     * Get a specific EVSE
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @return Location object with the specified EVSE
     */
    Location getEvse(String countryCode, String partyId, String locationId, String evseUid);
    
    /**
     * Get a specific connector
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @return Location object with the specified EVSE and connector
     */
    Location getConnector(String countryCode, String partyId, String locationId, String evseUid, String connectorId);
    
    /**
     * Create or update a location
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param location Location to create or update
     * @return Created or updated location
     */
    Location putLocation(String countryCode, String partyId, String locationId, Location location);
    
    /**
     * Create or update an EVSE
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param location Location containing the EVSE to create or update
     * @return Created or updated location with EVSE
     */
    Location putEvse(String countryCode, String partyId, String locationId, String evseUid, Location location);
    
    /**
     * Create or update a connector
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @param location Location containing the connector to create or update
     * @return Created or updated location with EVSE and connector
     */
    Location putConnector(String countryCode, String partyId, String locationId, String evseUid, String connectorId, Location location);
    
    /**
     * Delete a location
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @return True if deleted successfully
     */
    boolean deleteLocation(String countryCode, String partyId, String locationId);
    
    /**
     * Delete an EVSE
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @return True if deleted successfully
     */
    boolean deleteEvse(String countryCode, String partyId, String locationId, String evseUid);
    
    /**
     * Delete a connector
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @return True if deleted successfully
     */
    boolean deleteConnector(String countryCode, String partyId, String locationId, String evseUid, String connectorId);
} 