//package com.qssence.backend.subscription_panel.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.qssence.backend.subscription_panel.dto.request.UserLicenseRequestDto;
//import com.qssence.backend.subscription_panel.dto.response.UserLicenseResponseDto;
//import com.qssence.backend.subscription_panel.service.UserLicenseService;
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
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * UserLicenseController ke liye Integration Tests
// *
// * Ye test class mein hum UserLicenseController ke sabhi endpoints ko test karte hain
// * MockMvc use karte hain HTTP requests simulate karne ke liye
// */
//@WebMvcTest(UserLicenseController.class)
//@DisplayName("UserLicense Controller Tests")
//class UserLicenseControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserLicenseService userLicenseService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private UserLicenseRequestDto testRequest;
//    private UserLicenseResponseDto testResponse;
//
//    @BeforeEach
//    void setUp() {
//        // Har test se pehle ye setup run hota hai
//        testRequest = TestDataBuilder.userLicenseRequest()
//                .companyId(1L)
//                .totalUserAccess(10)
//                .adminAccountAllowed(2)
//                .userAccountAllowed(8)
//                .build();
//
//        testResponse = UserLicenseResponseDto.builder()
//                .licenseId(1L)
//                .companyId(1L)
//                .companyName("Test Company")
//                .licenseKey("test-key")
//                .totalUserAccess(10)
//                .adminAccountAllowed(2)
//                .userAccountAllowed(8)
//                .purchaseDate(LocalDate.now())
//                .expiryDate(LocalDate.now().plusYears(1))
//                .active(true)
//                .expired(false)
//                .build();
//    }
//
//    @Test
//    @DisplayName("Create User License - Success")
//    void createUserLicense_Success() throws Exception {
//        // Given
//        when(userLicenseService.createUserLicense(any(UserLicenseRequestDto.class)))
//                .thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/licenses/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User License created successfully"))
//                .andExpect(jsonPath("$.data.licenseId").value(1))
//                .andExpect(jsonPath("$.data.companyName").value("Test Company"));
//
//        verify(userLicenseService).createUserLicense(any(UserLicenseRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Create User License - Validation Error")
//    void createUserLicense_ValidationError() throws Exception {
//        // Given
//        when(userLicenseService.createUserLicense(any(UserLicenseRequestDto.class)))
//                .thenThrow(new IllegalArgumentException("Company not found"));
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/licenses/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Company not found"));
//
//        verify(userLicenseService).createUserLicense(any(UserLicenseRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Create User License - Server Error")
//    void createUserLicense_ServerError() throws Exception {
//        // Given
//        when(userLicenseService.createUserLicense(any(UserLicenseRequestDto.class)))
//                .thenThrow(new RuntimeException("Database connection failed"));
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/licenses/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Unexpected error while creating license"));
//
//        verify(userLicenseService).createUserLicense(any(UserLicenseRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Get All User Licenses - Success")
//    void getAllUserLicenses_Success() throws Exception {
//        // Given
//        List<UserLicenseResponseDto> licenses = Arrays.asList(testResponse);
//        when(userLicenseService.getAllUserLicenses()).thenReturn(licenses);
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/licenses/getAll"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("All User Licenses retrieved successfully"))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data[0].licenseId").value(1));
//
//        verify(userLicenseService).getAllUserLicenses();
//    }
//
//    @Test
//    @DisplayName("Get All User Licenses - Empty List")
//    void getAllUserLicenses_EmptyList() throws Exception {
//        // Given
//        when(userLicenseService.getAllUserLicenses()).thenReturn(Arrays.asList());
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/licenses/getAll"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("No User Licenses found"))
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data").isEmpty());
//
//        verify(userLicenseService).getAllUserLicenses();
//    }
//
//    @Test
//    @DisplayName("Get User License By ID - Success")
//    void getUserLicenseById_Success() throws Exception {
//        // Given
//        when(userLicenseService.getUserLicenseById(1L)).thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/licenses/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User License retrieved successfully"))
//                .andExpect(jsonPath("$.data.licenseId").value(1));
//
//        verify(userLicenseService).getUserLicenseById(1L);
//    }
//
//    @Test
//    @DisplayName("Get User License By ID - Not Found")
//    void getUserLicenseById_NotFound() throws Exception {
//        // Given
//        when(userLicenseService.getUserLicenseById(1L))
//                .thenThrow(new RuntimeException("User License not found"));
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/licenses/1"))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Unexpected error while fetching license by ID"));
//
//        verify(userLicenseService).getUserLicenseById(1L);
//    }
//
//    @Test
//    @DisplayName("Decode License Key - Success")
//    void decodeLicenseKey_Success() throws Exception {
//        // Given
//        String licenseKey = "test-license-key";
//        when(userLicenseService.decodeLicenseKey(licenseKey)).thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/licenses/decode/{key}", licenseKey))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("License key decoded successfully"))
//                .andExpect(jsonPath("$.data.licenseId").value(1));
//
//        verify(userLicenseService).decodeLicenseKey(licenseKey);
//    }
//
//    @Test
//    @DisplayName("Decode License Key - Invalid Key")
//    void decodeLicenseKey_InvalidKey() throws Exception {
//        // Given
//        String licenseKey = "invalid-key";
//        when(userLicenseService.decodeLicenseKey(licenseKey))
//                .thenThrow(new IllegalArgumentException("Invalid license key format"));
//
//        // When & Then
//        mockMvc.perform(get("/api/v2/licenses/decode/{key}", licenseKey))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid license key format"));
//
//        verify(userLicenseService).decodeLicenseKey(licenseKey);
//    }
//
//    @Test
//    @DisplayName("Update User License - Success")
//    void updateUserLicense_Success() throws Exception {
//        // Given
//        when(userLicenseService.updateUserLicense(eq(1L), any(UserLicenseRequestDto.class)))
//                .thenReturn(testResponse);
//
//        // When & Then
//        mockMvc.perform(put("/api/v2/licenses/update/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User License updated successfully. Old key disabled, new key generated."))
//                .andExpect(jsonPath("$.data.licenseId").value(1));
//
//        verify(userLicenseService).updateUserLicense(eq(1L), any(UserLicenseRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Update User License - Not Found")
//    void updateUserLicense_NotFound() throws Exception {
//        // Given
//        when(userLicenseService.updateUserLicense(eq(1L), any(UserLicenseRequestDto.class)))
//                .thenThrow(new RuntimeException("User License not found"));
//
//        // When & Then
//        mockMvc.perform(put("/api/v2/licenses/update/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(testRequest)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Unexpected error while updating license"));
//
//        verify(userLicenseService).updateUserLicense(eq(1L), any(UserLicenseRequestDto.class));
//    }
//
//    @Test
//    @DisplayName("Delete User License - Success")
//    void deleteUserLicense_Success() throws Exception {
//        // Given
//        doNothing().when(userLicenseService).deleteUserLicense(1L);
//
//        // When & Then
//        mockMvc.perform(delete("/api/v2/licenses/delete/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("User License marked as inactive successfully"));
//
//        verify(userLicenseService).deleteUserLicense(1L);
//    }
//
//    @Test
//    @DisplayName("Delete User License - Not Found")
//    void deleteUserLicense_NotFound() throws Exception {
//        // Given
//        doThrow(new RuntimeException("User License not found"))
//                .when(userLicenseService).deleteUserLicense(1L);
//
//        // When & Then
//        mockMvc.perform(delete("/api/v2/licenses/delete/1"))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Unexpected error while deleting license"));
//
//        verify(userLicenseService).deleteUserLicense(1L);
//    }
//
//    @Test
//    @DisplayName("Create User License - Invalid JSON")
//    void createUserLicense_InvalidJson() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/api/v2/licenses/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("invalid json"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @DisplayName("Create User License - Missing Required Fields")
//    void createUserLicense_MissingFields() throws Exception {
//        // Given - Empty request
//        UserLicenseRequestDto emptyRequest = new UserLicenseRequestDto();
//
//        // When & Then
//        mockMvc.perform(post("/api/v2/licenses/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(emptyRequest)))
//                .andExpect(status().isOk()); // Controller doesn't validate required fields, service will handle it
//    }
//}
