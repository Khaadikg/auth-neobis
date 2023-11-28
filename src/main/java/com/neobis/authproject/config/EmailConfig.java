package com.neobis.authproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${gmail}")
    private String mail;
    @Value("${gmail-app-code}")
    private String code;

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(mail);
        mailSender.setPassword(code);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol","smtp");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.debug","true");
        return mailSender;
    }
}
