package com.ev.notificationservice.service;

import com.ev.notificationservice.config.KafkaConfig;
import com.ev.notificationservice.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Send a notification event to the appropriate Kafka topic based on the channel
     * @param event The notification event to send
     * @return The ID of the event that was sent
     */
    public UUID sendNotificationEvent(NotificationEvent event) {
        String channel = event.getChannel();
        UUID userId = event.getUserId();
        
        log.info("Sending notification event to channel {}: {}", channel, event);
        
        String topic = getTopicForChannel(channel);
        
        try {
            // Send to the channel-specific topic
            kafkaTemplate.send(topic, userId.toString(), event);
            log.info("Notification event sent to topic {}", topic);
            return event.getId();
        } catch (Exception e) {
            log.error("Failed to send notification event: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Get the appropriate Kafka topic for the given notification channel
     * @param channel The notification channel (email, sms, push)
     * @return The name of the Kafka topic
     */
    private String getTopicForChannel(String channel) {
        if (channel == null) {
            return KafkaConfig.EMAIL_NOTIFICATIONS_TOPIC;
        }
        
        switch (channel.toLowerCase()) {
            case "email":
                return KafkaConfig.EMAIL_NOTIFICATIONS_TOPIC;
            case "sms":
                return KafkaConfig.SMS_NOTIFICATIONS_TOPIC;
            case "push":
                return KafkaConfig.PUSH_NOTIFICATIONS_TOPIC;
            default:
                return KafkaConfig.EMAIL_NOTIFICATIONS_TOPIC;
        }
    }
} 