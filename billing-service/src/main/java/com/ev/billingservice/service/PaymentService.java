package com.ev.billingservice.service;

import com.ev.billingservice.dto.PaymentDTO;
import com.ev.billingservice.model.Payment.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    
    PaymentDTO createPayment(PaymentDTO paymentDTO);
    
    PaymentDTO getPaymentById(UUID id);
    
    List<PaymentDTO> getPaymentsByInvoiceId(UUID invoiceId);
    
    List<PaymentDTO> getPaymentsByStatus(PaymentStatus status);
    
    PaymentDTO getPaymentByTransactionId(String transactionId);
    
    List<PaymentDTO> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end);
    
    PaymentDTO updatePayment(UUID id, PaymentDTO paymentDTO);
    
    PaymentDTO processPayment(PaymentDTO paymentDTO);
    
    PaymentDTO refundPayment(UUID id);
} 