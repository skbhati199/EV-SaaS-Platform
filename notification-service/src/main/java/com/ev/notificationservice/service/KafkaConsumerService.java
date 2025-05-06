package com.ev.notificationservice.service;

import com.ev.notificationservice.config.KafkaConfig;
import com.ev.notificationservice.dto.NotificationEvent;
import com.ev.notificationservice.dto.event.InvoiceEvent;
import com.ev.notificationservice.dto.event.PaymentEvent;
import com.ev.notificationservice.dto.event.UserEvent;
import com.ev.notificationservice.model.Notification;
import com.ev.notificationservice.model.NotificationType;
import com.ev.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushNotificationService;
    private final UserService userService;

    /**
     * Consume email notification events
     */
    @KafkaListener(topics = "#{kafkaConfig.EMAIL_NOTIFICATIONS_TOPIC}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeEmailNotification(NotificationEvent event, Acknowledgment acknowledgment) {
        log.info("Received email notification event: {}", event);
        
        try {
            String to = event.getRecipient();
            String subject = event.getSubject();
            String content = event.getContent();
            String templateId = event.getTemplateId();
            var templateData = event.getTemplateData();
            
            Notification notification = createNotificationFromEvent(event);
            notification.setSent(false);
            
            notification = notificationRepository.save(notification);
            
            boolean success = emailService.sendEmail(to, subject, content, templateId, templateData);
            
            if (success) {
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                
                log.info("Email notification sent successfully: {}", subject);
            } else {
                log.error("Failed to send email notification");
            }
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing email notification", e);
            throw e;
        }
    }
    
    /**
     * Consume SMS notification events
     */
    @KafkaListener(topics = "#{kafkaConfig.SMS_NOTIFICATIONS_TOPIC}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeSmsNotification(NotificationEvent event, Acknowledgment acknowledgment) {
        log.info("Received SMS notification event");
        
        try {
            String to = event.getRecipient();
            String content = event.getContent();
            
            Notification notification = createNotificationFromEvent(event);
            notification.setSent(false);
            
            notification = notificationRepository.save(notification);
            
            boolean success = smsService.sendSms(to, content);
            
            if (success) {
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                
                log.info("SMS notification sent successfully");
            } else {
                log.error("Failed to send SMS notification");
            }
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing SMS notification", e);
            throw e;
        }
    }
    
    /**
     * Consume push notification events
     */
    @KafkaListener(topics = "#{kafkaConfig.PUSH_NOTIFICATIONS_TOPIC}", groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumePushNotification(NotificationEvent event, Acknowledgment acknowledgment) {
        log.info("Received push notification event");
        
        try {
            String to = event.getRecipient();
            String subject = event.getSubject();
            String content = event.getContent();
            
            Notification notification = createNotificationFromEvent(event);
            notification.setSent(false);
            
            notification = notificationRepository.save(notification);
            
            boolean success = pushNotificationService.sendPushNotification(to, subject, content);
            
            if (success) {
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                
                log.info("Push notification sent successfully: {}", subject);
            } else {
                log.error("Failed to send push notification");
            }
            
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing push notification", e);
            throw e;
        }
    }
    
    /**
     * Consume payment events from billing service
     */
    @KafkaListener(
        topics = KafkaConfig.PAYMENT_EVENTS_TOPIC,
        groupId = KafkaConfig.NOTIFICATION_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumePaymentEvent(PaymentEvent event, Acknowledgment acknowledgment) {
        log.info("Received payment event: type={}, paymentId={}", event.getEventType(), event.getPaymentId());
        
        try {
            switch (event.getEventType()) {
                case "COMPLETED":
                    handlePaymentCompletedEvent(event);
                    break;
                case "FAILED":
                    handlePaymentFailedEvent(event);
                    break;
                case "REFUNDED":
                    handlePaymentRefundedEvent(event);
                    break;
            }
            
            acknowledgment.acknowledge();
            log.debug("Acknowledged payment event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing payment event: {}", event.getEventId(), e);
            throw e;
        }
    }
    
    /**
     * Consume invoice events from billing service
     */
    @KafkaListener(
        topics = KafkaConfig.INVOICE_EVENTS_TOPIC,
        groupId = KafkaConfig.NOTIFICATION_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeInvoiceEvent(InvoiceEvent event, Acknowledgment acknowledgment) {
        log.info("Received invoice event: type={}, invoiceId={}", event.getEventType(), event.getInvoiceId());
        
        try {
            switch (event.getEventType()) {
                case "CREATED":
                    handleInvoiceCreatedEvent(event);
                    break;
                case "PAID":
                    handleInvoicePaidEvent(event);
                    break;
                case "OVERDUE":
                    handleInvoiceOverdueEvent(event);
                    break;
            }
            
            acknowledgment.acknowledge();
            log.debug("Acknowledged invoice event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing invoice event: {}", event.getEventId(), e);
            throw e;
        }
    }
    
    /**
     * Consume user events from user service
     */
    @KafkaListener(
        topics = KafkaConfig.USER_EVENTS_TOPIC,
        groupId = KafkaConfig.NOTIFICATION_CONSUMER_GROUP,
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeUserEvent(UserEvent event, Acknowledgment acknowledgment) {
        log.info("Received user event: type={}, userId={}", event.getEventType(), event.getUserId());
        
        try {
            switch (event.getEventType()) {
                case CREATED:
                    handleUserCreatedEvent(event);
                    break;
                case PROFILE_UPDATED:
                    handleUserProfileUpdatedEvent(event);
                    break;
                case ACCOUNT_DISABLED:
                    handleUserAccountDisabledEvent(event);
                    break;
                case ACCOUNT_ENABLED:
                    handleUserAccountEnabledEvent(event);
                    break;
            }
            
            acknowledgment.acknowledge();
            log.debug("Acknowledged user event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing user event: {}", event.getEventId(), e);
            throw e;
        }
    }
    
    /**
     * Handle payment completed event
     */
    private void handlePaymentCompletedEvent(PaymentEvent event) {
        log.info("Processing payment completed event for payment: {}", event.getPaymentId());
        
        // Get user details
        UserEvent user = userService.getUserById(event.getUserId());
        if (user == null) {
            log.error("User not found for payment event: {}", event.getPaymentId());
            return;
        }
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", user.getFirstName());
        templateData.put("lastName", user.getLastName());
        templateData.put("paymentId", event.getPaymentId());
        templateData.put("amount", formatAmount(event.getAmount(), event.getCurrency()));
        templateData.put("paymentMethod", event.getPaymentMethod());
        templateData.put("paymentDate", event.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.PAYMENT_RECEIVED)
                .channel("email")
                .recipient(user.getEmail())
                .subject("Payment Received")
                .templateId("payment-received")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                user.getEmail(), 
                "Payment Received", 
                null,
                "payment-received", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Payment received email sent to user: {}", user.getEmail());
        } else {
            log.error("Failed to send payment received email to user: {}", user.getEmail());
        }
    }
    
    /**
     * Handle payment failed event
     */
    private void handlePaymentFailedEvent(PaymentEvent event) {
        log.info("Processing payment failed event for payment: {}", event.getPaymentId());
        
        // Get user details
        UserEvent user = userService.getUserById(event.getUserId());
        if (user == null) {
            log.error("User not found for payment event: {}", event.getPaymentId());
            return;
        }
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", user.getFirstName());
        templateData.put("lastName", user.getLastName());
        templateData.put("paymentId", event.getPaymentId());
        templateData.put("amount", formatAmount(event.getAmount(), event.getCurrency()));
        templateData.put("failureReason", event.getErrorMessage());
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.PAYMENT_FAILED)
                .channel("email")
                .recipient(user.getEmail())
                .subject("Payment Failed")
                .templateId("payment-failed")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                user.getEmail(), 
                "Payment Failed", 
                null,
                "payment-failed", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Payment failed email sent to user: {}", user.getEmail());
        } else {
            log.error("Failed to send payment failed email to user: {}", user.getEmail());
        }
    }
    
    /**
     * Handle payment refunded event
     */
    private void handlePaymentRefundedEvent(PaymentEvent event) {
        log.info("Processing payment refunded event for payment: {}", event.getPaymentId());
        
        // Get user details
        UserEvent user = userService.getUserById(event.getUserId());
        if (user == null) {
            log.error("User not found for payment event: {}", event.getPaymentId());
            return;
        }
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", user.getFirstName());
        templateData.put("lastName", user.getLastName());
        templateData.put("paymentId", event.getPaymentId());
        templateData.put("amount", formatAmount(event.getAmount(), event.getCurrency()));
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.PAYMENT_REFUNDED)
                .channel("email")
                .recipient(user.getEmail())
                .subject("Payment Refunded")
                .templateId("payment-refunded")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                user.getEmail(), 
                "Payment Refunded", 
                null,
                "payment-refunded", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Payment refunded email sent to user: {}", user.getEmail());
        } else {
            log.error("Failed to send payment refunded email to user: {}", user.getEmail());
        }
    }
    
    /**
     * Handle invoice created event
     */
    private void handleInvoiceCreatedEvent(InvoiceEvent event) {
        log.info("Processing invoice created event for invoice: {}", event.getInvoiceId());
        
        // Get user details
        UserEvent user = userService.getUserById(event.getUserId());
        if (user == null) {
            log.error("User not found for invoice event: {}", event.getInvoiceId());
            return;
        }
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", user.getFirstName());
        templateData.put("lastName", user.getLastName());
        templateData.put("invoiceId", event.getInvoiceId());
        templateData.put("invoiceNumber", event.getInvoiceNumber());
        templateData.put("amount", formatAmount(event.getTotalAmount(), event.getCurrency()));
        templateData.put("dueDate", event.getDueAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.INVOICE_CREATED)
                .channel("email")
                .recipient(user.getEmail())
                .subject("New Invoice Created: #" + event.getInvoiceNumber())
                .templateId("invoice-created")
                .templateData(templateData)
                .priority(NotificationType.Priority.MEDIUM)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                user.getEmail(), 
                "New Invoice Created: #" + event.getInvoiceNumber(), 
                null,
                "invoice-created", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Invoice created email sent to user: {}", user.getEmail());
        } else {
            log.error("Failed to send invoice created email to user: {}", user.getEmail());
        }
    }
    
    /**
     * Handle invoice paid event
     */
    private void handleInvoicePaidEvent(InvoiceEvent event) {
        log.info("Processing invoice paid event for invoice: {}", event.getInvoiceId());
        
        // Get user details
        UserEvent user = userService.getUserById(event.getUserId());
        if (user == null) {
            log.error("User not found for invoice event: {}", event.getInvoiceId());
            return;
        }
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", user.getFirstName());
        templateData.put("lastName", user.getLastName());
        templateData.put("invoiceId", event.getInvoiceId());
        templateData.put("invoiceNumber", event.getInvoiceNumber());
        templateData.put("amount", formatAmount(event.getTotalAmount(), event.getCurrency()));
        templateData.put("paymentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.INVOICE_PAID)
                .channel("email")
                .recipient(user.getEmail())
                .subject("Invoice Paid: #" + event.getInvoiceNumber())
                .templateId("invoice-paid")
                .templateData(templateData)
                .priority(NotificationType.Priority.MEDIUM)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                user.getEmail(), 
                "Invoice Paid: #" + event.getInvoiceNumber(), 
                null,
                "invoice-paid", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Invoice paid email sent to user: {}", user.getEmail());
        } else {
            log.error("Failed to send invoice paid email to user: {}", user.getEmail());
        }
    }
    
    /**
     * Handle invoice overdue event
     */
    private void handleInvoiceOverdueEvent(InvoiceEvent event) {
        log.info("Processing invoice overdue event for invoice: {}", event.getInvoiceId());
        
        // Get user details
        UserEvent user = userService.getUserById(event.getUserId());
        if (user == null) {
            log.error("User not found for invoice event: {}", event.getInvoiceId());
            return;
        }
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", user.getFirstName());
        templateData.put("lastName", user.getLastName());
        templateData.put("invoiceId", event.getInvoiceId());
        templateData.put("invoiceNumber", event.getInvoiceNumber());
        templateData.put("amount", formatAmount(event.getTotalAmount(), event.getCurrency()));
        templateData.put("dueDate", event.getDueAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        templateData.put("daysOverdue", calculateDaysOverdue(event.getDueAt()));
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.INVOICE_OVERDUE)
                .channel("email")
                .recipient(user.getEmail())
                .subject("Overdue Invoice: #" + event.getInvoiceNumber())
                .templateId("invoice-overdue")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                user.getEmail(), 
                "Overdue Invoice: #" + event.getInvoiceNumber(), 
                null,
                "invoice-overdue", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Invoice overdue email sent to user: {}", user.getEmail());
        } else {
            log.error("Failed to send invoice overdue email to user: {}", user.getEmail());
        }
    }
    
    /**
     * Handle user created event
     */
    private void handleUserCreatedEvent(UserEvent event) {
        log.info("Processing user created event for user: {}", event.getUserId());
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", event.getFirstName());
        templateData.put("lastName", event.getLastName());
        templateData.put("email", event.getEmail());
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.USER_CREATED)
                .channel("email")
                .recipient(event.getEmail())
                .subject("Welcome to EV SaaS Platform")
                .templateId("welcome-email")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                event.getEmail(), 
                "Welcome to EV SaaS Platform", 
                null, 
                "welcome-email", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Welcome email sent to new user: {}", event.getEmail());
        } else {
            log.error("Failed to send welcome email to user: {}", event.getEmail());
        }
    }
    
    /**
     * Handle user profile updated event
     */
    private void handleUserProfileUpdatedEvent(UserEvent event) {
        log.info("Processing user profile updated event for user: {}", event.getUserId());
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", event.getFirstName());
        templateData.put("lastName", event.getLastName());
        templateData.put("email", event.getEmail());
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.PROFILE_UPDATED)
                .channel("email")
                .recipient(event.getEmail())
                .subject("Your Profile Has Been Updated")
                .templateId("profile-updated")
                .templateData(templateData)
                .priority(NotificationType.Priority.MEDIUM)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                event.getEmail(), 
                "Your Profile Has Been Updated", 
                null, 
                "profile-updated", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Profile updated email sent to user: {}", event.getEmail());
        } else {
            log.error("Failed to send profile updated email to user: {}", event.getEmail());
        }
    }
    
    /**
     * Handle user account disabled event
     */
    private void handleUserAccountDisabledEvent(UserEvent event) {
        log.info("Processing user account disabled event for user: {}", event.getUserId());
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", event.getFirstName());
        templateData.put("lastName", event.getLastName());
        templateData.put("email", event.getEmail());
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.ACCOUNT_DISABLED)
                .channel("email")
                .recipient(event.getEmail())
                .subject("Your Account Has Been Disabled")
                .templateId("account-disabled")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                event.getEmail(), 
                "Your Account Has Been Disabled", 
                null, 
                "account-disabled", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Account disabled email sent to user: {}", event.getEmail());
        } else {
            log.error("Failed to send account disabled email to user: {}", event.getEmail());
        }
    }
    
    /**
     * Handle user account enabled event
     */
    private void handleUserAccountEnabledEvent(UserEvent event) {
        log.info("Processing user account enabled event for user: {}", event.getUserId());
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("firstName", event.getFirstName());
        templateData.put("lastName", event.getLastName());
        templateData.put("email", event.getEmail());
        
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .userId(event.getUserId())
                .type(NotificationType.ACCOUNT_ENABLED)
                .channel("email")
                .recipient(event.getEmail())
                .subject("Your Account Has Been Enabled")
                .templateId("account-enabled")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                event.getEmail(), 
                "Your Account Has Been Enabled", 
                null, 
                "account-enabled", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Account enabled email sent to user: {}", event.getEmail());
        } else {
            log.error("Failed to send account enabled email to user: {}", event.getEmail());
        }
    }
    
    private Notification createNotificationFromEvent(NotificationEvent event) {
        return Notification.builder()
                .userId(event.getUserId())
                .type(event.getType())
                .subject(event.getSubject())
                .content(event.getContent())
                .channel(event.getChannel())
                .recipient(event.getRecipient())
                .sent(false)
                .read(false)
                .createdAt(LocalDateTime.now())
                .relatedEntityId(event.getRelatedEntityId())
                .relatedEntityType(event.getRelatedEntityType())
                .priority(event.getPriority())
                .build();
    }

    /**
     * Format amount with currency for display
     * @param amount The amount
     * @param currency The currency code
     * @return Formatted amount string
     */
    private String formatAmount(BigDecimal amount, String currency) {
        return String.format("%s %.2f", currency, amount.doubleValue());
    }
    
    /**
     * Calculate days overdue from due date
     * @param dueDate The due date
     * @return Days overdue
     */
    private long calculateDaysOverdue(LocalDateTime dueDate) {
        return LocalDateTime.now().toLocalDate().toEpochDay() - dueDate.toLocalDate().toEpochDay();
    }
} 