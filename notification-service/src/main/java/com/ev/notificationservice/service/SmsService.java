package com.ev.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending SMS messages
 * This is a placeholder implementation. In a real-world scenario,
 * you would integrate with an SMS provider like Twilio, AWS SNS, etc.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    /**
     * Send an SMS with the given parameters
     * @param phoneNumber Recipient's phone number
     * @param message SMS content
     * @return true if the SMS was sent successfully
     */
    public boolean sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS sending is disabled. Would have sent SMS to {} with message: {}", phoneNumber, message);
            return false;
        }
        
        try {
            // In a real implementation, you would call an SMS provider API here
            // For example, using Twilio:
            // Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(fromNumber), message).create();
            
            log.info("SMS sent successfully to: {}", phoneNumber);
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", phoneNumber, e);
            return false;
        }
    }
} 