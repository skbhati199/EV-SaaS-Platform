package com.ev.billingservice.service;

import com.ev.billingservice.dto.InvoiceDTO;
import com.ev.billingservice.model.Invoice.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    
    InvoiceDTO createInvoice(InvoiceDTO invoiceDTO);
    
    InvoiceDTO getInvoiceById(UUID id);
    
    InvoiceDTO getInvoiceByInvoiceNumber(String invoiceNumber);
    
    List<InvoiceDTO> getInvoicesBySubscriptionId(UUID subscriptionId);
    
    List<InvoiceDTO> getInvoicesByUserId(UUID userId);
    
    Page<InvoiceDTO> getInvoicesByUserId(UUID userId, Pageable pageable);
    
    List<InvoiceDTO> getInvoicesByOrganizationId(UUID organizationId);
    
    Page<InvoiceDTO> getInvoicesByOrganizationId(UUID organizationId, Pageable pageable);
    
    List<InvoiceDTO> getInvoicesByStatus(InvoiceStatus status);
    
    List<InvoiceDTO> getOverdueInvoices();
    
    InvoiceDTO updateInvoice(UUID id, InvoiceDTO invoiceDTO);
    
    InvoiceDTO markInvoiceAsPaid(UUID id, LocalDateTime paymentDate);
    
    InvoiceDTO cancelInvoice(UUID id);
    
    String generateInvoiceNumber();
    
    void processOverdueInvoices();
    
    void generateInvoicesForSubscriptions();
    
    UUID generateInvoiceForTransaction(UUID transactionId);
} 