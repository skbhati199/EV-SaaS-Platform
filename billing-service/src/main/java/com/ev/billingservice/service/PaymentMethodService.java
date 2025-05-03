package com.ev.billingservice.service;

import com.ev.billingservice.dto.PaymentMethodDTO;
import com.ev.billingservice.model.PaymentMethod.PaymentType;

import java.util.List;
import java.util.UUID;

public interface PaymentMethodService {
    
    PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO);
    
    PaymentMethodDTO getPaymentMethodById(UUID id);
    
    List<PaymentMethodDTO> getPaymentMethodsByUserId(UUID userId);
    
    PaymentMethodDTO getDefaultPaymentMethodForUser(UUID userId);
    
    List<PaymentMethodDTO> getPaymentMethodsByUserIdAndType(UUID userId, PaymentType type);
    
    PaymentMethodDTO updatePaymentMethod(UUID id, PaymentMethodDTO paymentMethodDTO);
    
    PaymentMethodDTO setDefaultPaymentMethod(UUID id);
    
    void deletePaymentMethod(UUID id);
} 