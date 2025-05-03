package com.ev.userservice.service.impl;

import com.ev.userservice.dto.WalletDto;
import com.ev.userservice.dto.WalletTransactionDto;
import com.ev.userservice.dto.WalletTransactionRequest;
import com.ev.userservice.model.User;
import com.ev.userservice.model.Wallet;
import com.ev.userservice.model.WalletTransaction;
import com.ev.userservice.repository.UserRepository;
import com.ev.userservice.repository.WalletRepository;
import com.ev.userservice.repository.WalletTransactionRepository;
import com.ev.userservice.service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public WalletDto getWalletByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user with id: " + userId));
        
        return mapToDto(wallet);
    }

    @Override
    public List<WalletTransactionDto> getTransactionsByWalletId(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
        
        return transactionRepository.findByWallet(wallet)
                .stream()
                .map(this::mapToTransactionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WalletTransactionDto createTransaction(UUID walletId, WalletTransactionRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
        
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .referenceId(request.getReferenceId())
                .description(request.getDescription())
                .transactionStatus(WalletTransaction.TransactionStatus.PENDING)
                .build();
        
        // Process transaction based on type
        switch (request.getTransactionType()) {
            case DEPOSIT:
                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                transaction.setTransactionStatus(WalletTransaction.TransactionStatus.COMPLETED);
                break;
            case WITHDRAWAL:
            case CHARGING_PAYMENT:
                if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                    transaction.setTransactionStatus(WalletTransaction.TransactionStatus.FAILED);
                    transactionRepository.save(transaction);
                    throw new IllegalStateException("Insufficient funds for transaction");
                }
                wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                transaction.setTransactionStatus(WalletTransaction.TransactionStatus.COMPLETED);
                break;
            case REFUND:
                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                transaction.setTransactionStatus(WalletTransaction.TransactionStatus.COMPLETED);
                break;
        }
        
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        
        return mapToTransactionDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public WalletDto addFunds(UUID walletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
        
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        
        // Create transaction record
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType(WalletTransaction.TransactionType.DEPOSIT)
                .description("Funds added to wallet")
                .transactionStatus(WalletTransaction.TransactionStatus.COMPLETED)
                .build();
        
        transactionRepository.save(transaction);
        
        return mapToDto(walletRepository.save(wallet));
    }

    @Override
    @Transactional
    public WalletDto withdrawFunds(UUID walletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        
        // Create transaction record
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType(WalletTransaction.TransactionType.WITHDRAWAL)
                .description("Funds withdrawn from wallet")
                .transactionStatus(WalletTransaction.TransactionStatus.COMPLETED)
                .build();
        
        transactionRepository.save(transaction);
        
        return mapToDto(walletRepository.save(wallet));
    }

    @Override
    public boolean hasSufficientFunds(UUID walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
        
        return wallet.getBalance().compareTo(amount) >= 0;
    }
    
    private WalletDto mapToDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .userId(wallet.getUser().getId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }
    
    private WalletTransactionDto mapToTransactionDto(WalletTransaction transaction) {
        return WalletTransactionDto.builder()
                .id(transaction.getId())
                .walletId(transaction.getWallet().getId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .referenceId(transaction.getReferenceId())
                .description(transaction.getDescription())
                .transactionStatus(transaction.getTransactionStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
} 