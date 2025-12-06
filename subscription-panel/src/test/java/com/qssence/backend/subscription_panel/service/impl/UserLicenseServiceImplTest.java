//package com.qssence.backend.subscription_panel.service.impl;
//
//import com.qssence.backend.subscription_panel.dbo.Company;
//import com.qssence.backend.subscription_panel.dbo.UserLicense;
//import com.qssence.backend.subscription_panel.dto.request.UserLicenseRequestDto;
//import com.qssence.backend.subscription_panel.dto.response.UserLicenseResponseDto;
//import com.qssence.backend.subscription_panel.repository.CompanyRepository;
//import com.qssence.backend.subscription_panel.repository.UserLicenseRepository;
//import com.qssence.backend.subscription_panel.repository.plansRepo.CompanyPlanFeatureRepository;
//import com.qssence.backend.subscription_panel.service.MailService;
//import com.qssence.backend.subscription_panel.util.TestDataBuilder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * UserLicenseServiceImpl ke liye Unit Tests
// *
// * Ye test class mein hum UserLicenseServiceImpl ke sabhi methods ko test karte hain
// * Mock objects use karte hain real database ke bina testing karne ke liye
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("UserLicense Service Tests")
//class UserLicenseServiceImplTest {
//
//    @Mock
//    private UserLicenseRepository userLicenseRepository;
//
//    @Mock
//    private CompanyRepository companyRepository;
//
//    @Mock
//    private CompanyPlanFeatureRepository companyPlanFeatureRepository;
//
//    @Mock
//    private MailService mailService;
//
//    @InjectMocks
//    private UserLicenseServiceImpl userLicenseService;
//
//    private Company testCompany;
//    private UserLicense testUserLicense;
//    private UserLicenseRequestDto testRequest;
//
//    @BeforeEach
//    void setUp() {
//        // Har test se pehle ye setup run hota hai
//        testCompany = TestDataBuilder.company()
//                .companyId("TE001")
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .build();
//
//        testUserLicense = TestDataBuilder.userLicense()
//                .licenseId(1L)
//                .company(testCompany)
//                .build();
//
//        testRequest = TestDataBuilder.userLicenseRequest()
//                .companyId(1L)
//                .totalUserAccess(10)
//                .adminAccountAllowed(2)
//                .userAccountAllowed(8)
//                .build();
//    }
//
//    @Test
//    @DisplayName("Create User License - Success Case")
//    void createUserLicense_Success() {
//        // Given - Test data setup
//        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
//        when(userLicenseRepository.findByCompany(testCompany)).thenReturn(null);
//        when(userLicenseRepository.save(any(UserLicense.class))).thenReturn(testUserLicense);
//
//        // When - Method call
//        UserLicenseResponseDto result = userLicenseService.createUserLicense(testRequest);
//
//        // Then - Assertions
//        assertNotNull(result);
//        assertEquals(testCompany.getCompanyId(), result.getCompanyId());
//        assertEquals(testCompany.getCompanyName(), result.getCompanyName());
//
//        // Verify ki methods call hue hain
//        verify(companyRepository).findById(1L);
//        verify(userLicenseRepository).findByCompany(testCompany);
//        verify(userLicenseRepository).save(any(UserLicense.class));
//        verify(mailService).sendEmail(anyString(), anyString(), anyString());
//    }
//
//    @Test
//    @DisplayName("Create User License - Company Not Found")
//    void createUserLicense_CompanyNotFound() {
//        // Given
//        when(companyRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> userLicenseService.createUserLicense(testRequest));
//
//        assertEquals("Company not found", exception.getMessage());
//        verify(companyRepository).findById(1L);
//        verify(userLicenseRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Create User License - License Already Exists")
//    void createUserLicense_LicenseAlreadyExists() {
//        // Given
//        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
//        when(userLicenseRepository.findByCompany(testCompany)).thenReturn(testUserLicense);
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> userLicenseService.createUserLicense(testRequest));
//
//        assertEquals("A user license already exists for this company.", exception.getMessage());
//        verify(companyRepository).findById(1L);
//        verify(userLicenseRepository).findByCompany(testCompany);
//        verify(userLicenseRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Create User License - Invalid User Access Limits")
//    void createUserLicense_InvalidUserAccessLimits() {
//        // Given - Admin + User accounts exceed total access
//        UserLicenseRequestDto invalidRequest = TestDataBuilder.userLicenseRequest()
//                .companyId(1L)
//                .totalUserAccess(5)  // Total 5
//                .adminAccountAllowed(3)  // Admin 3
//                .userAccountAllowed(3)   // User 3 = Total 6 > 5
//                .build();
//
//        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
//        when(userLicenseRepository.findByCompany(testCompany)).thenReturn(null);
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> userLicenseService.createUserLicense(invalidRequest));
//
//        assertEquals("Admin and User accounts cannot exceed total user access limit", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Get All User Licenses - Success")
//    void getAllUserLicenses_Success() {
//        // Given
//        List<UserLicense> licenses = Arrays.asList(testUserLicense);
//        when(userLicenseRepository.findAll()).thenReturn(licenses);
//        when(companyPlanFeatureRepository.findByCompany(any(Company.class))).thenReturn(Arrays.asList());
//
//        // When
//        List<UserLicenseResponseDto> result = userLicenseService.getAllUserLicenses();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(testUserLicense.getLicenseId(), result.get(0).getLicenseId());
//        verify(userLicenseRepository).findAll();
//    }
//
//    @Test
//    @DisplayName("Get User License By ID - Success")
//    void getUserLicenseById_Success() {
//        // Given
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.of(testUserLicense));
//        when(companyPlanFeatureRepository.findByCompany(any(Company.class))).thenReturn(Arrays.asList());
//
//        // When
//        UserLicenseResponseDto result = userLicenseService.getUserLicenseById(1L);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(testUserLicense.getLicenseId(), result.getLicenseId());
//        verify(userLicenseRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Get User License By ID - Not Found")
//    void getUserLicenseById_NotFound() {
//        // Given
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> userLicenseService.getUserLicenseById(1L));
//
//        assertEquals("User License not found", exception.getMessage());
//        verify(userLicenseRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Decode License Key - Success")
//    void decodeLicenseKey_Success() {
//        // Given
//        String licenseKey = "MQ==:TE001"; // Base64 encoded "1:TE001"
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.of(testUserLicense));
//        when(companyPlanFeatureRepository.findByCompany(any(Company.class))).thenReturn(Arrays.asList());
//
//        // When
//        UserLicenseResponseDto result = userLicenseService.decodeLicenseKey(licenseKey);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(testUserLicense.getLicenseId(), result.getLicenseId());
//        verify(userLicenseRepository).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Decode License Key - Invalid Format")
//    void decodeLicenseKey_InvalidFormat() {
//        // Given
//        String invalidKey = "invalid-key";
//
//        // When & Then
//        assertThrows(RuntimeException.class,
//            () -> userLicenseService.decodeLicenseKey(invalidKey));
//    }
//
//    @Test
//    @DisplayName("Decode License Key - Empty Key")
//    void decodeLicenseKey_EmptyKey() {
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> userLicenseService.decodeLicenseKey(""));
//
//        assertEquals("License key is missing or empty", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Decode License Key - License Not Found")
//    void decodeLicenseKey_LicenseNotFound() {
//        // Given
//        String licenseKey = "MQ==:TE001";
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> userLicenseService.decodeLicenseKey(licenseKey));
//
//        assertEquals("License not found in database", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Decode License Key - License Inactive")
//    void decodeLicenseKey_LicenseInactive() {
//        // Given
//        String licenseKey = "MQ==:TE001";
//        testUserLicense.setActive(false);
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.of(testUserLicense));
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> userLicenseService.decodeLicenseKey(licenseKey));
//
//        assertEquals("Your license key is disabled. Please generate a new key.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Decode License Key - License Expired")
//    void decodeLicenseKey_LicenseExpired() {
//        // Given
//        String licenseKey = "MQ==:TE001";
//        testUserLicense.setExpiryDate(LocalDate.now().minusDays(1)); // Yesterday
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.of(testUserLicense));
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> userLicenseService.decodeLicenseKey(licenseKey));
//
//        assertEquals("Your license key has expired. Please renew your license.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Update User License - Success")
//    void updateUserLicense_Success() {
//        // Given
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.of(testUserLicense));
//        when(userLicenseRepository.save(any(UserLicense.class))).thenReturn(testUserLicense);
//
//        // When
//        UserLicenseResponseDto result = userLicenseService.updateUserLicense(1L, testRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(userLicenseRepository).findById(1L);
//        verify(userLicenseRepository).save(any(UserLicense.class));
//        verify(mailService).sendEmail(anyString(), anyString(), anyString());
//    }
//
//    @Test
//    @DisplayName("Update User License - Not Found")
//    void updateUserLicense_NotFound() {
//        // Given
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> userLicenseService.updateUserLicense(1L, testRequest));
//
//        assertEquals("User License not found", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Delete User License - Success")
//    void deleteUserLicense_Success() {
//        // Given
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.of(testUserLicense));
//        when(userLicenseRepository.save(any(UserLicense.class))).thenReturn(testUserLicense);
//
//        // When
//        userLicenseService.deleteUserLicense(1L);
//
//        // Then
//        assertFalse(testUserLicense.isActive()); // Should be marked as inactive
//        verify(userLicenseRepository).findById(1L);
//        verify(userLicenseRepository).save(testUserLicense);
//    }
//
//    @Test
//    @DisplayName("Delete User License - Not Found")
//    void deleteUserLicense_NotFound() {
//        // Given
//        when(userLicenseRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> userLicenseService.deleteUserLicense(1L));
//
//        assertEquals("User License not found", exception.getMessage());
//    }
//}
