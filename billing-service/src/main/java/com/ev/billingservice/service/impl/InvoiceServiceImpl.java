package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.InvoiceDTO;
import com.ev.billingservice.dto.InvoiceItemDTO;
import com.ev.billingservice.exception.BadRequestException;
import com.ev.billingservice.exception.ResourceNotFoundException;
import com.ev.billingservice.model.Invoice;
import com.ev.billingservice.model.Invoice.InvoiceStatus;
import com.ev.billingservice.model.InvoiceItem;
import com.ev.billingservice.repository.InvoiceItemRepository;
import com.ev.billingservice.repository.InvoiceRepository;
import com.ev.billingservice.repository.SubscriptionRepository;
import com.ev.billingservice.service.InvoiceService;
import com.ev.billingservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;
    
    @Override
    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        // Generate invoice number if not provided
        if (invoiceDTO.getInvoiceNumber() == null || invoiceDTO.getInvoiceNumber().isEmpty()) {
            invoiceDTO.setInvoiceNumber(generateInvoiceNumber());
        } else if (invoiceRepository.findByInvoiceNumber(invoiceDTO.getInvoiceNumber()).isPresent()) {
            throw new BadRequestException("Invoice with number " + invoiceDTO.getInvoiceNumber() + " already exists");
        }
        
        Invoice invoice = mapToEntity(invoiceDTO);
        invoice = invoiceRepository.save(invoice);
        
        // Save invoice items if present
        if (invoiceDTO.getInvoiceItems() != null && !invoiceDTO.getInvoiceItems().isEmpty()) {
            List<InvoiceItem> invoiceItems = invoiceDTO.getInvoiceItems().stream()
                    .map(itemDTO -> {
                        InvoiceItem item = mapItemToEntity(itemDTO);
                        item.setInvoiceId(invoice.getId());
                        return item;
                    })
                    .collect(Collectors.toList());
            
            invoiceItemRepository.saveAll(invoiceItems);
            invoice.setInvoiceItems(invoiceItems);
        }
        
        // Send notification if invoice is in ISSUED status
        if (invoice.getStatus() == InvoiceStatus.ISSUED) {
            notificationService.sendInvoiceCreatedNotification(invoice);
        }
        
        return mapToDTO(invoice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(UUID id) {
        Invoice invoice = findInvoiceById(id);
        return mapToDTO(invoice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceByInvoiceNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "invoiceNumber", invoiceNumber));
        return mapToDTO(invoice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesBySubscriptionId(UUID subscriptionId) {
        List<Invoice> invoices = invoiceRepository.findBySubscriptionId(subscriptionId);
        return invoices.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByUserId(UUID userId) {
        List<Invoice> invoices = invoiceRepository.findByUserId(userId);
        return invoices.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getInvoicesByUserId(UUID userId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByUserId(userId, pageable);
        return invoices.map(this::mapToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByOrganizationId(UUID organizationId) {
        List<Invoice> invoices = invoiceRepository.findByOrganizationId(organizationId);
        return invoices.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getInvoicesByOrganizationId(UUID organizationId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByOrganizationId(organizationId, pageable);
        return invoices.map(this::mapToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByStatus(InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByStatus(status);
        return invoices.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now());
        return overdueInvoices.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public InvoiceDTO updateInvoice(UUID id, InvoiceDTO invoiceDTO) {
        Invoice invoice = findInvoiceById(id);
        
        // Don't allow changing invoice number
        if (!invoice.getInvoiceNumber().equals(invoiceDTO.getInvoiceNumber())) {
            throw new BadRequestException("Invoice number cannot be changed");
        }
        
        // Update fields
        invoice.setAmount(invoiceDTO.getAmount());
        invoice.setTaxAmount(invoiceDTO.getTaxAmount());
        invoice.setTotalAmount(invoiceDTO.getTotalAmount());
        invoice.setStatus(invoiceDTO.getStatus());
        invoice.setDueDate(invoiceDTO.getDueDate());
        invoice.setPaymentDate(invoiceDTO.getPaymentDate());
        
        invoice = invoiceRepository.save(invoice);
        
        // Send notification if status changes to OVERDUE
        if (invoice.getStatus() == InvoiceStatus.OVERDUE) {
            notificationService.sendPaymentOverdueNotification(invoice);
        }
        
        return mapToDTO(invoice);
    }
    
    @Override
    @Transactional
    public InvoiceDTO markInvoiceAsPaid(UUID id, LocalDateTime paymentDate) {
        Invoice invoice = findInvoiceById(id);
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice is already paid");
        }
        
        if (invoice.getStatus() == InvoiceStatus.CANCELED) {
            throw new BadRequestException("Cannot mark a canceled invoice as paid");
        }
        
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaymentDate(paymentDate != null ? paymentDate : LocalDateTime.now());
        invoice = invoiceRepository.save(invoice);
        
        return mapToDTO(invoice);
    }
    
    @Override
    @Transactional
    public InvoiceDTO cancelInvoice(UUID id) {
        Invoice invoice = findInvoiceById(id);
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Cannot cancel a paid invoice");
        }
        
        invoice.setStatus(InvoiceStatus.CANCELED);
        invoice = invoiceRepository.save(invoice);
        
        return mapToDTO(invoice);
    }
    
    @Override
    public String generateInvoiceNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        return "INV-" + year + "-" + randomPart;
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "${billing.invoice.overdue.check.cron:0 0 0 * * ?}") // Default: every day at midnight
    public void processOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findByStatusAndDueDateBefore(InvoiceStatus.ISSUED, LocalDateTime.now());
        
        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);
            
            // Send overdue notification
            notificationService.sendPaymentOverdueNotification(invoice);
            
            log.info("Marked invoice {} as overdue", invoice.getInvoiceNumber());
        }
    }
    
    @Override
    @Transactional
    @Scheduled(cron = "${billing.invoice.generation.cron:0 0 0 1 * ?}") // Default: first day of each month
    public void generateInvoicesForSubscriptions() {
        // Implementation for automatic invoice generation
        // This would typically get active subscriptions and generate invoices for them
        // Code omitted for brevity
    }
    
    private Invoice findInvoiceById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }
    
    private Invoice mapToEntity(InvoiceDTO invoiceDTO) {
        return Invoice.builder()
                .subscriptionId(invoiceDTO.getSubscriptionId())
                .userId(invoiceDTO.getUserId())
                .organizationId(invoiceDTO.getOrganizationId())
                .invoiceNumber(invoiceDTO.getInvoiceNumber())
                .amount(invoiceDTO.getAmount())
                .taxAmount(invoiceDTO.getTaxAmount())
                .totalAmount(invoiceDTO.getTotalAmount())
                .status(invoiceDTO.getStatus())
                .dueDate(invoiceDTO.getDueDate())
                .paymentDate(invoiceDTO.getPaymentDate())
                .build();
    }
    
    private InvoiceDTO mapToDTO(Invoice invoice) {
        List<InvoiceItemDTO> invoiceItems = null;
        if (invoice.getInvoiceItems() != null) {
            invoiceItems = invoice.getInvoiceItems().stream()
                    .map(this::mapItemToDTO)
                    .collect(Collectors.toList());
        } else {
            // Fetch items from repository if not loaded with the invoice
            List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoice.getId());
            invoiceItems = items.stream()
                    .map(this::mapItemToDTO)
                    .collect(Collectors.toList());
        }
        
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .subscriptionId(invoice.getSubscriptionId())
                .userId(invoice.getUserId())
                .organizationId(invoice.getOrganizationId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .amount(invoice.getAmount())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .paymentDate(invoice.getPaymentDate())
                .invoiceItems(invoiceItems)
                .build();
    }
    
    private InvoiceItem mapItemToEntity(InvoiceItemDTO itemDTO) {
        return InvoiceItem.builder()
                .invoiceId(itemDTO.getInvoiceId())
                .description(itemDTO.getDescription())
                .quantity(itemDTO.getQuantity())
                .unitPrice(itemDTO.getUnitPrice())
                .totalPrice(itemDTO.getTotalPrice())
                .build();
    }
    
    private InvoiceItemDTO mapItemToDTO(InvoiceItem item) {
        return InvoiceItemDTO.builder()
                .id(item.getId())
                .invoiceId(item.getInvoiceId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
} 