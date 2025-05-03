package com.ev.notificationservice.service;

import java.util.Map;

/**
 * Service for sending email notifications
 */
public interface EmailService {
    
    /**
     * Send a simple email
     * @param to Recipient email
     * @param subject Email subject
     * @param content Email body
     * @return true if the email was sent successfully
     */
    boolean sendEmail(String to, String subject, String content);
    
    /**
     * Send an email using a template
     * @param to Recipient email
     * @param subject Email subject
     * @param templateName Template name
     * @param templateData Template variables
     * @return true if the email was sent successfully
     */
    boolean sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> templateData);
    
    /**
     * Send a simple text or templated email depending on whether a template is provided
     * @param to Recipient email
     * @param subject Email subject
     * @param content Email content
     * @param templateName Template name (optional)
     * @param templateData Template variables (optional)
     * @return true if the email was sent successfully
     */
    boolean sendEmail(String to, String subject, String content, String templateName, Map<String, Object> templateData);
} 