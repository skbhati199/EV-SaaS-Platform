package com.ev.billingservice.service.impl;

import com.ev.billingservice.dto.PaymentMethodDTO;
import com.ev.billingservice.exception.ResourceNotFoundException;
import com.ev.billingservice.model.PaymentMethod;
import com.ev.billingservice.model.PaymentMethod.PaymentType;
import com.ev.billingservice.repository.PaymentMethodRepository;
import com.ev.billingservice.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    @Transactional
    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod paymentMethod = mapToEntity(paymentMethodDTO);
        
        // If this payment method is set as default, update all others to non-default
        if (paymentMethod.isDefault()) {
            paymentMethodRepository.updateOtherPaymentMethodsToNonDefault(
                    paymentMethod.getUserId(), paymentMethod.getId());
        }
        
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        log.info("Created payment method with ID: {}", savedPaymentMethod.getId());
        
        return mapToDTO(savedPaymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodDTO getPaymentMethodById(UUID id) {
        PaymentMethod paymentMethod = findPaymentMethodById(id);
        return mapToDTO(paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getPaymentMethodsByUserId(UUID userId) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(userId);
        return paymentMethods.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodDTO getDefaultPaymentMethodForUser(UUID userId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Default payment method not found for user ID: " + userId));
        return mapToDTO(paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDTO> getPaymentMethodsByUserIdAndType(UUID userId, PaymentType type) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserIdAndType(userId, type);
        return paymentMethods.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentMethodDTO updatePaymentMethod(UUID id, PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod existingPaymentMethod = findPaymentMethodById(id);
        
        // Only allow updating certain fields
        existingPaymentMethod.setType(paymentMethodDTO.getType());
        existingPaymentMethod.setProvider(paymentMethodDTO.getProvider());
        existingPaymentMethod.setTokenId(paymentMethodDTO.getTokenId());
        existingPaymentMethod.setLastFour(paymentMethodDTO.getLastFour());
        existingPaymentMethod.setExpiryMonth(paymentMethodDTO.getExpiryMonth());
        existingPaymentMethod.setExpiryYear(paymentMethodDTO.getExpiryYear());
        
        // Handle default payment method
        if (paymentMethodDTO.isDefault() && !existingPaymentMethod.isDefault()) {
            existingPaymentMethod.setDefault(true);
            paymentMethodRepository.updateOtherPaymentMethodsToNonDefault(
                    existingPaymentMethod.getUserId(), existingPaymentMethod.getId());
        }
        
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(existingPaymentMethod);
        log.info("Updated payment method with ID: {}", updatedPaymentMethod.getId());
        
        return mapToDTO(updatedPaymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethodDTO setDefaultPaymentMethod(UUID id) {
        PaymentMethod paymentMethod = findPaymentMethodById(id);
        
        // Set this payment method as default and others as non-default
        paymentMethod.setDefault(true);
        paymentMethodRepository.updateOtherPaymentMethodsToNonDefault(
                paymentMethod.getUserId(), paymentMethod.getId());
        
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        log.info("Set payment method with ID: {} as default", updatedPaymentMethod.getId());
        
        return mapToDTO(updatedPaymentMethod);
    }

    @Override
    @Transactional
    public void deletePaymentMethod(UUID id) {
        PaymentMethod paymentMethod = findPaymentMethodById(id);
        paymentMethodRepository.delete(paymentMethod);
        log.info("Deleted payment method with ID: {}", id);
    }
    
    private PaymentMethod findPaymentMethodById(UUID id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + id));
    }
    
    private PaymentMethod mapToEntity(PaymentMethodDTO dto) {
        return PaymentMethod.builder()
                .userId(dto.getUserId())
                .type(dto.getType())
                .provider(dto.getProvider())
                .tokenId(dto.getTokenId())
                .lastFour(dto.getLastFour())
                .expiryMonth(dto.getExpiryMonth())
                .expiryYear(dto.getExpiryYear())
                .isDefault(dto.isDefault())
                .build();
    }
    
    private PaymentMethodDTO mapToDTO(PaymentMethod entity) {
        return PaymentMethodDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .provider(entity.getProvider())
                .tokenId(entity.getTokenId())
                .lastFour(entity.getLastFour())
                .expiryMonth(entity.getExpiryMonth())
                .expiryYear(entity.getExpiryYear())
                .isDefault(entity.isDefault())
                .build();
    }
} 