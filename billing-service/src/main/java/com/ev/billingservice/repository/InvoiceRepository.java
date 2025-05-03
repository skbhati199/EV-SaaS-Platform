package com.ev.billingservice.repository;

import com.ev.billingservice.model.Invoice;
import com.ev.billingservice.model.Invoice.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findBySubscriptionId(UUID subscriptionId);
    
    List<Invoice> findByUserId(UUID userId);
    
    Page<Invoice> findByUserId(UUID userId, Pageable pageable);
    
    List<Invoice> findByOrganizationId(UUID organizationId);
    
    Page<Invoice> findByOrganizationId(UUID organizationId, Pageable pageable);
    
    List<Invoice> findByStatus(InvoiceStatus status);
    
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDateTime dueDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = 'ISSUED' AND i.dueDate < ?1")
    List<Invoice> findOverdueInvoices(LocalDateTime now);
} 