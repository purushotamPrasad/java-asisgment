package com.qssence.backend.subscription_panel;

import com.qssence.backend.subscription_panel.util.StandaloneTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Standalone Service Test - Main application code se completely independent
 * 
 * Ye test verify karta hai ki testing framework properly setup hai
 * aur main application code ke bina bhi tests run ho sakte hain
 */
@DisplayName("Standalone Service Test - Independent Testing")
class StandaloneServiceTest {

    @Test
    @DisplayName("Test Company Creation - Success")
    void testCompanyCreation_Success() {
        // Given
        StandaloneTestDataBuilder.TestCompany company = StandaloneTestDataBuilder.createTestCompany();
        
        // When
        String companyId = company.getCompanyId();
        String companyName = company.getCompanyName();
        String companyEmail = company.getCompanyEmailId();
        
        // Then
        assertNotNull(company);
        assertEquals("TE001", companyId);
        assertEquals("Test Company", companyName);
        assertEquals("test@company.com", companyEmail);
        assertTrue(company.getStatus().equals("ACTIVE"));
    }

    @Test
    @DisplayName("Test User License Creation - Success")
    void testUserLicenseCreation_Success() {
        // Given
        StandaloneTestDataBuilder.TestUserLicense license = StandaloneTestDataBuilder.createTestUserLicense();
        
        // When
        Long licenseId = license.getLicenseId();
        Integer totalAccess = license.getTotalUserAccess();
        Integer adminAccess = license.getAdminAccountAllowed();
        Integer userAccess = license.getUserAccountAllowed();
        
        // Then
        assertNotNull(license);
        assertEquals(1L, licenseId);
        assertEquals(10, totalAccess);
        assertEquals(2, adminAccess);
        assertEquals(8, userAccess);
        assertTrue(license.isActive());
        assertTrue(totalAccess >= (adminAccess + userAccess));
    }

    @Test
    @DisplayName("Test Company Request DTO - Success")
    void testCompanyRequestDto_Success() {
        // Given
        StandaloneTestDataBuilder.TestCompanyRequestDto request = StandaloneTestDataBuilder.createTestCompanyRequest();
        
        // When
        String companyName = request.getCompanyName();
        String companyPrefix = request.getCompanyPrefix();
        String companyEmail = request.getCompanyEmailId();
        
        // Then
        assertNotNull(request);
        assertEquals("Test Company", companyName);
        assertEquals("TE", companyPrefix);
        assertEquals("test@company.com", companyEmail);
        assertFalse(companyPrefix.isEmpty());
    }

    @Test
    @DisplayName("Test User License Request DTO - Success")
    void testUserLicenseRequestDto_Success() {
        // Given
        StandaloneTestDataBuilder.TestUserLicenseRequestDto request = StandaloneTestDataBuilder.createTestUserLicenseRequest();
        
        // When
        Long companyId = request.getCompanyId();
        Integer totalAccess = request.getTotalUserAccess();
        Integer adminAccess = request.getAdminAccountAllowed();
        Integer userAccess = request.getUserAccountAllowed();
        
        // Then
        assertNotNull(request);
        assertEquals(1L, companyId);
        assertEquals(10, totalAccess);
        assertEquals(2, adminAccess);
        assertEquals(8, userAccess);
        assertTrue(totalAccess >= (adminAccess + userAccess));
    }

    @Test
    @DisplayName("Test Company Response DTO - Success")
    void testCompanyResponseDto_Success() {
        // Given
        StandaloneTestDataBuilder.TestCompanyResponseDto response = StandaloneTestDataBuilder.createTestCompanyResponse();
        
        // When
        String companyId = response.getCompanyId();
        String companyName = response.getCompanyName();
        String status = response.getStatus();
        
        // Then
        assertNotNull(response);
        assertEquals("TE001", companyId);
        assertEquals("Test Company", companyName);
        assertEquals("ACTIVE", status);
        assertFalse(companyId.isEmpty());
    }

    @Test
    @DisplayName("Test User License Response DTO - Success")
    void testUserLicenseResponseDto_Success() {
        // Given
        StandaloneTestDataBuilder.TestUserLicenseResponseDto response = StandaloneTestDataBuilder.createTestUserLicenseResponse();
        
        // When
        Long licenseId = response.getLicenseId();
        String licenseKey = response.getLicenseKey();
        boolean isActive = response.isActive();
        boolean isExpired = response.isExpired();
        
        // Then
        assertNotNull(response);
        assertEquals(1L, licenseId);
        assertEquals("test-key", licenseKey);
        assertTrue(isActive);
        assertFalse(isExpired);
        assertFalse(licenseKey.isEmpty());
    }

    @Test
    @DisplayName("Test Data Validation - Company Email")
    void testDataValidation_CompanyEmail() {
        // Given
        StandaloneTestDataBuilder.TestCompany company = StandaloneTestDataBuilder.createTestCompany();
        
        // When
        String email = company.getCompanyEmailId();
        boolean isValidEmail = email.contains("@") && email.contains(".");
        
        // Then
        assertTrue(isValidEmail);
        assertFalse(email.isEmpty());
        assertTrue(email.endsWith(".com"));
    }

    @Test
    @DisplayName("Test Data Validation - User Access Limits")
    void testDataValidation_UserAccessLimits() {
        // Given
        StandaloneTestDataBuilder.TestUserLicense license = StandaloneTestDataBuilder.createTestUserLicense();
        
        // When
        Integer totalAccess = license.getTotalUserAccess();
        Integer adminAccess = license.getAdminAccountAllowed();
        Integer userAccess = license.getUserAccountAllowed();
        Integer calculatedTotal = adminAccess + userAccess;
        
        // Then
        assertTrue(totalAccess >= calculatedTotal);
        assertTrue(adminAccess > 0);
        assertTrue(userAccess > 0);
        assertTrue(totalAccess > 0);
    }

    @Test
    @DisplayName("Test Data Validation - License Dates")
    void testDataValidation_LicenseDates() {
        // Given
        StandaloneTestDataBuilder.TestUserLicense license = StandaloneTestDataBuilder.createTestUserLicense();
        
        // When
        java.time.LocalDate purchaseDate = license.getPurchaseDate();
        java.time.LocalDate expiryDate = license.getExpiryDate();
        boolean isExpiryAfterPurchase = expiryDate.isAfter(purchaseDate);
        
        // Then
        assertNotNull(purchaseDate);
        assertNotNull(expiryDate);
        assertTrue(isExpiryAfterPurchase);
        assertFalse(expiryDate.isBefore(java.time.LocalDate.now()));
    }

    @Test
    @DisplayName("Test Data Validation - Company Prefix")
    void testDataValidation_CompanyPrefix() {
        // Given
        StandaloneTestDataBuilder.TestCompany company = StandaloneTestDataBuilder.createTestCompany();
        
        // When
        String prefix = company.getCompanyPrefix();
        boolean isValidPrefix = prefix != null && !prefix.isEmpty() && prefix.length() >= 2;
        
        // Then
        assertTrue(isValidPrefix);
        assertEquals(2, prefix.length());
        assertTrue(prefix.matches("[A-Z]{2}"));
    }
}
