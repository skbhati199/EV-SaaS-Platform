package com.ev.billingservice.controller;

import com.ev.billingservice.dto.InvoiceDTO;
import com.ev.billingservice.model.Invoice.InvoiceStatus;
import com.ev.billingservice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceDTO invoiceDTO) {
        return new ResponseEntity<>(invoiceService.createInvoice(invoiceDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
    
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceDTO> getInvoiceByInvoiceNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByInvoiceNumber(invoiceNumber));
    }
    
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesBySubscriptionId(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(invoiceService.getInvoicesBySubscriptionId(subscriptionId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId));
    }
    
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<InvoiceDTO>> getPagedInvoicesByUserId(@PathVariable UUID userId, Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUserId(userId, pageable));
    }
    
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByOrganizationId(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByOrganizationId(organizationId));
    }
    
    @GetMapping("/organization/{organizationId}/paged")
    public ResponseEntity<Page<InvoiceDTO>> getPagedInvoicesByOrganizationId(@PathVariable UUID organizationId, Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getInvoicesByOrganizationId(organizationId, pageable));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceDTO>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable UUID id, @Valid @RequestBody InvoiceDTO invoiceDTO) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceDTO));
    }
    
    @PutMapping("/{id}/mark-paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDTO> markInvoiceAsPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.markInvoiceAsPaid(id, LocalDateTime.now()));
    }
    
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceDTO> cancelInvoice(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.cancelInvoice(id));
    }
    
    @PostMapping("/process-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> processOverdueInvoices() {
        invoiceService.processOverdueInvoices();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> generateInvoicesForSubscriptions() {
        invoiceService.generateInvoicesForSubscriptions();
        return ResponseEntity.ok().build();
    }
}