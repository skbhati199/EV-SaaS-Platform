package com.ev.station.service.impl;

import com.ev.station.dto.event.PowerDistributionEvent;
import com.ev.station.dto.notification.PowerControlNotification;
import com.ev.station.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of NotificationService for sending WebSocket notifications to clients.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    
    // WebSocket destination for power control notifications
    private static final String POWER_CONTROL_DESTINATION = "/topic/power-control";
    
    @Override
    public void sendPowerControlNotification(PowerControlNotification notification) {
        if (notification.getTimestamp() == null) {
            notification.setTimestamp(LocalDateTime.now());
        }
        
        log.debug("Sending power control notification: {}", notification);
        messagingTemplate.convertAndSend(POWER_CONTROL_DESTINATION, notification);
    }
    
    @Override
    public void notifyPowerLimitSet(
            UUID stationId,
            String stationName,
            Integer connectorId,
            Double powerLimitKW,
            PowerDistributionEvent.PowerAdjustmentReason reason,
            boolean temporary,
            Integer durationSeconds,
            boolean success) {
        
        LocalDateTime expiryTime = null;
        if (temporary && durationSeconds != null) {
            expiryTime = LocalDateTime.now().plusSeconds(durationSeconds);
        }
        
        PowerControlNotification notification = PowerControlNotification.builder()
                .type(success ? 
                        PowerControlNotification.NotificationType.POWER_LIMIT_SET : 
                        PowerControlNotification.NotificationType.POWER_LIMIT_FAILED)
                .stationId(stationId)
                .stationName(stationName)
                .connectorId(connectorId)
                .powerLimitKW(powerLimitKW)
                .temporary(temporary)
                .expiryTime(expiryTime)
                .reason(reason)
                .success(success)
                .timestamp(LocalDateTime.now())
                .build();
        
        sendPowerControlNotification(notification);
    }
    
    @Override
    public void notifyPowerLimitCleared(
            UUID stationId, 
            String stationName, 
            Integer connectorId, 
            boolean success) {
        
        PowerControlNotification notification = PowerControlNotification.builder()
                .type(success ? 
                        PowerControlNotification.NotificationType.POWER_LIMIT_CLEARED : 
                        PowerControlNotification.NotificationType.POWER_LIMIT_FAILED)
                .stationId(stationId)
                .stationName(stationName)
                .connectorId(connectorId)
                .powerLimitKW(null) // No power limit when cleared
                .success(success)
                .timestamp(LocalDateTime.now())
                .build();
        
        sendPowerControlNotification(notification);
    }
    
    @Override
    public void notifyPowerLimitExpired(
            UUID stationId, 
            String stationName, 
            Integer connectorId) {
        
        PowerControlNotification notification = PowerControlNotification.builder()
                .type(PowerControlNotification.NotificationType.POWER_LIMIT_EXPIRED)
                .stationId(stationId)
                .stationName(stationName)
                .connectorId(connectorId)
                .powerLimitKW(null) // No power limit when expired
                .success(true) // Expiration is always considered successful
                .timestamp(LocalDateTime.now())
                .build();
        
        sendPowerControlNotification(notification);
    }
} 