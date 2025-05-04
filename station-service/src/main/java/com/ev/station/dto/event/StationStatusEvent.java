package com.ev.station.dto.event;

import com.ev.station.model.StationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event sent when a station's status changes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationStatusEvent {
    /**
     * Unique identifier for the event
     */
    private UUID eventId;
    
    /**
     * ID of the station
     */
    private UUID stationId;
    
    /**
     * Station model
     */
    private String stationModel;
    
    /**
     * Station vendor
     */
    private String stationVendor;
    
    /**
     * Station serial number
     */
    private String serialNumber;
    
    /**
     * Previous status of the station
     */
    private StationStatus previousStatus;
    
    /**
     * New status of the station
     */
    private StationStatus newStatus;
    
    /**
     * Timestamp when the status change occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * Optional reason for the status change
     */
    private String reason;
    
    /**
     * Optional error code if the status change was due to an error
     */
    private String errorCode;
} 