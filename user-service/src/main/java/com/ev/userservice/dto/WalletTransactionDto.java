package com.ev.userservice.dto;

import com.ev.userservice.model.WalletTransaction;
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
public class WalletTransactionDto {
    private UUID id;
    private UUID walletId;
    private BigDecimal amount;
    private WalletTransaction.TransactionType transactionType;
    private UUID referenceId;
    private String description;
    private WalletTransaction.TransactionStatus transactionStatus;
    private LocalDateTime createdAt;
} 