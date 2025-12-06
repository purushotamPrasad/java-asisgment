package com.qssence.backend.subscription_panel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Main Application Test - Spring Boot context load karne ke liye
 * 
 * Ye test ensure karta hai ki Spring Boot application properly start ho rahi hai
 * aur sabhi beans correctly configured hain
 */
@SpringBootTest
@ActiveProfiles("test")
class SubscriptionPanelApplicationTests {

    @Test
    void contextLoads() {
        // Ye test sirf verify karta hai ki Spring context properly load ho raha hai
        // Agar koi configuration issue hai to ye test fail ho jayega
    }
}