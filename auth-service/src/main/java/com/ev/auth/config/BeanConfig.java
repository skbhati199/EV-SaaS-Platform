package com.ev.auth.config;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    
    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }
    
    @Bean
    public CodeGenerator codeGenerator() {
        return new DefaultCodeGenerator();
    }
    
    @Bean
    public TimeProvider timeProvider() {
        return new SystemTimeProvider();
    }
    
    @Bean
    public CodeVerifier codeVerifier(CodeGenerator codeGenerator, TimeProvider timeProvider) {
        return new DefaultCodeVerifier(codeGenerator, timeProvider);
    }
    
    @Bean
    public QrGenerator qrGenerator() {
        return new ZxingPngQrGenerator();
    }
} 