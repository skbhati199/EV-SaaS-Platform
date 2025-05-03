package com.ev.notificationservice.service;

import com.ev.notificationservice.config.KafkaConfig;
import com.ev.notificationservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Send a notification event to the appropriate topic based on the channel
     * @param event The notification event to send
     * @return CompletableFuture of the send result
     */
    public CompletableFuture<SendResult<String, Object>> sendNotificationEvent(NotificationEvent event) {
        String topic = getTopicByChannel(event.getChannel());
        String key = event.getUserId().toString();
        
        log.info("Sending notification event to topic {}: {}", topic, event);
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        
        // Also send to the all-notifications topic for analytics and auditing
        kafkaTemplate.send(KafkaConfig.TOPIC_ALL_NOTIFICATIONS, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent notification event to topic {}: offset = {}", topic, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send notification event to topic " + topic, ex);
            }
        });
        
        return future;
    }
    
    /**
     * Send a batch of notifications in a transaction
     * @param events The notification events to send
     */
    public void sendNotificationEvents(Iterable<NotificationEvent> events) {
        events.forEach(this::sendNotificationEvent);
    }
    
    /**
     * Get the appropriate topic for the given channel
     * @param channel The notification channel (EMAIL, SMS, PUSH)
     * @return The Kafka topic name
     */
    private String getTopicByChannel(String channel) {
        if (channel == null) {
            return KafkaConfig.TOPIC_ALL_NOTIFICATIONS;
        }
        
        return switch (channel.toUpperCase()) {
            case "EMAIL" -> KafkaConfig.TOPIC_EMAIL_NOTIFICATIONS;
            case "SMS" -> KafkaConfig.TOPIC_SMS_NOTIFICATIONS;
            case "PUSH" -> KafkaConfig.TOPIC_PUSH_NOTIFICATIONS;
            default -> KafkaConfig.TOPIC_ALL_NOTIFICATIONS;
        };
    }
} 