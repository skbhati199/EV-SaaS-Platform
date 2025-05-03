package com.ev.notificationservice.controller;

import com.ev.notificationservice.dto.NotificationDTO;
import com.ev.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
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
    
    @GetMapping("/related/{relatedEntityType}/{relatedEntityId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByRelatedEntity(
            @PathVariable String relatedEntityType,
            @PathVariable UUID relatedEntityId) {
        return ResponseEntity.ok(notificationService.getNotificationsByRelatedEntity(relatedEntityId, relatedEntityType));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByType(@PathVariable String type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }
    
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) {
        return new ResponseEntity<>(notificationService.createNotification(notificationDTO), HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
    
    @PatchMapping("/{id}/sent")
    public ResponseEntity<NotificationDTO> markAsSent(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsSent(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteNotificationsByUserId(@PathVariable UUID userId) {
        notificationService.deleteNotificationsByUserId(userId);
        return ResponseEntity.noContent().build();
    }
} 