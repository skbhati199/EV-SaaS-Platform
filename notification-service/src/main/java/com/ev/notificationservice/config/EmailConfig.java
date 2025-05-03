package com.ev.notificationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Properties;

@Configuration
@Slf4j
public class EmailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;
    
    @Value("${spring.mail.port:587}")
    private int port;
    
    @Value("${spring.mail.username:noreply@evsaas.com}")
    private String username;
    
    @Value("${spring.mail.password:}")
    private String password;
    
    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private String auth;
    
    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private String starttls;
    
    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;
    
    @Bean
    public JavaMailSender javaMailSender() {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Using dummy mail sender.");
            return new DummyJavaMailSender();
        }
        
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        
        if (username != null && !username.isEmpty()) {
            mailSender.setUsername(username);
        }
        
        if (password != null && !password.isEmpty()) {
            mailSender.setPassword(password);
        }
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", "false");
        
        return mailSender;
    }
    
    @Bean
    @Primary
    @Qualifier("emailTemplateResolver")
    public ITemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/email/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }
    
    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(@Qualifier("emailTemplateResolver") ITemplateResolver templateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
    
    /**
     * A dummy implementation of JavaMailSender that logs emails instead of sending them
     * This is useful for development and testing
     */
    @Slf4j
    private static class DummyJavaMailSender extends JavaMailSenderImpl {
        @Override
        public void send(org.springframework.mail.SimpleMailMessage simpleMessage) {
            log.info("Dummy mail sender - Would have sent email: {} -> {}: {}",
                    simpleMessage.getFrom(),
                    simpleMessage.getTo(),
                    simpleMessage.getSubject());
        }
        
        @Override
        public void send(org.springframework.mail.SimpleMailMessage... simpleMessages) {
            for (org.springframework.mail.SimpleMailMessage message : simpleMessages) {
                send(message);
            }
        }
        
        @Override
        public void send(jakarta.mail.internet.MimeMessage mimeMessage) {
            try {
                log.info("Dummy mail sender - Would have sent mime message to: {}", 
                        mimeMessage.getAllRecipients());
            } catch (jakarta.mail.MessagingException e) {
                log.error("Error reading mime message", e);
            }
        }
        
        @Override
        public void send(jakarta.mail.internet.MimeMessage... mimeMessages) {
            for (jakarta.mail.internet.MimeMessage message : mimeMessages) {
                send(message);
            }
        }
    }
} 