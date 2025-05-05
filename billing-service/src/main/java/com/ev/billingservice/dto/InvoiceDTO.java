package com.ev.billingservice.dto;

import com.ev.billingservice.model.Invoice.InvoiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    
    private UUID id;
    
    @NotNull(message = "Subscription ID is required")
    private UUID subscriptionId;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Organization ID is required")
    private UUID organizationId;
    
    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be non-negative")
    private BigDecimal amount;
    
    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be non-negative")
    private BigDecimal taxAmount;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total amount must be non-negative")
    private BigDecimal totalAmount;
    
    @NotNull(message = "Status is required")
    private InvoiceStatus status;
    
    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;
    
    private LocalDateTime paymentDate;
    
    private List<InvoiceItemDTO> invoiceItems;
    
    private List<PaymentDTO> payments;
    
    private String currency;
    
    @NotNull(message = "Issue date is required")
    private LocalDateTime issuedAt;
    
    @NotNull(message = "Due date is required")
    private LocalDateTime dueAt;
} 