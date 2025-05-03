package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.PaymentDTO;
import com.ev.billingservice.exception.BadRequestException;
import com.ev.billingservice.exception.ResourceNotFoundException;
import com.ev.billingservice.model.Invoice;
import com.ev.billingservice.model.Invoice.InvoiceStatus;
import com.ev.billingservice.model.Payment;
import com.ev.billingservice.model.Payment.PaymentStatus;
import com.ev.billingservice.repository.InvoiceRepository;
import com.ev.billingservice.repository.PaymentRepository;
import com.ev.billingservice.service.NotificationService;
import com.ev.billingservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;
    
    @Override
    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        // Verify the invoice exists
        Invoice invoice = invoiceRepository.findById(paymentDTO.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", paymentDTO.getInvoiceId()));
        
        // Set the user ID based on the invoice
        Payment payment = mapToEntity(paymentDTO);
        payment.setUserId(invoice.getUserId());
        payment = paymentRepository.save(payment);
        
        // Update invoice status if payment is successful
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            updateInvoiceStatus(invoice, payment.getAmount(), payment.getPaymentDate());
            
            // Send notification for successful payment
            notificationService.sendPaymentReceivedNotification(payment);
        }
        
        return mapToDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(UUID id) {
        Payment payment = findPaymentById(id);
        return mapToDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByInvoiceId(UUID invoiceId) {
        List<Payment> payments = paymentRepository.findByInvoiceId(invoiceId);
        return payments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "transactionId", transactionId));
        return mapToDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Payment> payments = paymentRepository.findByPaymentDateBetween(start, end);
        return payments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PaymentDTO updatePayment(UUID id, PaymentDTO paymentDTO) {
        Payment existingPayment = findPaymentById(id);
        
        // Don't allow changing invoice ID
        if (!existingPayment.getInvoiceId().equals(paymentDTO.getInvoiceId())) {
            throw new BadRequestException("Invoice ID cannot be changed");
        }
        
        // Update fields
        existingPayment.setAmount(paymentDTO.getAmount());
        existingPayment.setPaymentMethod(paymentDTO.getPaymentMethod());
        existingPayment.setTransactionId(paymentDTO.getTransactionId());
        existingPayment.setStatus(paymentDTO.getStatus());
        existingPayment.setPaymentDate(paymentDTO.getPaymentDate());
        
        final Payment savedPayment = paymentRepository.save(existingPayment);
        
        // If payment status changed to COMPLETED, update invoice and send notification
        if (paymentDTO.getStatus() == PaymentStatus.COMPLETED && 
                savedPayment.getStatus() == PaymentStatus.COMPLETED) {
            
            final UUID invoiceId = savedPayment.getInvoiceId();
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
            
            updateInvoiceStatus(invoice, savedPayment.getAmount(), savedPayment.getPaymentDate());
            
            // Send notification for successful payment
            notificationService.sendPaymentReceivedNotification(savedPayment);
        }
        
        return mapToDTO(savedPayment);
    }
    
    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        // This method would typically integrate with a payment processor
        // For demonstration, we're assuming the payment is successful
        
        paymentDTO.setStatus(PaymentStatus.COMPLETED);
        paymentDTO.setTransactionId(UUID.randomUUID().toString());
        
        return createPayment(paymentDTO);
    }
    
    @Override
    @Transactional
    public PaymentDTO refundPayment(UUID id) {
        Payment payment = findPaymentById(id);
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BadRequestException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        final Payment savedPayment = paymentRepository.save(payment);
        
        // Update invoice status
        final UUID invoiceId = savedPayment.getInvoiceId();
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        
        invoice.setStatus(InvoiceStatus.REFUNDED);
        invoiceRepository.save(invoice);
        
        return mapToDTO(savedPayment);
    }
    
    private void updateInvoiceStatus(Invoice invoice, java.math.BigDecimal paymentAmount, LocalDateTime paymentDate) {
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return; // Already paid
        }
        
        // Check if payment covers the total amount
        if (paymentAmount.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaymentDate(paymentDate);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }
        
        invoiceRepository.save(invoice);
    }
    
    private Payment findPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }
    
    private Payment mapToEntity(PaymentDTO paymentDTO) {
        return Payment.builder()
                .invoiceId(paymentDTO.getInvoiceId())
                .amount(paymentDTO.getAmount())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .transactionId(paymentDTO.getTransactionId())
                .status(paymentDTO.getStatus())
                .paymentDate(paymentDTO.getPaymentDate())
                .build();
    }
    
    private PaymentDTO mapToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoiceId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
} 