package com.ev.billingservice.repository;

import com.ev.billingservice.model.Payment;
import com.ev.billingservice.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    List<Payment> findByInvoiceId(UUID invoiceId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
}