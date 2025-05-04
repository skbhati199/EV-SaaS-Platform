package com.ev.roamingservice.service;

import com.ev.roamingservice.dto.event.RoamingPartnerEvent;
import com.ev.roamingservice.model.OcpiConnectionStatus;
import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for managing roaming partners (CPOs and EMSPs) and publishing partner events to Kafka
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoamingPartnerService {

    private final KafkaProducerService kafkaProducerService;
    private final TokenService tokenService;
    
    /**
     * Register a new roaming partner
     * 
     * @param countryCode Country code of the party
     * @param partyId Party ID
     * @param name Name of the party
     * @param role Role (CPO, EMSP, etc.)
     * @param versionsUrl URL to the versions endpoint
     * @return The created party
     */
    @Transactional
    public OcpiParty registerPartner(String countryCode, String partyId, String name, OcpiRole role, String versionsUrl) {
        // Create party entity
        OcpiParty party = OcpiParty.builder()
                .countryCode(countryCode)
                .partyId(partyId)
                .name(name)
                .role(role)
                .versionsUrl(versionsUrl)
                .status(OcpiConnectionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        // In a real implementation, save the party to the database here
        
        // Publish partner creation event
        try {
            RoamingPartnerEvent event = kafkaProducerService.createRoamingPartnerEvent(
                    party, RoamingPartnerEvent.RoamingPartnerEventType.CREATED);
            
            kafkaProducerService.sendRoamingPartnerEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send partner creation event: {}", exception.getMessage());
                        } else {
                            log.debug("Partner creation event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing partner creation event: {}", e.getMessage(), e);
        }
        
        return party;
    }
    
    /**
     * Establish connection with a partner
     * 
     * @param party The party to connect with
     */
    @Transactional
    public void establishConnection(OcpiParty party) {
        // In a real implementation, update the party in the database
        party.setStatus(OcpiConnectionStatus.CONNECTED);
        party.setUpdatedAt(LocalDateTime.now());
        
        // Publish connection established event
        try {
            RoamingPartnerEvent event = kafkaProducerService.createRoamingPartnerEvent(
                    party, RoamingPartnerEvent.RoamingPartnerEventType.CONNECTION_ESTABLISHED);
            
            kafkaProducerService.sendRoamingPartnerEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send connection established event: {}", exception.getMessage());
                        } else {
                            log.debug("Connection established event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing connection established event: {}", e.getMessage(), e);
        }
        
        // Create and issue a permanent token (Type C) for the partner
        tokenService.createToken(party, OcpiTokenType.C, 24 * 30); // 30 days validity
    }
    
    /**
     * Suspend connection with a partner
     * 
     * @param party The party whose connection to suspend
     */
    @Transactional
    public void suspendConnection(OcpiParty party) {
        // In a real implementation, update the party in the database
        party.setStatus(OcpiConnectionStatus.SUSPENDED);
        party.setUpdatedAt(LocalDateTime.now());
        
        // Publish connection suspended event
        try {
            RoamingPartnerEvent event = kafkaProducerService.createRoamingPartnerEvent(
                    party, RoamingPartnerEvent.RoamingPartnerEventType.CONNECTION_SUSPENDED);
            
            kafkaProducerService.sendRoamingPartnerEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send connection suspended event: {}", exception.getMessage());
                        } else {
                            log.debug("Connection suspended event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing connection suspended event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Resume connection with a partner
     * 
     * @param party The party whose connection to resume
     */
    @Transactional
    public void resumeConnection(OcpiParty party) {
        // In a real implementation, update the party in the database
        party.setStatus(OcpiConnectionStatus.CONNECTED);
        party.setUpdatedAt(LocalDateTime.now());
        
        // Publish connection resumed event
        try {
            RoamingPartnerEvent event = kafkaProducerService.createRoamingPartnerEvent(
                    party, RoamingPartnerEvent.RoamingPartnerEventType.CONNECTION_RESUMED);
            
            kafkaProducerService.sendRoamingPartnerEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send connection resumed event: {}", exception.getMessage());
                        } else {
                            log.debug("Connection resumed event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing connection resumed event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Disconnect from a partner
     * 
     * @param party The party to disconnect from
     */
    @Transactional
    public void disconnectPartner(OcpiParty party) {
        // In a real implementation, update the party in the database
        party.setStatus(OcpiConnectionStatus.DISCONNECTED);
        party.setUpdatedAt(LocalDateTime.now());
        
        // Publish partner disconnected event
        try {
            RoamingPartnerEvent event = kafkaProducerService.createRoamingPartnerEvent(
                    party, RoamingPartnerEvent.RoamingPartnerEventType.CONNECTION_FAILED);
            
            kafkaProducerService.sendRoamingPartnerEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send partner disconnected event: {}", exception.getMessage());
                        } else {
                            log.debug("Partner disconnected event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing partner disconnected event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Delete a roaming partner
     * 
     * @param party The party to delete
     */
    @Transactional
    public void deletePartner(OcpiParty party) {
        // Publish partner deletion event
        try {
            RoamingPartnerEvent event = kafkaProducerService.createRoamingPartnerEvent(
                    party, RoamingPartnerEvent.RoamingPartnerEventType.DELETED);
            
            kafkaProducerService.sendRoamingPartnerEvent(event)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send partner deletion event: {}", exception.getMessage());
                        } else {
                            log.debug("Partner deletion event sent successfully: {}", event.getEventId());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing partner deletion event: {}", e.getMessage(), e);
        }
        
        // In a real implementation, delete the party from the database here
    }
} 