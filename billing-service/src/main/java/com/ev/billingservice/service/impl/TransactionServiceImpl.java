package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.event.ChargingSessionEvent;
import com.ev.billingservice.dto.event.InvoiceEvent;
import com.ev.billingservice.model.ChargingTransaction;
import com.ev.billingservice.model.TransactionStatus;
import com.ev.billingservice.repository.ChargingTransactionRepository;
import com.ev.billingservice.service.BillingPlanService;
import com.ev.billingservice.service.InvoiceService;
import com.ev.billingservice.service.SubscriptionService;
import com.ev.billingservice.service.TransactionService;
import com.ev.billingservice.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final ChargingTransactionRepository transactionRepository;
    private final BillingPlanService billingPlanService;
    private final SubscriptionService subscriptionService;
    private final InvoiceService invoiceService;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public UUID createPendingTransaction(
            UUID sessionId,
            UUID userId,
            UUID stationId,
            UUID connectorId,
            LocalDateTime startTime) {
        
        log.info("Creating pending transaction for session: {}, user: {}", sessionId, userId);
        
        // Check if transaction already exists
        if (transactionRepository.findBySessionId(sessionId).isPresent()) {
            log.warn("Transaction already exists for session: {}", sessionId);
            return transactionRepository.findBySessionId(sessionId).get().getId();
        }
        
        // Find user's subscription
        UUID subscriptionId = null;
        UUID billingPlanId = null;
        
        try {
            var subscription = subscriptionService.getActiveSubscriptionForUser(userId);
            if (subscription != null) {
                subscriptionId = subscription.getId();
                billingPlanId = subscription.getBillingPlanId();
            }
        } catch (Exception e) {
            log.error("Error getting subscription for user: {}", userId, e);
        }
        
        // Create transaction
        ChargingTransaction transaction = ChargingTransaction.builder()
                .sessionId(sessionId)
                .userId(userId)
                .stationId(stationId)
                .connectorId(connectorId)
                .status(TransactionStatus.PENDING)
                .startTime(startTime)
                .energyDeliveredKwh(BigDecimal.ZERO)
                .durationSeconds(0L)
                .subscriptionId(subscriptionId)
                .billingPlanId(billingPlanId)
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        log.info("Created pending transaction: {} for session: {}", transaction.getId(), sessionId);
        
        return transaction.getId();
    }

    @Override
    @Transactional
    public void updatePendingTransaction(
            UUID sessionId,
            BigDecimal energyDeliveredKwh,
            Long durationSeconds,
            BigDecimal meterValue) {
        
        log.info("Updating transaction for session: {}", sessionId);
        
        ChargingTransaction transaction = transactionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for session: " + sessionId));
        
        // Only update if pending
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.warn("Cannot update transaction with status: {}", transaction.getStatus());
            return;
        }
        
        // Update transaction
        transaction.setEnergyDeliveredKwh(energyDeliveredKwh);
        transaction.setDurationSeconds(durationSeconds);
        transaction.setMeterValue(meterValue);
        
        // Update estimated amount
        if (transaction.getBillingPlanId() != null) {
            try {
                BigDecimal amount = calculateAmount(
                        transaction.getBillingPlanId(),
                        energyDeliveredKwh,
                        durationSeconds);
                
                transaction.setAmount(amount);
            } catch (Exception e) {
                log.error("Error calculating amount for transaction: {}", transaction.getId(), e);
            }
        }
        
        transactionRepository.save(transaction);
        
        log.info("Updated transaction: {} for session: {}", transaction.getId(), sessionId);
    }

    @Override
    @Transactional
    public UUID completeTransaction(
            UUID sessionId,
            LocalDateTime endTime,
            BigDecimal energyDeliveredKwh,
            Long durationSeconds) {
        
        log.info("Completing transaction for session: {}", sessionId);
        
        ChargingTransaction transaction = transactionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for session: " + sessionId));
        
        // Only complete if pending
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.warn("Cannot complete transaction with status: {}", transaction.getStatus());
            return null;
        }
        
        // Update transaction
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setEndTime(endTime);
        transaction.setEnergyDeliveredKwh(energyDeliveredKwh);
        transaction.setDurationSeconds(durationSeconds);
        
        // Calculate final amount
        if (transaction.getBillingPlanId() != null) {
            try {
                BigDecimal amount = calculateAmount(
                        transaction.getBillingPlanId(),
                        energyDeliveredKwh,
                        durationSeconds);
                
                transaction.setAmount(amount);
                transaction.setCurrency("USD"); // TODO: Get from billing plan
            } catch (Exception e) {
                log.error("Error calculating amount for transaction: {}", transaction.getId(), e);
            }
        }
        
        transaction = transactionRepository.save(transaction);
        
        log.info("Completed transaction: {} for session: {}", transaction.getId(), sessionId);
        
        // Generate invoice for single transaction (can be batched later)
        try {
            UUID invoiceId = generateInvoice(transaction);
            log.info("Generated invoice: {} for transaction: {}", invoiceId, transaction.getId());
            return invoiceId;
        } catch (Exception e) {
            log.error("Error generating invoice for transaction: {}", transaction.getId(), e);
            return null;
        }
    }

    @Override
    public Object getTransactionBySessionId(UUID sessionId) {
        return transactionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for session: " + sessionId));
    }
    
    /**
     * Calculate the amount for a transaction based on the billing plan
     */
    private BigDecimal calculateAmount(UUID billingPlanId, BigDecimal energyDeliveredKwh, Long durationSeconds) {
        // Get the billing plan
        var billingPlan = billingPlanService.getBillingPlanById(billingPlanId);
        
        // Simple calculation based on energy and time
        // In a real implementation, this would be more complex based on the billing plan
        BigDecimal energyRate = billingPlan.getEnergyRate() != null ? 
                billingPlan.getEnergyRate() : BigDecimal.valueOf(0.20); // $0.20 per kWh default
        
        BigDecimal timeRate = billingPlan.getTimeRate() != null ?
                billingPlan.getTimeRate() : BigDecimal.valueOf(0.01); // $0.01 per minute default
                
        BigDecimal energyCost = energyDeliveredKwh.multiply(energyRate);
        BigDecimal timeCost = BigDecimal.valueOf(durationSeconds / 60.0).multiply(timeRate);
        
        return energyCost.add(timeCost);
    }
    
    /**
     * Generate an invoice for a transaction
     */
    private UUID generateInvoice(ChargingTransaction transaction) {
        // Mark as invoiced
        transaction.setStatus(TransactionStatus.INVOICED);
        transactionRepository.save(transaction);
        
        // Generate invoice - this would call the invoice service
        UUID invoiceId = invoiceService.generateInvoiceForTransaction(transaction.getId());
        
        // Get invoice details
        var invoice = invoiceService.getInvoiceById(invoiceId);
        
        // Send invoice event
        try {
            InvoiceEvent event = InvoiceEvent.builder()
                .eventId(UUID.randomUUID())
                .invoiceId(invoiceId)
                .userId(transaction.getUserId())
                .eventType("CREATED")
                .invoiceNumber(invoice.getInvoiceNumber())
                .totalAmount(invoice.getTotalAmount())
                .currency(invoice.getCurrency())
                .status(invoice.getStatus())
                .issuedAt(invoice.getIssuedAt())
                .dueAt(invoice.getDueAt())
                .chargingSessionIds(Collections.singletonList(transaction.getSessionId()))
                .transactionIds(Collections.singletonList(transaction.getId()))
                .timestamp(LocalDateTime.now())
                .invoiceUrl("/api/v1/billing/invoices/" + invoiceId) // Sample URL
                .build();
            
            kafkaProducerService.sendInvoiceEvent(event);
            log.debug("Sent invoice event for invoice: {}", invoiceId);
        } catch (Exception e) {
            log.error("Failed to send invoice event for invoice {}: {}", invoiceId, e.getMessage(), e);
            // Don't throw - we still want to return the invoice ID even if the event sending fails
        }
        
        return invoiceId;
    }
} 