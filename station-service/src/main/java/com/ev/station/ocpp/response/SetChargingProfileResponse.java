package com.ev.station.ocpp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCPP SetChargingProfile Response object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetChargingProfileResponse {
    
    /**
     * Status of the charging profile setting operation
     */
    private ChargingProfileStatus status;
    
    /**
     * Possible status values for the SetChargingProfile response
     */
    public enum ChargingProfileStatus {
        /**
         * Request has been accepted and charging profile will be applied
         */
        Accepted,
        
        /**
         * Request has been rejected
         */
        Rejected,
        
        /**
         * Profile is not supported by the charging station
         */
        NotSupported
    }
} 