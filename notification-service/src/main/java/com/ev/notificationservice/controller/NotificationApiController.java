package com.ev.notificationservice.controller;

import com.ev.notificationservice.dto.NotificationEvent;
import com.ev.notificationservice.model.Notification;
import com.ev.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(@RequestHeader("X-User-ID") UUID userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@RequestHeader("X-User-ID") UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }
    
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID notificationId, @RequestHeader("X-User-ID") UUID userId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/email")
    public ResponseEntity<Void> sendEmailNotification(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam String type) {
        notificationService.sendEmailNotification(userId, email, subject, content, type);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/sms")
    public ResponseEntity<Void> sendSmsNotification(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam String phoneNumber,
            @RequestParam String message,
            @RequestParam String type) {
        notificationService.sendSmsNotification(userId, phoneNumber, message, type);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    @PostMapping("/push")
    public ResponseEntity<Void> sendPushNotification(
            @RequestHeader("X-User-ID") UUID userId,
            @RequestParam String deviceToken,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type) {
        notificationService.sendPushNotification(userId, deviceToken, title, message, type);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
} 