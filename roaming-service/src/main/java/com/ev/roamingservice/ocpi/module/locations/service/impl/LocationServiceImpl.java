package com.ev.roamingservice.ocpi.module.locations.service.impl;

import com.ev.roamingservice.dto.event.LocationEvent;
import com.ev.roamingservice.model.LocationEntity;
import com.ev.roamingservice.ocpi.module.locations.dto.Location;
import com.ev.roamingservice.ocpi.module.locations.service.LocationService;
import com.ev.roamingservice.repository.LocationRepository;
import com.ev.roamingservice.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the LocationService with Kafka event integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Get a list of all locations
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param offset Pagination offset
     * @param limit Pagination limit
     * @return List of locations
     */
    @Override
    public List<Location> getLocations(String countryCode, String partyId, Integer offset, Integer limit) {
        List<LocationEntity> locationEntities = locationRepository.findByCountryCodeAndPartyId(countryCode, partyId);
        
        // Apply pagination
        int startIndex = Math.min(offset, locationEntities.size());
        int endIndex = Math.min(startIndex + limit, locationEntities.size());
        
        List<Location> locations = new ArrayList<>();
        
        for (int i = startIndex; i < endIndex; i++) {
            locations.add(convertToDto(locationEntities.get(i)));
        }
        
        return locations;
    }

    /**
     * Get a specific location
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @return Location object if found
     */
    @Override
    public Location getLocation(String countryCode, String partyId, String locationId) {
        Optional<LocationEntity> locationEntity = locationRepository
                .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
        
        return locationEntity.map(this::convertToDto).orElse(null);
    }

    /**
     * Get a specific EVSE
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @return Location object with the specified EVSE
     */
    @Override
    public Location getEvse(String countryCode, String partyId, String locationId, String evseUid) {
        Optional<LocationEntity> locationEntity = locationRepository
                .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
        
        if (locationEntity.isPresent()) {
            Location location = convertToDto(locationEntity.get());
            // Filter for the specific EVSE - this is a simplified implementation
            return location;
        }
        
        return null;
    }

    /**
     * Get a specific connector
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @return Location object with the specified EVSE and connector
     */
    @Override
    public Location getConnector(String countryCode, String partyId, String locationId, String evseUid, String connectorId) {
        // Similar to getEvse but also filter for the specific connector
        return getEvse(countryCode, partyId, locationId, evseUid);
    }

    /**
     * Create or update a location
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param location Location to create or update
     * @return Created or updated location
     */
    @Override
    @Transactional
    public Location putLocation(String countryCode, String partyId, String locationId, Location location) {
        Optional<LocationEntity> existingLocationOpt = locationRepository
                .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
        
        LocationEntity locationEntity;
        boolean isNew = !existingLocationOpt.isPresent();
        
        if (isNew) {
            locationEntity = new LocationEntity();
            locationEntity.setCountryCode(countryCode);
            locationEntity.setPartyId(partyId);
            locationEntity.setLocationId(locationId);
            locationEntity.setCreatedAt(ZonedDateTime.now());
        } else {
            locationEntity = existingLocationOpt.get();
        }
        
        // Update fields from the DTO
        updateEntityFromDto(locationEntity, location);
        
        // Save to database
        locationEntity = locationRepository.save(locationEntity);
        
        // Publish Kafka event
        try {
            LocationEvent event = kafkaProducerService.createLocationEvent(
                    locationEntity, 
                    isNew ? LocationEvent.LocationEventType.CREATED : LocationEvent.LocationEventType.UPDATED);
            
            kafkaProducerService.sendLocationEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send location event: {}", exception.getMessage());
                        }
                    });
        } catch (Exception e) {
            // Log the error but don't fail the operation
            log.error("Error publishing location event: {}", e.getMessage(), e);
        }
        
        return convertToDto(locationEntity);
    }

    /**
     * Create or update an EVSE
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param location Location containing the EVSE to create or update
     * @return Created or updated location with EVSE
     */
    @Override
    @Transactional
    public Location putEvse(String countryCode, String partyId, String locationId, String evseUid, Location location) {
        // Implementation would add/update an EVSE to a location
        // For now, just trigger the location update with an EVSE-specific event type
        Location updatedLocation = putLocation(countryCode, partyId, locationId, location);
        
        // Publish EVSE-specific Kafka event
        try {
            Optional<LocationEntity> locationEntity = locationRepository
                    .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
            
            if (locationEntity.isPresent()) {
                LocationEvent event = kafkaProducerService.createLocationEvent(
                        locationEntity.get(), LocationEvent.LocationEventType.EVSE_UPDATED);
                
                kafkaProducerService.sendLocationEvent(event)
                        .whenComplete((result, exception) -> {
                            if (exception != null) {
                                log.error("Failed to send EVSE update event: {}", exception.getMessage());
                            }
                        });
            }
        } catch (Exception e) {
            log.error("Error publishing EVSE update event: {}", e.getMessage(), e);
        }
        
        return updatedLocation;
    }

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
    @Override
    @Transactional
    public Location putConnector(String countryCode, String partyId, String locationId, String evseUid, String connectorId, Location location) {
        // Implementation would add/update a connector to an EVSE
        // For now, just trigger the location update with a connector-specific event type
        Location updatedLocation = putEvse(countryCode, partyId, locationId, evseUid, location);
        
        // Publish connector-specific Kafka event
        try {
            Optional<LocationEntity> locationEntity = locationRepository
                    .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
            
            if (locationEntity.isPresent()) {
                LocationEvent event = kafkaProducerService.createLocationEvent(
                        locationEntity.get(), LocationEvent.LocationEventType.CONNECTOR_UPDATED);
                
                kafkaProducerService.sendLocationEvent(event)
                        .whenComplete((result, exception) -> {
                            if (exception != null) {
                                log.error("Failed to send connector update event: {}", exception.getMessage());
                            }
                        });
            }
        } catch (Exception e) {
            log.error("Error publishing connector update event: {}", e.getMessage(), e);
        }
        
        return updatedLocation;
    }

    /**
     * Delete a location
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @return True if deleted successfully
     */
    @Override
    @Transactional
    public boolean deleteLocation(String countryCode, String partyId, String locationId) {
        Optional<LocationEntity> locationEntityOpt = locationRepository
                .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
        
        if (!locationEntityOpt.isPresent()) {
            return false;
        }
        
        LocationEntity locationEntity = locationEntityOpt.get();
        
        // Publish delete event before actually deleting
        try {
            LocationEvent event = kafkaProducerService.createLocationEvent(
                    locationEntity, LocationEvent.LocationEventType.DELETED);
            
            kafkaProducerService.sendLocationEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send location delete event: {}", exception.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing location delete event: {}", e.getMessage(), e);
        }
        
        // Delete the location
        locationRepository.deleteByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
        
        return true;
    }

    /**
     * Delete an EVSE
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @return True if deleted successfully
     */
    @Override
    @Transactional
    public boolean deleteEvse(String countryCode, String partyId, String locationId, String evseUid) {
        // Implementation would remove an EVSE from a location
        // For this example, we'll just emit an event indicating the EVSE was removed
        
        Optional<LocationEntity> locationEntityOpt = locationRepository
                .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
        
        if (!locationEntityOpt.isPresent()) {
            return false;
        }
        
        // Publish EVSE removed event
        try {
            LocationEvent event = kafkaProducerService.createLocationEvent(
                    locationEntityOpt.get(), LocationEvent.LocationEventType.EVSE_REMOVED);
            event.setAdditionalData("evseUid=" + evseUid);
            
            kafkaProducerService.sendLocationEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send EVSE removed event: {}", exception.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing EVSE removed event: {}", e.getMessage(), e);
        }
        
        // In a real implementation, you would update the location to remove the EVSE
        
        return true;
    }

    /**
     * Delete a connector
     * @param countryCode Country code of the CPO
     * @param partyId Party ID of the CPO
     * @param locationId ID of the location
     * @param evseUid ID of the EVSE
     * @param connectorId ID of the connector
     * @return True if deleted successfully
     */
    @Override
    @Transactional
    public boolean deleteConnector(String countryCode, String partyId, String locationId, String evseUid, String connectorId) {
        // Implementation would remove a connector from an EVSE
        // For this example, we'll just emit an event indicating the connector was removed
        
        Optional<LocationEntity> locationEntityOpt = locationRepository
                .findByCountryCodeAndPartyIdAndLocationId(countryCode, partyId, locationId);
        
        if (!locationEntityOpt.isPresent()) {
            return false;
        }
        
        // Publish connector removed event
        try {
            LocationEvent event = kafkaProducerService.createLocationEvent(
                    locationEntityOpt.get(), LocationEvent.LocationEventType.CONNECTOR_REMOVED);
            event.setAdditionalData("evseUid=" + evseUid + ",connectorId=" + connectorId);
            
            kafkaProducerService.sendLocationEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send connector removed event: {}", exception.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing connector removed event: {}", e.getMessage(), e);
        }
        
        // In a real implementation, you would update the location to remove the connector
        
        return true;
    }

    /**
     * Convert a location entity to a DTO
     * @param entity Location entity
     * @return Location DTO
     */
    private Location convertToDto(LocationEntity entity) {
        // This is a simplified implementation
        // In a real application, you would map all fields and handle nested objects
        
        return Location.builder()
                .id(entity.getLocationId())
                .countryCode(entity.getCountryCode())
                .partyId(entity.getPartyId())
                .name(entity.getName())
                .address(entity.getAddress())
                .city(entity.getCity())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                // Add other fields and handle nested objects
                .lastUpdated(entity.getLastUpdated())
                .build();
    }

    /**
     * Update a location entity from a DTO
     * @param entity Location entity to update
     * @param dto DTO with updated values
     */
    private void updateEntityFromDto(LocationEntity entity, Location dto) {
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        entity.setCoordinates(dto.getCoordinates() != null ? 
                dto.getCoordinates().getLatitude() + "," + dto.getCoordinates().getLongitude() : null);
        entity.setTimeZone(dto.getTimeZone());
        entity.setLastUpdated(ZonedDateTime.now());
        entity.setUpdatedAt(ZonedDateTime.now());
        
        // In a real implementation, you would also handle:
        // - EVSEs and their connectors
        // - Related locations
        // - Other complex properties
    }
} 