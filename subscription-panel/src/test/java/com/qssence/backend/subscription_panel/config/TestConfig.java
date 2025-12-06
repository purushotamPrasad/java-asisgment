package com.qssence.backend.subscription_panel.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Test Configuration - Testing ke liye special configuration
 * Ye configuration sirf test cases ke liye use hoti hai
 */
@TestConfiguration
public class TestConfig {

    /**
     * Mock JavaMailSender for testing
     * Real email nahi bhejega, sirf testing ke liye
     */
    @Bean
    @Primary
    public JavaMailSender mockJavaMailSender() {
        return new JavaMailSenderImpl();
    }
}
