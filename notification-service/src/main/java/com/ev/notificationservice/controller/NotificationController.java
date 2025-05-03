package com.ev.notificationservice.controller;

import com.ev.notificationservice.dto.NotificationDTO;
import com.ev.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }
    
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }
    
    @GetMapping("/user/{userId}/unsent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getUnsentNotificationsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUnsentNotificationsByUserId(userId));
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByRelatedEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return ResponseEntity.ok(notificationService.getNotificationsByRelatedEntity(entityId, entityType));
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(notificationService.getNotificationsByDateRange(start, end));
    }
    
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByType(@PathVariable String type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }
    
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserIdAndType(
            @PathVariable UUID userId,
            @PathVariable String type) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserIdAndType(userId, type));
    }
    
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
    
    @PutMapping("/{id}/mark-sent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationDTO> markNotificationAsSent(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsSent(id, null));
    }
    
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendAllPendingNotifications() {
        notificationService.sendNotifications();
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
} 