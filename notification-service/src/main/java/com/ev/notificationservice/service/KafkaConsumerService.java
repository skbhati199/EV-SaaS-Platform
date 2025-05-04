package com.ev.notificationservice.service;

import com.ev.notificationservice.config.KafkaConfig;
import com.ev.notificationservice.dto.NotificationEvent;
import com.ev.notificationservice.dto.event.InvoiceEvent;
import com.ev.notificationservice.dto.event.PaymentEvent;
import com.ev.notificationservice.model.Notification;
import com.ev.notificationservice.model.NotificationType;
import com.ev.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        log.info("Handling payment completed event for payment: {}", event.getPaymentId());
        
        try {
            // Get user contact information
            var user = userService.getUserById(event.getUserId());
            String email = user.getEmail();
            
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getFirstName() + " " + user.getLastName());
            templateData.put("paymentAmount", event.getAmount().toString());
            templateData.put("paymentCurrency", event.getCurrency());
            templateData.put("paymentMethod", event.getPaymentMethod());
            templateData.put("paymentDate", event.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            templateData.put("invoiceNumber", "INV-" + event.getInvoiceId().toString().substring(0, 8).toUpperCase());
            
            // Send email notification
            NotificationEvent emailEvent = NotificationEvent.builder()
                .userId(event.getUserId())
                .type(NotificationType.PAYMENT_RECEIVED.name())
                .subject("Payment Confirmation")
                .recipient(email)
                .channel("EMAIL")
                .templateId("payment-confirmation-template")
                .templateData(templateData)
                .relatedEntityId(event.getPaymentId().toString())
                .relatedEntityType("PAYMENT")
                .build();
            
            emailService.sendEmail(
                email, 
                emailEvent.getSubject(), 
                null, 
                emailEvent.getTemplateId(), 
                emailEvent.getTemplateData()
            );
            
            // Save notification record
            Notification notification = createNotificationFromEvent(emailEvent);
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Payment confirmation email sent to: {}", email);
        } catch (Exception e) {
            log.error("Error sending payment confirmation notification", e);
        }
    }
    
    /**
     * Handle payment failed event
     */
    private void handlePaymentFailedEvent(PaymentEvent event) {
        log.info("Handling payment failed event for payment: {}", event.getPaymentId());
        
        try {
            // Get user contact information
            var user = userService.getUserById(event.getUserId());
            String email = user.getEmail();
            
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getFirstName() + " " + user.getLastName());
            templateData.put("paymentAmount", event.getAmount().toString());
            templateData.put("paymentCurrency", event.getCurrency());
            templateData.put("paymentMethod", event.getPaymentMethod());
            templateData.put("errorMessage", event.getErrorMessage());
            
            // Send email notification
            NotificationEvent emailEvent = NotificationEvent.builder()
                .userId(event.getUserId())
                .type(NotificationType.PAYMENT_FAILED.name())
                .subject("Payment Failed")
                .recipient(email)
                .channel("EMAIL")
                .templateId("payment-failed-template")
                .templateData(templateData)
                .relatedEntityId(event.getPaymentId().toString())
                .relatedEntityType("PAYMENT")
                .build();
            
            emailService.sendEmail(
                email, 
                emailEvent.getSubject(), 
                null, 
                emailEvent.getTemplateId(), 
                emailEvent.getTemplateData()
            );
            
            // Save notification record
            Notification notification = createNotificationFromEvent(emailEvent);
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Payment failed email sent to: {}", email);
        } catch (Exception e) {
            log.error("Error sending payment failed notification", e);
        }
    }
    
    /**
     * Handle payment refunded event
     */
    private void handlePaymentRefundedEvent(PaymentEvent event) {
        log.info("Handling payment refunded event for payment: {}", event.getPaymentId());
        
        try {
            // Get user contact information
            var user = userService.getUserById(event.getUserId());
            String email = user.getEmail();
            
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getFirstName() + " " + user.getLastName());
            templateData.put("refundAmount", event.getAmount().toString());
            templateData.put("refundCurrency", event.getCurrency());
            templateData.put("refundDate", event.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            
            // Send email notification
            NotificationEvent emailEvent = NotificationEvent.builder()
                .userId(event.getUserId())
                .type(NotificationType.PAYMENT_REFUNDED.name())
                .subject("Payment Refunded")
                .recipient(email)
                .channel("EMAIL")
                .templateId("payment-refunded-template")
                .templateData(templateData)
                .relatedEntityId(event.getPaymentId().toString())
                .relatedEntityType("PAYMENT")
                .build();
            
            emailService.sendEmail(
                email, 
                emailEvent.getSubject(), 
                null, 
                emailEvent.getTemplateId(), 
                emailEvent.getTemplateData()
            );
            
            // Save notification record
            Notification notification = createNotificationFromEvent(emailEvent);
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Payment refund email sent to: {}", email);
        } catch (Exception e) {
            log.error("Error sending payment refund notification", e);
        }
    }
    
    /**
     * Handle invoice created event
     */
    private void handleInvoiceCreatedEvent(InvoiceEvent event) {
        log.info("Handling invoice created event for invoice: {}", event.getInvoiceId());
        
        try {
            // Get user contact information
            var user = userService.getUserById(event.getUserId());
            String email = user.getEmail();
            
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getFirstName() + " " + user.getLastName());
            templateData.put("invoiceNumber", event.getInvoiceNumber());
            templateData.put("invoiceAmount", event.getTotalAmount().toString());
            templateData.put("invoiceCurrency", event.getCurrency());
            templateData.put("invoiceDate", event.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            templateData.put("invoiceDueDate", event.getDueAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            templateData.put("invoiceUrl", event.getInvoiceUrl());
            
            // Send email notification
            NotificationEvent emailEvent = NotificationEvent.builder()
                .userId(event.getUserId())
                .type(NotificationType.INVOICE_CREATED.name())
                .subject("New Invoice Available: " + event.getInvoiceNumber())
                .recipient(email)
                .channel("EMAIL")
                .templateId("invoice-created-template")
                .templateData(templateData)
                .relatedEntityId(event.getInvoiceId().toString())
                .relatedEntityType("INVOICE")
                .build();
            
            emailService.sendEmail(
                email, 
                emailEvent.getSubject(), 
                null, 
                emailEvent.getTemplateId(), 
                emailEvent.getTemplateData()
            );
            
            // Save notification record
            Notification notification = createNotificationFromEvent(emailEvent);
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Invoice created email sent to: {}", email);
        } catch (Exception e) {
            log.error("Error sending invoice created notification", e);
        }
    }
    
    /**
     * Handle invoice paid event
     */
    private void handleInvoicePaidEvent(InvoiceEvent event) {
        log.info("Handling invoice paid event for invoice: {}", event.getInvoiceId());
        
        try {
            // Get user contact information
            var user = userService.getUserById(event.getUserId());
            String email = user.getEmail();
            
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getFirstName() + " " + user.getLastName());
            templateData.put("invoiceNumber", event.getInvoiceNumber());
            templateData.put("invoiceAmount", event.getTotalAmount().toString());
            templateData.put("invoiceCurrency", event.getCurrency());
            templateData.put("paymentDate", event.getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            templateData.put("invoiceUrl", event.getInvoiceUrl());
            
            // Send email notification
            NotificationEvent emailEvent = NotificationEvent.builder()
                .userId(event.getUserId())
                .type(NotificationType.INVOICE_PAID.name())
                .subject("Invoice Paid: " + event.getInvoiceNumber())
                .recipient(email)
                .channel("EMAIL")
                .templateId("invoice-paid-template")
                .templateData(templateData)
                .relatedEntityId(event.getInvoiceId().toString())
                .relatedEntityType("INVOICE")
                .build();
            
            emailService.sendEmail(
                email, 
                emailEvent.getSubject(), 
                null, 
                emailEvent.getTemplateId(), 
                emailEvent.getTemplateData()
            );
            
            // Save notification record
            Notification notification = createNotificationFromEvent(emailEvent);
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Invoice paid email sent to: {}", email);
        } catch (Exception e) {
            log.error("Error sending invoice paid notification", e);
        }
    }
    
    /**
     * Handle invoice overdue event
     */
    private void handleInvoiceOverdueEvent(InvoiceEvent event) {
        log.info("Handling invoice overdue event for invoice: {}", event.getInvoiceId());
        
        try {
            // Get user contact information
            var user = userService.getUserById(event.getUserId());
            String email = user.getEmail();
            
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("userName", user.getFirstName() + " " + user.getLastName());
            templateData.put("invoiceNumber", event.getInvoiceNumber());
            templateData.put("invoiceAmount", event.getTotalAmount().toString());
            templateData.put("invoiceCurrency", event.getCurrency());
            templateData.put("invoiceDueDate", event.getDueAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            templateData.put("daysPastDue", LocalDateTime.now().toLocalDate().toEpochDay() - event.getDueAt().toLocalDate().toEpochDay());
            templateData.put("invoiceUrl", event.getInvoiceUrl());
            
            // Send email notification
            NotificationEvent emailEvent = NotificationEvent.builder()
                .userId(event.getUserId())
                .type(NotificationType.INVOICE_OVERDUE.name())
                .subject("OVERDUE: Invoice " + event.getInvoiceNumber())
                .recipient(email)
                .channel("EMAIL")
                .templateId("invoice-overdue-template")
                .templateData(templateData)
                .relatedEntityId(event.getInvoiceId().toString())
                .relatedEntityType("INVOICE")
                .build();
            
            emailService.sendEmail(
                email, 
                emailEvent.getSubject(), 
                null, 
                emailEvent.getTemplateId(), 
                emailEvent.getTemplateData()
            );
            
            // Save notification record
            Notification notification = createNotificationFromEvent(emailEvent);
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Invoice overdue email sent to: {}", email);
        } catch (Exception e) {
            log.error("Error sending invoice overdue notification", e);
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
                .channel("email")
                .recipient(event.getEmail())
                .subject("Welcome to EV SaaS Platform")
                .templateId("welcome-email")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        notification.setNotificationType(NotificationType.USER_CREATED);
        
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
                .channel("email")
                .recipient(event.getEmail())
                .subject("Your Profile Has Been Updated")
                .templateId("profile-updated")
                .templateData(templateData)
                .priority(NotificationType.Priority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        notification.setNotificationType(NotificationType.PROFILE_UPDATED);
        
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
            log.info("Profile update notification sent to user: {}", event.getEmail());
        } else {
            log.error("Failed to send profile update notification to user: {}", event.getEmail());
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
                .channel("email")
                .recipient(event.getEmail())
                .subject("Your Account Has Been Disabled")
                .templateId("account-disabled")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        notification.setNotificationType(NotificationType.ACCOUNT_DISABLED);
        
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
            log.info("Account disabled notification sent to user: {}", event.getEmail());
        } else {
            log.error("Failed to send account disabled notification to user: {}", event.getEmail());
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
                .channel("email")
                .recipient(event.getEmail())
                .subject("Your Account Has Been Reactivated")
                .templateId("account-enabled")
                .templateData(templateData)
                .priority(NotificationType.Priority.HIGH)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification notification = createNotificationFromEvent(notificationEvent);
        notification.setSent(false);
        notification.setNotificationType(NotificationType.ACCOUNT_ENABLED);
        
        notification = notificationRepository.save(notification);
        
        boolean success = emailService.sendEmail(
                event.getEmail(), 
                "Your Account Has Been Reactivated", 
                null, 
                "account-enabled", 
                templateData);
        
        if (success) {
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            log.info("Account enabled notification sent to user: {}", event.getEmail());
        } else {
            log.error("Failed to send account enabled notification to user: {}", event.getEmail());
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
                .build();
    }
} 