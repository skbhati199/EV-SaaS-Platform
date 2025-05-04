package com.ev.roamingservice.service;

import com.ev.roamingservice.config.KafkaConfig;
import com.ev.roamingservice.dto.event.CdrEvent;
import com.ev.roamingservice.dto.event.LocationEvent;
import com.ev.roamingservice.dto.event.RoamingPartnerEvent;
import com.ev.roamingservice.dto.event.TokenEvent;
import com.ev.roamingservice.model.LocationEntity;
import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for sending Kafka events related to roaming entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send a location event to Kafka
     * @param event The location event to send
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendLocationEvent(LocationEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(ZonedDateTime.now());
        }
        
        log.info("Sending location event: type={}, locationId={}, eventId={}", 
                event.getEventType(), event.getLocationId(), event.getEventId());
        
        return kafkaTemplate.send(KafkaConfig.LOCATION_EVENTS_TOPIC, event.getLocationId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Location event sent successfully: {}", event.getEventId());
                    } else {
                        log.error("Failed to send location event: {}", event.getEventId(), ex);
                    }
                });
    }
    
    /**
     * Send a token event to Kafka
     * @param event The token event to send
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendTokenEvent(TokenEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        
        log.info("Sending token event: type={}, tokenId={}, eventId={}", 
                event.getEventType(), event.getTokenId(), event.getEventId());
        
        return kafkaTemplate.send(KafkaConfig.TOKEN_EVENTS_TOPIC, event.getTokenId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Token event sent successfully: {}", event.getEventId());
                    } else {
                        log.error("Failed to send token event: {}", event.getEventId(), ex);
                    }
                });
    }
    
    /**
     * Send a roaming partner event to Kafka
     * @param event The roaming partner event to send
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendRoamingPartnerEvent(RoamingPartnerEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        
        log.info("Sending roaming partner event: type={}, partnerId={}, eventId={}", 
                event.getEventType(), event.getPartnerId(), event.getEventId());
        
        return kafkaTemplate.send(KafkaConfig.ROAMING_PARTNER_EVENTS_TOPIC, event.getPartnerId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Roaming partner event sent successfully: {}", event.getEventId());
                    } else {
                        log.error("Failed to send roaming partner event: {}", event.getEventId(), ex);
                    }
                });
    }
    
    /**
     * Send a CDR event to Kafka
     * @param event The CDR event to send
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendCdrEvent(CdrEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }
        
        if (event.getTimestamp() == null) {
            event.setTimestamp(ZonedDateTime.now());
        }
        
        log.info("Sending CDR event: type={}, cdrId={}, eventId={}", 
                event.getEventType(), event.getCdrId(), event.getEventId());
        
        return kafkaTemplate.send(KafkaConfig.CDR_EVENTS_TOPIC, event.getCdrId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("CDR event sent successfully: {}", event.getEventId());
                    } else {
                        log.error("Failed to send CDR event: {}", event.getEventId(), ex);
                    }
                });
    }
    
    /**
     * Create a location event from a LocationEntity
     * @param location The location entity
     * @param eventType The type of event
     * @return The created LocationEvent
     */
    public LocationEvent createLocationEvent(LocationEntity location, LocationEvent.LocationEventType eventType) {
        return LocationEvent.builder()
                .eventId(UUID.randomUUID())
                .locationId(location.getLocationId())
                .countryCode(location.getCountryCode())
                .partyId(location.getPartyId())
                .eventType(eventType)
                .timestamp(ZonedDateTime.now())
                .name(location.getName())
                .address(location.getAddress())
                .city(location.getCity())
                .coordinates(location.getCoordinates())
                .build();
    }
    
    /**
     * Create a token event from an OcpiToken entity
     * @param token The token entity
     * @param eventType The type of event
     * @return The created TokenEvent
     */
    public TokenEvent createTokenEvent(OcpiToken token, TokenEvent.TokenEventType eventType) {
        return TokenEvent.builder()
                .eventId(UUID.randomUUID())
                .tokenId(token.getId())
                .tokenValue(token.getToken())
                .tokenType(token.getTokenType())
                .eventType(eventType)
                .partyId(token.getParty() != null ? token.getParty().getId() : null)
                .countryCode(token.getParty() != null ? token.getParty().getCountryCode() : null)
                .partyIdText(token.getParty() != null ? token.getParty().getPartyId() : null)
                .timestamp(LocalDateTime.now())
                .validUntil(token.getValidUntil())
                .revoked(token.isRevoked())
                .build();
    }
    
    /**
     * Create a roaming partner event from an OcpiParty entity
     * @param party The party entity
     * @param eventType The type of event
     * @return The created RoamingPartnerEvent
     */
    public RoamingPartnerEvent createRoamingPartnerEvent(OcpiParty party, RoamingPartnerEvent.RoamingPartnerEventType eventType) {
        return RoamingPartnerEvent.builder()
                .eventId(UUID.randomUUID())
                .partnerId(party.getId())
                .countryCode(party.getCountryCode())
                .partyId(party.getPartyId())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .name(party.getName())
                .role(party.getRole())
                .status(party.getStatus())
                .versionsUrl(party.getVersionsUrl())
                .build();
    }
} 