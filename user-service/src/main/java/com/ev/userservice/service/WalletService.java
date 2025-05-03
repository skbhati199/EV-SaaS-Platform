package com.ev.userservice.service;

import com.ev.userservice.dto.WalletDto;
import com.ev.userservice.dto.WalletTransactionDto;
import com.ev.userservice.dto.WalletTransactionRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {
    WalletDto getWalletByUserId(UUID userId);
    List<WalletTransactionDto> getTransactionsByWalletId(UUID walletId);
    WalletTransactionDto createTransaction(UUID walletId, WalletTransactionRequest request);
    WalletDto addFunds(UUID walletId, BigDecimal amount);
    WalletDto withdrawFunds(UUID walletId, BigDecimal amount);
    boolean hasSufficientFunds(UUID walletId, BigDecimal amount);
} 