package com.ev.notificationservice.controller;

import com.ev.notificationservice.dto.NotificationDTO;
import com.ev.notificationservice.service.EventNotificationService;
import com.ev.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;
    private final EventNotificationService eventNotificationService;
    
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@RequestHeader("X-User-ID") UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@RequestHeader("X-User-ID") UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }
    
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }
    
    @PostMapping("/email")
    public ResponseEntity<UUID> sendEmailNotification(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam String type) {
        UUID id = eventNotificationService.sendEmailNotification(userId, email, subject, content, type);
        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/sms")
    public ResponseEntity<UUID> sendSmsNotification(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam String phoneNumber,
            @RequestParam String message,
            @RequestParam String type) {
        UUID id = eventNotificationService.sendSmsNotification(userId, phoneNumber, message, type);
        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/push")
    public ResponseEntity<UUID> sendPushNotification(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam String deviceToken,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type) {
        UUID id = eventNotificationService.sendPushNotification(userId, deviceToken, title, message, type);
        return new ResponseEntity<>(id, HttpStatus.ACCEPTED);
    }
} 