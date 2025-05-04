package com.ev.billingservice.service;

import com.ev.billingservice.config.KafkaConfig;
import com.ev.billingservice.dto.event.InvoiceEvent;
import com.ev.billingservice.dto.event.PaymentEvent;
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
     * Send a payment event to Kafka
     *
     * @param event The payment event to send
     * @return A CompletableFuture for the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendPaymentEvent(PaymentEvent event) {
        log.info("Sending payment event: type={}, paymentId={}", 
                event.getEventType(), event.getPaymentId());

        // Ensure the event has an ID and timestamp
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.PAYMENT_EVENTS_TOPIC,
                event.getPaymentId().toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent payment event={}, offset={}", 
                        event, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send payment event={} due to: {}", 
                        event, ex.getMessage(), ex);
            }
        });

        return future;
    }

    /**
     * Send an invoice event to Kafka
     *
     * @param event The invoice event to send
     * @return A CompletableFuture for the send operation
     */
    public CompletableFuture<SendResult<String, Object>> sendInvoiceEvent(InvoiceEvent event) {
        log.info("Sending invoice event: type={}, invoiceId={}", 
                event.getEventType(), event.getInvoiceId());

        // Ensure the event has an ID
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.INVOICE_EVENTS_TOPIC,
                event.getInvoiceId().toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent invoice event={}, offset={}", 
                        event, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send invoice event={} due to: {}", 
                        event, ex.getMessage(), ex);
            }
        });

        return future;
    }
} 