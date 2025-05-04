package com.ev.userservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for the User service.
 * Sets up producer, admin, and topic configurations.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.application.name}")
    private String applicationName;

    // Topic configurations for user events
    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String WALLET_EVENTS_TOPIC = "wallet-events";
    public static final String RFID_TOKEN_EVENTS_TOPIC = "rfid-token-events";
    
    // Consumer group for the user service
    public static final String USER_CONSUMER_GROUP = "user-service-group";

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

    // Topic creation with Kafka Admin
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic(USER_EVENTS_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic walletEventsTopic() {
        return new NewTopic(WALLET_EVENTS_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic rfidTokenEventsTopic() {
        return new NewTopic(RFID_TOKEN_EVENTS_TOPIC, 3, (short) 1);
    }
} 