package com.ev.station.service;

import com.ev.station.config.KafkaConfig;
import com.ev.station.dto.event.ChargingSessionEvent;
import com.ev.station.dto.event.StationStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for sending events to Kafka topics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send a station status event to Kafka
     *
     * @param event The station status event to send
     * @return A CompletableFuture for the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendStationStatusEvent(StationStatusEvent event) {
        log.info("Sending station status event for station: {}, new status: {}", 
                event.getStationId(), event.getNewStatus());

        // Ensure the event has an ID and timestamp
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.STATION_STATUS_TOPIC,
                event.getStationId().toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent station status event={}, offset={}", 
                        event, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send station status event={} due to: {}", 
                        event, ex.getMessage(), ex);
            }
        });

        return future;
    }

    /**
     * Send a charging session event to Kafka
     *
     * @param event The charging session event to send
     * @return A CompletableFuture for the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendChargingSessionEvent(ChargingSessionEvent event) {
        log.info("Sending charging session event: type={}, sessionId={}", 
                event.getEventType(), event.getSessionId());

        // Ensure the event has an ID
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.CHARGING_SESSION_TOPIC,
                event.getSessionId().toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent charging session event={}, offset={}", 
                        event, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send charging session event={} due to: {}", 
                        event, ex.getMessage(), ex);
            }
        });

        return future;
    }

    /**
     * Send a connector status event to Kafka
     *
     * @param connectorId The ID of the connector
     * @param event The event to send
     * @return A CompletableFuture for the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendConnectorStatusEvent(UUID connectorId, Object event) {
        log.info("Sending connector status event for connector: {}", connectorId);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.CONNECTOR_STATUS_TOPIC,
                connectorId.toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent connector status event for connector={}, offset={}", 
                        connectorId, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send connector status event for connector={} due to: {}", 
                        connectorId, ex.getMessage(), ex);
            }
        });

        return future;
    }

    /**
     * Send a telemetry event to Kafka
     *
     * @param stationId The ID of the station
     * @param event The telemetry event to send
     * @return A CompletableFuture for the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendTelemetryEvent(UUID stationId, Object event) {
        log.info("Sending telemetry event for station: {}", stationId);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.TELEMETRY_TOPIC,
                stationId.toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent telemetry event for station={}, offset={}", 
                        stationId, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send telemetry event for station={} due to: {}", 
                        stationId, ex.getMessage(), ex);
            }
        });

        return future;
    }
} 