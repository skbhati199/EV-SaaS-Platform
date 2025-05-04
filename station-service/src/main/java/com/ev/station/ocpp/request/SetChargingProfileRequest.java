package com.ev.station.ocpp.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCPP SetChargingProfile Request object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetChargingProfileRequest {
    
    /**
     * The connector ID for which the charging profile applies. 
     * Value 0 applies to the whole charge point.
     */
    private Integer connectorId;
    
    /**
     * The charging profile to be set
     */
    private ChargingProfile csChargingProfiles;
    
    /**
     * Charging profile definition according to OCPP
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargingProfile {
        /**
         * Unique identifier for this charging profile
         */
        private Integer chargingProfileId;
        
        /**
         * Indicates whether the charging profile can be stacked (combined)
         */
        private Integer stackLevel;
        
        /**
         * Purpose of the charging profile
         */
        private ChargingProfilePurposeType chargingProfilePurpose;
        
        /**
         * Kind of charging profile
         */
        private ChargingProfileKindType chargingProfileKind;
        
        /**
         * Contains schedule periods for the charging profile
         */
        private ChargingSchedule chargingSchedule;
        
        /**
         * Start timestamp of the profile (optional)
         */
        private String validFrom;
        
        /**
         * End timestamp of the profile (optional)
         */
        private String validTo;
        
        /**
         * Transaction ID for which this profile applies (optional)
         */
        private Integer transactionId;
    }
    
    /**
     * Charging schedule definition according to OCPP
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargingSchedule {
        /**
         * Duration of the charging schedule in seconds (optional)
         */
        private Integer duration;
        
        /**
         * Start schedule from this point in time (optional)
         */
        private String startSchedule;
        
        /**
         * Unit of the charging rate
         */
        private ChargingRateUnitType chargingRateUnit;
        
        /**
         * List of charging periods that define the schedule
         */
        private ChargingSchedulePeriod[] chargingSchedulePeriod;
        
        /**
         * Maximum charging rate for the entire schedule (optional)
         */
        private Float minChargingRate;
    }
    
    /**
     * Charging schedule period definition according to OCPP
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargingSchedulePeriod {
        /**
         * Start time of the period in seconds from the start of schedule
         */
        private Integer startPeriod;
        
        /**
         * Power limit during this period
         */
        private Float limit;
        
        /**
         * Number of phases (optional)
         */
        private Integer numberPhases;
    }
    
    /**
     * Charging profile purpose type
     */
    public enum ChargingProfilePurposeType {
        ChargePointMaxProfile,
        TxDefaultProfile,
        TxProfile
    }
    
    /**
     * Charging profile kind type
     */
    public enum ChargingProfileKindType {
        Absolute,
        Recurring,
        Relative
    }
    
    /**
     * Charging rate unit type
     */
    public enum ChargingRateUnitType {
        W,
        A
    }
} 