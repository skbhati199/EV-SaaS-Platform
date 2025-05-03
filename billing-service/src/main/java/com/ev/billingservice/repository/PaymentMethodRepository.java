package com.ev.billingservice.repository;

import com.ev.billingservice.model.PaymentMethod;
import com.ev.billingservice.model.PaymentMethod.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    
    List<PaymentMethod> findByUserId(UUID userId);
    
    Optional<PaymentMethod> findByUserIdAndIsDefaultTrue(UUID userId);
    
    List<PaymentMethod> findByUserIdAndType(UUID userId, PaymentType type);
    
    @Modifying
    @Query("UPDATE PaymentMethod pm SET pm.isDefault = false WHERE pm.userId = ?1 AND pm.id <> ?2")
    int updateOtherPaymentMethodsToNonDefault(UUID userId, UUID paymentMethodId);
} 