//package com.qssence.backend.subscription_panel.service.impl;
//
//import com.qssence.backend.subscription_panel.dbo.Company;
//import com.qssence.backend.subscription_panel.dbo.Status;
//import com.qssence.backend.subscription_panel.dto.request.CompanyRequestDto;
//import com.qssence.backend.subscription_panel.dto.response.CompanyResponseDto;
//import com.qssence.backend.subscription_panel.repository.CompanyRepository;
//import com.qssence.backend.subscription_panel.util.TestDataBuilder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * CompanyServiceImpl ke liye Unit Tests
// *
// * Ye test class mein hum CompanyServiceImpl ke sabhi methods ko test karte hain
// * Mock objects use karte hain real database ke bina testing karne ke liye
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("Company Service Tests")
//class CompanyServiceImplTest {
//
//    @Mock
//    private CompanyRepository companyRepository;
//
//    @InjectMocks
//    private CompanyServiceImpl companyService;
//
//    private Company testCompany;
//    private CompanyRequestDto testRequest;
//
//    @BeforeEach
//    void setUp() {
//        // Har test se pehle ye setup run hota hai
//        testCompany = TestDataBuilder.company()
//                .companyId("TE001")
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix("TE")
//                .build();
//
//        testRequest = TestDataBuilder.companyRequest()
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix("TE")
//                .build();
//    }
//
//    @Test
//    @DisplayName("Create Company - Success Case")
//    void createCompany_Success() {
//        // Given - Test data setup
//        when(companyRepository.findByCompanyEmailId("test@company.com")).thenReturn(Optional.empty());
//        when(companyRepository.count()).thenReturn(0L);
//        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
//
//        // When - Method call
//        CompanyResponseDto result = companyService.createCompany(testRequest);
//
//        // Then - Assertions
//        assertNotNull(result);
//        assertEquals("TE001", result.getCompanyId());
//        assertEquals("Test Company", result.getCompanyName());
//        assertEquals("test@company.com", result.getCompanyEmailId());
//        assertEquals("TE", result.getCompanyPrefix());
//
//        // Verify ki methods call hue hain
//        verify(companyRepository).findByCompanyEmailId("test@company.com");
//        verify(companyRepository).count();
//        verify(companyRepository).save(any(Company.class));
//    }
//
//    @Test
//    @DisplayName("Create Company - Email Already Exists")
//    void createCompany_EmailAlreadyExists() {
//        // Given
//        when(companyRepository.findByCompanyEmailId("test@company.com")).thenReturn(Optional.of(testCompany));
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> companyService.createCompany(testRequest));
//
//        assertEquals("Email already exists: test@company.com", exception.getMessage());
//        verify(companyRepository).findByCompanyEmailId("test@company.com");
//        verify(companyRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Create Company - Missing Company Prefix")
//    void createCompany_MissingCompanyPrefix() {
//        // Given
//        CompanyRequestDto invalidRequest = TestDataBuilder.companyRequest()
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix("") // Empty prefix
//                .build();
//
//        when(companyRepository.findByCompanyEmailId("test@company.com")).thenReturn(Optional.empty());
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> companyService.createCompany(invalidRequest));
//
//        assertEquals("Company prefix is required and cannot be blank", exception.getMessage());
//        verify(companyRepository).findByCompanyEmailId("test@company.com");
//        verify(companyRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Create Company - Null Company Prefix")
//    void createCompany_NullCompanyPrefix() {
//        // Given
//        CompanyRequestDto invalidRequest = TestDataBuilder.companyRequest()
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix(null) // Null prefix
//                .build();
//
//        when(companyRepository.findByCompanyEmailId("test@company.com")).thenReturn(Optional.empty());
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> companyService.createCompany(invalidRequest));
//
//        assertEquals("Company prefix is required and cannot be blank", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Get Company By ID - Success")
//    void getCompanyById_Success() {
//        // Given
//        when(companyRepository.findById("TE001")).thenReturn(Optional.of(testCompany));
//
//        // When
//        CompanyResponseDto result = companyService.getCompanyById("TE001");
//
//        // Then
//        assertNotNull(result);
//        assertEquals("TE001", result.getCompanyId());
//        assertEquals("Test Company", result.getCompanyName());
//        verify(companyRepository).findById("TE001");
//    }
//
//    @Test
//    @DisplayName("Get Company By ID - Not Found")
//    void getCompanyById_NotFound() {
//        // Given
//        when(companyRepository.findById("TE001")).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> companyService.getCompanyById("TE001"));
//
//        assertEquals("Company with ID TE001 not found.", exception.getMessage());
//        verify(companyRepository).findById("TE001");
//    }
//
//    @Test
//    @DisplayName("Get All Companies - Success")
//    void getAllCompanies_Success() {
//        // Given
//        List<Company> companies = Arrays.asList(testCompany);
//        when(companyRepository.findAll()).thenReturn(companies);
//
//        // When
//        List<CompanyResponseDto> result = companyService.getAllCompanies();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("TE001", result.get(0).getCompanyId());
//        verify(companyRepository).findAll();
//    }
//
//    @Test
//    @DisplayName("Get All Companies - Empty List")
//    void getAllCompanies_EmptyList() {
//        // Given
//        when(companyRepository.findAll()).thenReturn(Arrays.asList());
//
//        // When
//        List<CompanyResponseDto> result = companyService.getAllCompanies();
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(companyRepository).findAll();
//    }
//
//    @Test
//    @DisplayName("Delete Company - Success")
//    void deleteCompany_Success() {
//        // Given
//        when(companyRepository.findById("TE001")).thenReturn(Optional.of(testCompany));
//
//        // When
//        companyService.deleteCompany("TE001");
//
//        // Then
//        verify(companyRepository).findById("TE001");
//        verify(companyRepository).delete(testCompany);
//    }
//
//    @Test
//    @DisplayName("Delete Company - Not Found")
//    void deleteCompany_NotFound() {
//        // Given
//        when(companyRepository.findById("TE001")).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> companyService.deleteCompany("TE001"));
//
//        assertEquals("Company with ID TE001 not found.", exception.getMessage());
//        verify(companyRepository).findById("TE001");
//        verify(companyRepository, never()).delete(any());
//    }
//
//    @Test
//    @DisplayName("Update Company - Success")
//    void updateCompany_Success() {
//        // Given
//        CompanyRequestDto updateRequest = TestDataBuilder.companyRequest()
//                .companyName("Updated Company")
//                .companyEmailId("updated@company.com")
//                .companyPrefix("UC")
//                .status("INACTIVE")
//                .build();
//
//        when(companyRepository.findById("TE001")).thenReturn(Optional.of(testCompany));
//        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
//
//        // When
//        CompanyResponseDto result = companyService.updateCompany("TE001", updateRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(companyRepository).findById("TE001");
//        verify(companyRepository).save(any(Company.class));
//    }
//
//    @Test
//    @DisplayName("Update Company - Not Found")
//    void updateCompany_NotFound() {
//        // Given
//        when(companyRepository.findById("TE001")).thenReturn(Optional.empty());
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class,
//            () -> companyService.updateCompany("TE001", testRequest));
//
//        assertEquals("Company with ID TE001 not found.", exception.getMessage());
//        verify(companyRepository).findById("TE001");
//        verify(companyRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Update Company - Missing Prefix")
//    void updateCompany_MissingPrefix() {
//        // Given
//        CompanyRequestDto invalidRequest = TestDataBuilder.companyRequest()
//                .companyName("Updated Company")
//                .companyEmailId("updated@company.com")
//                .companyPrefix("") // Empty prefix
//                .build();
//
//        when(companyRepository.findById("TE001")).thenReturn(Optional.of(testCompany));
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> companyService.updateCompany("TE001", invalidRequest));
//
//        assertEquals("Company prefix is required and cannot be blank", exception.getMessage());
//        verify(companyRepository).findById("TE001");
//        verify(companyRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Update Company - Invalid Status")
//    void updateCompany_InvalidStatus() {
//        // Given
//        CompanyRequestDto invalidRequest = TestDataBuilder.companyRequest()
//                .companyName("Updated Company")
//                .companyEmailId("updated@company.com")
//                .companyPrefix("UC")
//                .status("INVALID_STATUS") // Invalid status
//                .build();
//
//        when(companyRepository.findById("TE001")).thenReturn(Optional.of(testCompany));
//
//        // When & Then
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> companyService.updateCompany("TE001", invalidRequest));
//
//        assertEquals("Invalid status value: INVALID_STATUS", exception.getMessage());
//        verify(companyRepository).findById("TE001");
//        verify(companyRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Generate Company ID - Test Logic")
//    void generateCompanyId_TestLogic() {
//        // Given
//        when(companyRepository.count()).thenReturn(5L);
//        when(companyRepository.findByCompanyEmailId(anyString())).thenReturn(Optional.empty());
//        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
//
//        CompanyRequestDto request = TestDataBuilder.companyRequest()
//                .companyName("New Company")
//                .companyEmailId("new@company.com")
//                .companyPrefix("NC")
//                .build();
//
//        // When
//        CompanyResponseDto result = companyService.createCompany(request);
//
//        // Then
//        assertNotNull(result);
//        // Company ID should be "NE006" (first 2 letters of "New Company" + 006)
//        assertEquals("NE006", result.getCompanyId());
//        verify(companyRepository).count();
//    }
//
//    @Test
//    @DisplayName("Company Status Mapping - Test Different Statuses")
//    void companyStatusMapping_TestDifferentStatuses() {
//        // Given
//        CompanyRequestDto activeRequest = TestDataBuilder.companyRequest()
//                .companyName("Active Company")
//                .companyEmailId("active@company.com")
//                .companyPrefix("AC")
//                .status("ACTIVE")
//                .build();
//
//        when(companyRepository.findByCompanyEmailId("active@company.com")).thenReturn(Optional.empty());
//        when(companyRepository.count()).thenReturn(0L);
//        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
//            Company company = invocation.getArgument(0);
//            return company;
//        });
//
//        // When
//        CompanyResponseDto result = companyService.createCompany(activeRequest);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("ACTIVE", result.getStatus());
//    }
//}
