package com.ev.roamingservice.ocpi.module.locations.controller;

import com.ev.roamingservice.dto.OcpiResponse;
import com.ev.roamingservice.ocpi.module.locations.dto.Location;
import com.ev.roamingservice.ocpi.module.locations.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling OCPI Locations module endpoints
 */
@RestController
@RequestMapping("${ocpi.base-path}/2.2/locations")
public class LocationsController {

    private final LocationService locationService;

    @Autowired
    public LocationsController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * Get a list of all locations (eMSP interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param offset Pagination offset
     * @param limit Pagination limit
     * @return List of locations
     */
    @GetMapping("/{countryCode}/{partyId}")
    public ResponseEntity<OcpiResponse<List<Location>>> getLocations(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        
        List<Location> locations = locationService.getLocations(countryCode, partyId, offset, limit);
        return ResponseEntity.ok(OcpiResponse.success(locations));
    }

    /**
     * Get a specific location (eMSP interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @return Location if found
     */
    @GetMapping("/{countryCode}/{partyId}/{locationId}")
    public ResponseEntity<OcpiResponse<Location>> getLocation(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId) {
        
        Location location = locationService.getLocation(countryCode, partyId, locationId);
        if (location == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcpiResponse.notFound("Location not found"));
        }
        
        return ResponseEntity.ok(OcpiResponse.success(location));
    }

    /**
     * Get a specific EVSE (eMSP interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @return Location with the specific EVSE
     */
    @GetMapping("/{countryCode}/{partyId}/{locationId}/{evseUid}")
    public ResponseEntity<OcpiResponse<Location>> getEvse(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId,
            @PathVariable String evseUid) {
        
        Location location = locationService.getEvse(countryCode, partyId, locationId, evseUid);
        if (location == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcpiResponse.notFound("EVSE not found"));
        }
        
        return ResponseEntity.ok(OcpiResponse.success(location));
    }

    /**
     * Get a specific connector (eMSP interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @return Location with the specific EVSE and connector
     */
    @GetMapping("/{countryCode}/{partyId}/{locationId}/{evseUid}/{connectorId}")
    public ResponseEntity<OcpiResponse<Location>> getConnector(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId,
            @PathVariable String evseUid,
            @PathVariable String connectorId) {
        
        Location location = locationService.getConnector(countryCode, partyId, locationId, evseUid, connectorId);
        if (location == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcpiResponse.notFound("Connector not found"));
        }
        
        return ResponseEntity.ok(OcpiResponse.success(location));
    }

    /**
     * Create or update a location (CPO interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param location Location to create or update
     * @return Created or updated location
     */
    @PutMapping("/{countryCode}/{partyId}/{locationId}")
    public ResponseEntity<OcpiResponse<Location>> putLocation(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId,
            @RequestBody Location location) {
        
        Location updatedLocation = locationService.putLocation(countryCode, partyId, locationId, location);
        return ResponseEntity.ok(OcpiResponse.success(updatedLocation));
    }

    /**
     * Create or update an EVSE (CPO interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param location Location containing the EVSE to create or update
     * @return Created or updated location with EVSE
     */
    @PutMapping("/{countryCode}/{partyId}/{locationId}/{evseUid}")
    public ResponseEntity<OcpiResponse<Location>> putEvse(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId,
            @PathVariable String evseUid,
            @RequestBody Location location) {
        
        Location updatedLocation = locationService.putEvse(countryCode, partyId, locationId, evseUid, location);
        return ResponseEntity.ok(OcpiResponse.success(updatedLocation));
    }

    /**
     * Create or update a connector (CPO interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @param location Location containing the connector to create or update
     * @return Created or updated location with EVSE and connector
     */
    @PutMapping("/{countryCode}/{partyId}/{locationId}/{evseUid}/{connectorId}")
    public ResponseEntity<OcpiResponse<Location>> putConnector(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId,
            @PathVariable String evseUid,
            @PathVariable String connectorId,
            @RequestBody Location location) {
        
        Location updatedLocation = locationService.putConnector(countryCode, partyId, locationId, evseUid, connectorId, location);
        return ResponseEntity.ok(OcpiResponse.success(updatedLocation));
    }

    /**
     * Delete a location (CPO interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @return No content if deleted successfully
     */
    @DeleteMapping("/{countryCode}/{partyId}/{locationId}")
    public ResponseEntity<OcpiResponse<String>> deleteLocation(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId) {
        
        boolean deleted = locationService.deleteLocation(countryCode, partyId, locationId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcpiResponse.notFound("Location not found"));
        }
        
        return ResponseEntity.ok(OcpiResponse.success("Location deleted"));
    }

    /**
     * Delete an EVSE (CPO interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @return No content if deleted successfully
     */
    @DeleteMapping("/{countryCode}/{partyId}/{locationId}/{evseUid}")
    public ResponseEntity<OcpiResponse<String>> deleteEvse(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId,
            @PathVariable String evseUid) {
        
        boolean deleted = locationService.deleteEvse(countryCode, partyId, locationId, evseUid);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcpiResponse.notFound("EVSE not found"));
        }
        
        return ResponseEntity.ok(OcpiResponse.success("EVSE deleted"));
    }

    /**
     * Delete a connector (CPO interface)
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @return No content if deleted successfully
     */
    @DeleteMapping("/{countryCode}/{partyId}/{locationId}/{evseUid}/{connectorId}")
    public ResponseEntity<OcpiResponse<String>> deleteConnector(
            @PathVariable String countryCode,
            @PathVariable String partyId,
            @PathVariable String locationId,
            @PathVariable String evseUid,
            @PathVariable String connectorId) {
        
        boolean deleted = locationService.deleteConnector(countryCode, partyId, locationId, evseUid, connectorId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(OcpiResponse.notFound("Connector not found"));
        }
        
        return ResponseEntity.ok(OcpiResponse.success("Connector deleted"));
    }
} 