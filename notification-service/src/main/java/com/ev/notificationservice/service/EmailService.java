package com.ev.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    /**
     * Send an email with the given parameters
     * @param to Recipient email address
     * @param subject Email subject
     * @param content Email content (HTML)
     * @param templateId Optional template ID
     * @param templateData Optional template data
     * @return true if the email was sent successfully
     */
    public boolean sendEmail(String to, String subject, String content, 
                          String templateId, Map<String, Object> templateData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, applicationName);
            helper.setTo(to);
            helper.setSubject(subject);
            
            String emailContent;
            if (templateId != null && templateData != null) {
                // Use Thymeleaf template
                Context context = new Context();
                context.setVariables(templateData);
                emailContent = templateEngine.process("templates/email/" + templateId, context);
            } else {
                // Use the provided content
                emailContent = content;
            }
            
            helper.setText(emailContent, true);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            return false;
        }
    }
} 