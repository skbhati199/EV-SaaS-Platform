package com.ev.roamingservice.service;

import com.ev.roamingservice.dto.event.CdrEvent;
import com.ev.roamingservice.dto.event.LocationEvent;
import com.ev.roamingservice.dto.event.RoamingPartnerEvent;
import com.ev.roamingservice.dto.event.TokenEvent;
import com.ev.roamingservice.model.LocationEntity;
import com.ev.roamingservice.model.OcpiConnectionStatus;
import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiRole;
import com.ev.roamingservice.model.OcpiToken;
import com.ev.roamingservice.model.OcpiTokenType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class to demonstrate and validate the Kafka event functionality
 * for the Roaming service.
 */
@ExtendWith(MockitoExtension.class)
public class RoamingEventTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Mock
    private SendResult<String, Object> sendResult;
    
    @Spy
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private KafkaProducerService kafkaProducerService;
    
    @InjectMocks
    private CdrService cdrService;
    
    @InjectMocks
    private TokenService tokenService;
    
    @InjectMocks
    private RoamingPartnerService roamingPartnerService;
    
    private CompletableFuture<SendResult<String, Object>> completableFuture;
    
    @BeforeEach
    public void setup() {
        completableFuture = new CompletableFuture<>();
        completableFuture.complete(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(completableFuture);
        
        // Set up dependencies
        cdrService = new CdrService(kafkaProducerService, objectMapper);
        tokenService = new TokenService(kafkaProducerService);
        roamingPartnerService = new RoamingPartnerService(kafkaProducerService, tokenService);
    }
    
    @Test
    public void testLocationEventProduction() {
        // Create a test location entity
        LocationEntity location = LocationEntity.builder()
                .id(1L)
                .locationId("LOC1")
                .countryCode("US")
                .partyId("CPO")
                .name("Test Location")
                .address("123 Test St")
                .city("Test City")
                .coordinates("37.7749,-122.4194")
                .build();
        
        // Create and send a location event
        LocationEvent event = kafkaProducerService.createLocationEvent(
                location, LocationEvent.LocationEventType.CREATED);
        kafkaProducerService.sendLocationEvent(event);
        
        // Verify the event was sent to Kafka
        verify(kafkaTemplate).send(
                eq("location-events"), 
                eq(location.getLocationId()), 
                any(LocationEvent.class));
    }
    
    @Test
    public void testTokenEventProduction() {
        // Create a test party
        OcpiParty party = OcpiParty.builder()
                .id(1L)
                .partyId("CPO")
                .countryCode("US")
                .role(OcpiRole.CPO)
                .status(OcpiConnectionStatus.CONNECTED)
                .build();
        
        // Create a test token
        OcpiToken token = OcpiToken.builder()
                .id(1L)
                .token(UUID.randomUUID().toString())
                .tokenType(OcpiTokenType.C)
                .party(party)
                .validUntil(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();
        
        // Test creating a token
        tokenService.createToken(party, OcpiTokenType.A, 24);
        
        // Verify the event was sent to Kafka
        verify(kafkaTemplate).send(
                eq("token-events"), 
                anyString(), 
                any(TokenEvent.class));
    }
    
    @Test
    public void testRoamingPartnerEventProduction() {
        // Create a test party
        OcpiParty party = OcpiParty.builder()
                .id(1L)
                .partyId("CPO")
                .countryCode("US")
                .name("Test CPO")
                .role(OcpiRole.CPO)
                .status(OcpiConnectionStatus.PENDING)
                .versionsUrl("https://example.com/ocpi")
                .build();
        
        // Test registering a partner
        roamingPartnerService.registerPartner(
                "US", "CPO", "Test CPO", OcpiRole.CPO, "https://example.com/ocpi");
        
        // Verify the event was sent to Kafka
        verify(kafkaTemplate).send(
                eq("roaming-partner-events"), 
                anyString(), 
                any(RoamingPartnerEvent.class));
    }
    
    @Test
    public void testCdrEventGenerationFromChargingSession() {
        // Create a JSON charging session event
        ObjectNode sessionEvent = objectMapper.createObjectNode()
                .put("eventType", "COMPLETED")
                .put("sessionId", "SESSION1")
                .put("startTime", ZonedDateTime.now().minusHours(1).toString())
                .put("endTime", ZonedDateTime.now().toString())
                .put("stationId", "STATION1")
                .put("evseId", "EVSE1")
                .put("connectorId", "CONNECTOR1")
                .put("energyDelivered", "10.5")
                .put("totalCost", "25.75")
                .put("currency", "USD")
                .put("authorizationId", "AUTH1");
        
        // Process the charging session event
        cdrService.processChargingSessionEvent(sessionEvent.toString());
        
        // Verify the CDR event was sent to Kafka
        verify(kafkaTemplate).send(
                eq("cdr-events"), 
                anyString(), 
                any(CdrEvent.class));
    }
    
    @Test
    public void testCdrStatusUpdate() {
        // Test updating CDR status
        cdrService.updateCdrStatus("CDR1", "SENT");
        
        // Verify the event was sent to Kafka
        verify(kafkaTemplate).send(
                eq("cdr-events"), 
                eq("CDR1"), 
                any(CdrEvent.class));
    }
} 