package com.ev.userservice.dto;

import com.ev.userservice.model.WalletTransaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionRequest {
    
    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction type is required")
    private WalletTransaction.TransactionType transactionType;
    
    private UUID referenceId;
    
    private String description;
} 