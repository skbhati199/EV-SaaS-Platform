package com.ev.userservice.service.impl;

import com.ev.userservice.dto.ChargingHistoryDto;
import com.ev.userservice.dto.RecordChargingSessionRequest;
import com.ev.userservice.model.ChargingHistory;
import com.ev.userservice.model.User;
import com.ev.userservice.repository.ChargingHistoryRepository;
import com.ev.userservice.repository.UserRepository;
import com.ev.userservice.service.ChargingHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargingHistoryServiceImpl implements ChargingHistoryService {

    private final ChargingHistoryRepository chargingHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<ChargingHistoryDto> getChargingHistoryByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        return chargingHistoryRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChargingHistoryDto getChargingHistoryById(UUID id) {
        return chargingHistoryRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Charging history not found with id: " + id));
    }

    @Override
    public ChargingHistoryDto getChargingHistoryBySessionId(UUID sessionId) {
        return chargingHistoryRepository.findBySessionId(sessionId)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("Charging history not found for session with id: " + sessionId));
    }

    @Override
    @Transactional
    public ChargingHistoryDto recordChargingSession(UUID userId, RecordChargingSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        // Check if a record with this session ID already exists
        if (chargingHistoryRepository.findBySessionId(request.getSessionId()).isPresent()) {
            throw new IllegalArgumentException("Charging session with id " + request.getSessionId() + " already exists");
        }
        
        ChargingHistory history = ChargingHistory.builder()
                .user(user)
                .sessionId(request.getSessionId())
                .stationId(request.getStationId())
                .connectorId(request.getConnectorId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .energyConsumedKwh(request.getEnergyConsumedKwh())
                .cost(request.getCost())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .paymentStatus(ChargingHistory.PaymentStatus.PENDING)
                .build();
        
        return mapToDto(chargingHistoryRepository.save(history));
    }

    @Override
    @Transactional
    public ChargingHistoryDto updatePaymentStatus(UUID id, ChargingHistory.PaymentStatus status) {
        ChargingHistory history = chargingHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging history not found with id: " + id));
        
        history.setPaymentStatus(status);
        history.setUpdatedAt(LocalDateTime.now());
        
        return mapToDto(chargingHistoryRepository.save(history));
    }

    @Override
    public List<ChargingHistoryDto> getChargingHistoryByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        return chargingHistoryRepository.findByUserAndStartTimeBetween(user, startDate, endDate)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    private ChargingHistoryDto mapToDto(ChargingHistory history) {
        return ChargingHistoryDto.builder()
                .id(history.getId())
                .userId(history.getUser().getId())
                .sessionId(history.getSessionId())
                .stationId(history.getStationId())
                .connectorId(history.getConnectorId())
                .startTime(history.getStartTime())
                .endTime(history.getEndTime())
                .energyConsumedKwh(history.getEnergyConsumedKwh())
                .cost(history.getCost())
                .currency(history.getCurrency())
                .paymentStatus(history.getPaymentStatus())
                .build();
    }
} 