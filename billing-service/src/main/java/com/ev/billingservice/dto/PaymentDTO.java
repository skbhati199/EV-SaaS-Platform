package com.ev.billingservice.dto;

import com.ev.billingservice.model.Payment.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    
    private UUID id;
    
    @NotNull(message = "Invoice ID is required")
    private UUID invoiceId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String transactionId;
    
    @NotNull(message = "Status is required")
    private PaymentStatus status;
    
    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;
} 