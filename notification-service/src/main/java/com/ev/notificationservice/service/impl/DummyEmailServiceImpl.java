package com.ev.notificationservice.service.impl;

import com.ev.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A simplified implementation of EmailService that doesn't use Thymeleaf templates
 * This is used in development and testing environments
 */
@Service
@Primary
@Slf4j
public class DummyEmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    
    @Value("${spring.mail.username:noreply@nbevc.com}")
    private String fromEmail;
    
    public DummyEmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            emailSender.send(message);
            log.info("Email sent to {}: subject={}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendTemplatedEmail(String to, String subject, String templateName, Map<String, Object> templateData) {
        // In the dummy implementation, we just convert the template data to a simple text format
        StringBuilder content = new StringBuilder();
        content.append(subject).append("\n\n");
        
        if (templateData != null) {
            for (Map.Entry<String, Object> entry : templateData.entrySet()) {
                content.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        return sendEmail(to, subject, content.toString());
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String content, String templateName, Map<String, Object> templateData) {
        if (templateName != null && !templateName.isEmpty()) {
            return sendTemplatedEmail(to, subject, templateName, templateData);
        } else {
            return sendEmail(to, subject, content);
        }
    }
} 