package com.qssence.backend.subscription_panel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple Test - Basic testing setup verify karne ke liye
 * 
 * Ye test verify karta hai ki testing framework properly setup hai
 */
@DisplayName("Simple Test")
class SimpleTest {

    @Test
    @DisplayName("Basic Test - Testing Framework Setup")
    void basicTest() {
        // Given
        String expected = "Hello Testing";
        
        // When
        String actual = "Hello Testing";
        
        // Then
        assertEquals(expected, actual);
        assertNotNull(actual);
        assertTrue(actual.contains("Testing"));
    }

    @Test
    @DisplayName("Math Test - Basic Calculations")
    void mathTest() {
        // Given
        int a = 5;
        int b = 3;
        
        // When
        int sum = a + b;
        int product = a * b;
        
        // Then
        assertEquals(8, sum);
        assertEquals(15, product);
        assertTrue(sum > product);
    }

    @Test
    @DisplayName("String Test - String Operations")
    void stringTest() {
        // Given
        String companyName = "Test Company";
        
        // When
        String upperCase = companyName.toUpperCase();
        String lowerCase = companyName.toLowerCase();
        int length = companyName.length();
        
        // Then
        assertEquals("TEST COMPANY", upperCase);
        assertEquals("test company", lowerCase);
        assertEquals(12, length);
        assertFalse(companyName.isEmpty());
    }
}
