package com.qssence.backend.authservice.service.MailServices;

import com.qssence.backend.authservice.entity.SmtpConfig;
import com.qssence.backend.authservice.repository.MailRepository.SmtpConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Optional;
import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Autowired
    private SmtpConfigRepository emailConfigRepository;

    public JavaMailSender javaMailSender(String providerName) {
        Optional<SmtpConfig> optionalConfig = emailConfigRepository.findByProviderName(providerName);
        if (optionalConfig.isPresent()) {
            SmtpConfig emailConfig = optionalConfig.get();

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setProtocol(emailConfig.getMailProtocol());
            mailSender.setHost(emailConfig.getMailHost());
            mailSender.setPort(emailConfig.getMailPort());
            mailSender.setUsername(emailConfig.getMailUsername());
            mailSender.setPassword(emailConfig.getMailPassword());

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", emailConfig.getMailProtocol());
            props.put("mail.smtp.auth", String.valueOf(emailConfig.isMailAuth()));  // ✅ boolean → string
            props.put("mail.smtp.starttls.enable", String.valueOf(emailConfig.isMailStarttlsEnable())); // ✅ boolean → string
            props.put("mail.smtp.starttls.required", "true"); // ✅ force STARTTLS
            props.put("mail.smtp.ssl.trust", emailConfig.getMailHost()); // ✅ trust smtp.gmail.com
            props.put("mail.debug", "true");

            return mailSender;
        } else {
            throw new RuntimeException("Email configuration not found in the database!");
        }
    }
}
