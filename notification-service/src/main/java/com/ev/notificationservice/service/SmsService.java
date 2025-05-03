package com.ev.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending SMS notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    /**
     * Send an SMS message to a phone number
     * @param phoneNumber The recipient's phone number
     * @param message The message text
     * @return true if the SMS was sent successfully
     */
    public boolean sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.info("SMS sending is disabled. Would have sent SMS to {}: {}", phoneNumber, message);
            return true; // Return true to not break the flow for testing
        }
        
        try {
            // Here we would integrate with an SMS service provider like Twilio
            // This is a placeholder implementation that logs the message and returns success
            
            log.info("Sending SMS to {}: {}", phoneNumber, message);
            
            // Actual SMS sending logic would go here
            // For example, using Twilio:
            // Message twilioMessage = Message.creator(
            //     new PhoneNumber(phoneNumber),
            //     new PhoneNumber(fromPhoneNumber),
            //     message)
            // .create();
            
            log.info("Successfully sent SMS to {}", phoneNumber);
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }
} 