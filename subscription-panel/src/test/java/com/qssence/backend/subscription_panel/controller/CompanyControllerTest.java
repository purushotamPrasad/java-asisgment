//package com.qssence.backend.subscription_panel.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.qssence.backend.subscription_panel.dto.request.CompanyRequestDto;
//import com.qssence.backend.subscription_panel.dto.response.CompanyResponseDto;
//import com.qssence.backend.subscription_panel.service.CompanyService;
//import com.qssence.backend.subscription_panel.util.TestDataBuilder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * CompanyController ke liye Integration Tests
// *
// * Ye test class mein hum CompanyController ke sabhi endpoints ko test karte hain
// * MockMvc use karte hain HTTP requests simulate karne ke liye
// */
//@WebMvcTest(CompanyController.class)
//@DisplayName("Company Controller Tests")
//class CompanyControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CompanyService companyService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private CompanyRequestDto testRequest;
//    private CompanyResponseDto testResponse;
//
//    @BeforeEach
//    void setUp() {
//        // Har test se pehle ye setup run hota hai
//        testRequest = TestDataBuilder.companyRequest()
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix("TE")
//                .password("TestPass123!")
//                .build();
//
//        testResponse = CompanyResponseDto.builder()
//                .companyId("TE001")
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix("TE")
//                .location("Mumbai")
//                .status("ACTIVE")
//                .build();
//    }
//
//    @Test
//    @DisplayName("Create Company - Success")
//    void createCompany_Success() throws Exception {
//        // Given
//        when(companyService.createCompany(any(CompanyRequestDto.class)))
//                .thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/company/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Company created successfully"))
//                .andExpect(jsonPath("$.data.companyId").value("TE001"))
//                .andExpect(jsonPath("$.data.companyName").value("Test Company"));
//
//        verify(companyService).createCompany(any(CompanyRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Create Company - Password Validation Error")
//    void createCompany_PasswordValidationError() throws Exception {
//        // Given - Weak password
//        CompanyRequestDto weakPasswordRequest = TestDataBuilder.companyRequest()
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix("TE")
//                .password("weak") // Weak password
//                .build();
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/company/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(weakPasswordRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").containsString("Password must be at least 8 characters"));
//
//        verify(companyService, never()).createCompany(any());
//    }
//
//    @Test
//    @DisplayName("Create Company - Service Validation Error")
//    void createCompany_ServiceValidationError() throws Exception {
//        // Given
//        when(companyService.createCompany(any(CompanyRequestDto.class)))
//                .thenThrow(new IllegalArgumentException("Email already exists: test@company.com"));
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/company/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Failed to create company: Email already exists: test@company.com"));
//
//        verify(companyService).createCompany(any(CompanyRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Get Company By ID - Success")
//    void getCompanyById_Success() throws Exception {
//        // Given
//        when(companyService.getCompanyById("TE001")).thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/company/getById/TE001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Company retrieved successfully"))
//                .andExpect(jsonPath("$.data.companyId").value("TE001"));
//
//        verify(companyService).getCompanyById("TE001");
//    }
//
//    @Test
//    @DisplayName("Get Company By ID - Not Found")
//    void getCompanyById_NotFound() throws Exception {
//        // Given
//        when(companyService.getCompanyById("TE001"))
//                .thenThrow(new RuntimeException("Company with ID TE001 not found."));
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/company/getById/TE001"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Failed to retrieve company: Company with ID TE001 not found."));
//
//        verify(companyService).getCompanyById("TE001");
//    }
//
//    @Test
//    @DisplayName("Get All Companies - Success")
//    void getAllCompanies_Success() throws Exception {
//        // Given
//        List<CompanyResponseDto> companies = Arrays.asList(testResponse);
//        when(companyService.getAllCompanies()).thenReturn(companies);
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/company/getAll"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Companies retrieved successfully"))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data[0].companyId").value("TE001"));
//
//        verify(companyService).getAllCompanies();
//    }
//
//    @Test
//    @DisplayName("Get All Companies - Empty List")
//    void getAllCompanies_EmptyList() throws Exception {
//        // Given
//        when(companyService.getAllCompanies()).thenReturn(Arrays.asList());
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/company/getAll"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("No companies found"))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(companyService).getAllCompanies();
//    }
//
//    @Test
//    @DisplayName("Get All Companies - Server Error")
//    void getAllCompanies_ServerError() throws Exception {
//        // Given
//        when(companyService.getAllCompanies())
//                .thenThrow(new RuntimeException("Database connection failed"));
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/company/getAll"))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Failed to retrieve companies: Database connection failed"));
//
//        verify(companyService).getAllCompanies();
//    }
//
//    @Test
//    @DisplayName("Update Company - Success")
//    void updateCompany_Success() throws Exception {
//        // Given
//        when(companyService.updateCompany(eq("TE001"), any(CompanyRequestDto.class)))
//                .thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(put("/api/v2/company/update/TE001")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Company updated successfully"))
//                .andExpect(jsonPath("$.data.companyId").value("TE001"));
//
//        verify(companyService).updateCompany(eq("TE001"), any(CompanyRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Update Company - Not Found")
//    void updateCompany_NotFound() throws Exception {
//        // Given
//        when(companyService.updateCompany(eq("TE001"), any(CompanyRequestDto.class)))
//                .thenThrow(new RuntimeException("Company with ID TE001 not found."));
//
//        // When & Then
//        mockMvc.perform(put("/api/v2/company/update/TE001")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Failed to update company: Company with ID TE001 not found."));
//
//        verify(companyService).updateCompany(eq("TE001"), any(CompanyRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Delete Company - Success")
//    void deleteCompany_Success() throws Exception {
//        // Given
//        doNothing().when(companyService).deleteCompany("TE001");
//
//        // When & Then
//        mockMvc.perform(delete("/api/v2/company/delete/TE001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Company deleted successfully"));
//
//        verify(companyService).deleteCompany("TE001");
//    }
//
//    @Test
//    @DisplayName("Delete Company - Not Found")
//    void deleteCompany_NotFound() throws Exception {
//        // Given
//        doThrow(new RuntimeException("Company with ID TE001 not found."))
//                .when(companyService).deleteCompany("TE001");
//
//        // When & Then
//        mockMvc.perform(delete("/api/v2/company/delete/TE001"))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Failed to delete company: Company with ID TE001 not found."));
//
//        verify(companyService).deleteCompany("TE001");
//    }
//
//    @Test
//    @DisplayName("Create Company - Invalid JSON")
//    void createCompany_InvalidJson() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/api/v2/company/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("invalid json"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Create Company - Missing Required Fields")
//    void createCompany_MissingFields() throws Exception {
//        // Given - Empty request
//        CompanyRequestDto emptyRequest = new CompanyRequestDto();
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/company/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(emptyRequest)))
//                .andExpect(status().isBadRequest()); // Password validation will fail
//    }
//
//    @Test
//    @DisplayName("Create Company - Valid Password Format")
//    void createCompany_ValidPasswordFormat() throws Exception {
//        // Given - Strong password
//        CompanyRequestDto strongPasswordRequest = TestDataBuilder.companyRequest()
//                .companyName("Test Company")
//                .companyEmailId("test@company.com")
//                .companyPrefix("TE")
//                .password("StrongPass123!") // Strong password
//                .build();
//
//        when(companyService.createCompany(any(CompanyRequestDto.class)))
//                .thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/company/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(strongPasswordRequest)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true));
//
//        verify(companyService).createCompany(any(CompanyRequestDto.class));
//    }
//}
