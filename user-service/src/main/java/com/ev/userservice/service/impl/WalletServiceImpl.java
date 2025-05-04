package com.ev.userservice.service.impl;

import com.ev.userservice.dto.WalletDto;
import com.ev.userservice.dto.WalletTransactionDto;
import com.ev.userservice.dto.WalletTransactionRequest;
import com.ev.userservice.dto.event.WalletEvent;
import com.ev.userservice.model.User;
import com.ev.userservice.model.Wallet;
import com.ev.userservice.model.WalletTransaction;
import com.ev.userservice.repository.UserRepository;
import com.ev.userservice.repository.WalletRepository;
import com.ev.userservice.repository.WalletTransactionRepository;
import com.ev.userservice.service.KafkaProducerService;
import com.ev.userservice.service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

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
        WalletEvent.WalletEventType eventType = null;
        boolean transactionSuccessful = false;
        
        switch (request.getTransactionType()) {
            case DEPOSIT:
                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                transaction.setTransactionStatus(WalletTransaction.TransactionStatus.COMPLETED);
                eventType = WalletEvent.WalletEventType.TOPPED_UP;
                transactionSuccessful = true;
                break;
            case WITHDRAWAL:
                if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                    transaction.setTransactionStatus(WalletTransaction.TransactionStatus.FAILED);
                    transactionRepository.save(transaction);
                    
                    // Send payment failed event
                    publishWalletEvent(wallet, transaction, 
                            WalletEvent.WalletEventType.PAYMENT_FAILED, 
                            "Insufficient funds for withdrawal");
                    
                    throw new IllegalStateException("Insufficient funds for transaction");
                }
                wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                transaction.setTransactionStatus(WalletTransaction.TransactionStatus.COMPLETED);
                eventType = WalletEvent.WalletEventType.DEBITED;
                transactionSuccessful = true;
                break;
            case CHARGING_PAYMENT:
                if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                    transaction.setTransactionStatus(WalletTransaction.TransactionStatus.FAILED);
                    transactionRepository.save(transaction);
                    
                    // Send payment failed event
                    publishWalletEvent(wallet, transaction, 
                            WalletEvent.WalletEventType.PAYMENT_FAILED, 
                            "Insufficient funds for charging payment");
                    
                    throw new IllegalStateException("Insufficient funds for transaction");
                }
                wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                transaction.setTransactionStatus(WalletTransaction.TransactionStatus.COMPLETED);
                eventType = WalletEvent.WalletEventType.PAYMENT_COMPLETED;
                transactionSuccessful = true;
                break;
            case REFUND:
                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                transaction.setTransactionStatus(WalletTransaction.TransactionStatus.COMPLETED);
                eventType = WalletEvent.WalletEventType.REFUND_COMPLETED;
                transactionSuccessful = true;
                break;
        }
        
        wallet.setUpdatedAt(LocalDateTime.now());
        Wallet updatedWallet = walletRepository.save(wallet);
        WalletTransaction savedTransaction = transactionRepository.save(transaction);
        
        // Publish event if transaction was successful
        if (transactionSuccessful && eventType != null) {
            publishWalletEvent(updatedWallet, savedTransaction, eventType, 
                    savedTransaction.getDescription());
        }
        
        return mapToTransactionDto(savedTransaction);
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
        
        WalletTransaction savedTransaction = transactionRepository.save(transaction);
        Wallet updatedWallet = walletRepository.save(wallet);
        
        // Publish wallet topped up event
        publishWalletEvent(updatedWallet, savedTransaction, 
                WalletEvent.WalletEventType.TOPPED_UP, "Funds added to wallet");
        
        return mapToDto(updatedWallet);
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
            // Publish wallet payment failed event
            publishWalletEvent(wallet, null, 
                    WalletEvent.WalletEventType.PAYMENT_FAILED, 
                    "Insufficient funds for withdrawal");
            
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
        
        WalletTransaction savedTransaction = transactionRepository.save(transaction);
        Wallet updatedWallet = walletRepository.save(wallet);
        
        // Publish wallet debited event
        publishWalletEvent(updatedWallet, savedTransaction, 
                WalletEvent.WalletEventType.DEBITED, "Funds withdrawn from wallet");
        
        return mapToDto(updatedWallet);
    }

    @Override
    public boolean hasSufficientFunds(UUID walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + walletId));
        
        return wallet.getBalance().compareTo(amount) >= 0;
    }
    
    /**
     * Publish a wallet event to Kafka
     */
    private void publishWalletEvent(Wallet wallet, WalletTransaction transaction, 
                                    WalletEvent.WalletEventType eventType, String description) {
        try {
            WalletEvent event = WalletEvent.builder()
                    .eventId(UUID.randomUUID())
                    .userId(wallet.getUser().getId())
                    .walletId(wallet.getId())
                    .eventType(eventType)
                    .timestamp(LocalDateTime.now())
                    .amount(transaction != null ? transaction.getAmount() : null)
                    .newBalance(wallet.getBalance())
                    .transactionId(transaction != null ? transaction.getId() : null)
                    .description(description)
                    .build();
            
            kafkaProducerService.sendWalletEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish wallet event: {}", eventType, e);
            // Don't fail the transaction if event publishing fails
        }
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