package com.ev.notificationservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.application.name}")
    private String applicationName;

    // Topic configurations for notification service
    public static final String EMAIL_NOTIFICATIONS_TOPIC = "email-notifications";
    public static final String SMS_NOTIFICATIONS_TOPIC = "sms-notifications";
    public static final String PUSH_NOTIFICATIONS_TOPIC = "push-notifications";
    
    // Topics from other services that notification service consumes
    public static final String PAYMENT_EVENTS_TOPIC = "payment-events";
    public static final String INVOICE_EVENTS_TOPIC = "invoice-events";
    public static final String CHARGING_SESSION_TOPIC = "charging-session-events";
    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String WALLET_EVENTS_TOPIC = "wallet-events";
    public static final String RFID_TOKEN_EVENTS_TOPIC = "rfid-token-events";
    
    // Consumer group for the notification service
    public static final String NOTIFICATION_CONSUMER_GROUP = "notification-service-group";

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

    // Consumer configuration
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, NOTIFICATION_CONSUMER_GROUP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        // Set consumer client ID
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, applicationName + "-consumer");
        
        // Increase fetch size to improve throughput
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        
        // Trust packages from all services
        props.put(JsonDeserializer.TRUSTED_PACKAGES, 
                "com.ev.notificationservice.dto," +
                "com.ev.notificationservice.dto.event," +
                "com.ev.station.dto.event," +
                "com.ev.billingservice.dto.event," +
                "com.ev.userservice.dto.event");
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // Configure manual commits
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        // Configure concurrency
        factory.setConcurrency(3);
        
        // Configure error handling with retries
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 3)));
        
        return factory;
    }

    // Topic creation
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic emailNotificationsTopic() {
        return new NewTopic(EMAIL_NOTIFICATIONS_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic smsNotificationsTopic() {
        return new NewTopic(SMS_NOTIFICATIONS_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic pushNotificationsTopic() {
        return new NewTopic(PUSH_NOTIFICATIONS_TOPIC, 3, (short) 1);
    }
} 