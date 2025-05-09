package com.ev.userservice.repository;

import com.ev.userservice.model.Wallet;
import com.ev.userservice.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
    List<WalletTransaction> findByWallet(Wallet wallet);
    Page<WalletTransaction> findByWallet(Wallet wallet, Pageable pageable);
    Optional<WalletTransaction> findByReferenceId(UUID referenceId);
} 