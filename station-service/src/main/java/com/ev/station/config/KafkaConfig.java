package com.ev.station.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:kafka:9092}")
    private String bootstrapServers;

    @Value("${spring.application.name:station-service}")
    private String applicationName;

    // Topic configurations
    public static final String STATION_STATUS_TOPIC = "station-status-events";
    public static final String CHARGING_SESSION_TOPIC = "charging-session-events";
    public static final String CONNECTOR_STATUS_TOPIC = "connector-status-events";
    public static final String TELEMETRY_TOPIC = "telemetry-events";

    // Producer configuration
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, applicationName + "-producer");
        
        // Add reliability configurations
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Highest reliability
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);  // Retry on errors
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // Retry delay
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Prevent duplicates
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Topic creation
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic stationStatusTopic() {
        return new NewTopic(STATION_STATUS_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic chargingSessionTopic() {
        return new NewTopic(CHARGING_SESSION_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic connectorStatusTopic() {
        return new NewTopic(CONNECTOR_STATUS_TOPIC, 3, (short) 1);
    }
    
    @Bean
    public NewTopic telemetryTopic() {
        return new NewTopic(TELEMETRY_TOPIC, 3, (short) 1);
    }
} 