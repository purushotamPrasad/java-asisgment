package com.qssence.backend.subscription_panel.controller;

import com.qssence.backend.subscription_panel.dto.request.CompanyRequestDto;
import com.qssence.backend.subscription_panel.dto.response.CompanyResponseDto;
import com.qssence.backend.subscription_panel.exception.ApiResponse;
import com.qssence.backend.subscription_panel.service.CompanyService;  // Use the interface
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/company")
public class CompanyController {

    private final CompanyService companyService;

    // Constructor-based Dependency Injection
    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> createCompany(@Valid @RequestBody CompanyRequestDto companyRequestDto) {
        ApiResponse<CompanyResponseDto> response = new ApiResponse<>();
        try {
            // Validate password format explicitly (though @Valid already enforces it)
            String password = companyRequestDto.getPassword();
            if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                throw new IllegalArgumentException("Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character");
            }

            // Pass the validated DTO to the service
            CompanyResponseDto companyResponseDto = companyService.createCompany(companyRequestDto);
            response.setSuccess(true);
            response.setMessage("Company created successfully");
            response.setData(companyResponseDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle password validation errors
            response.setSuccess(false);
            response.setMessage("Validation error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Handle other errors
            response.setSuccess(false);
            response.setMessage("Failed to create company: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/getById/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> getCompanyById(@PathVariable String companyId) {
        ApiResponse<CompanyResponseDto> response = new ApiResponse<>();
        try {
            CompanyResponseDto companyResponseDto = companyService.getCompanyById(companyId);
            response.setSuccess(true);
            response.setMessage("Company retrieved successfully");
            response.setData(companyResponseDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to retrieve company: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<CompanyResponseDto>>> getAllCompanies() {
        ApiResponse<List<CompanyResponseDto>> response = new ApiResponse<>();
        try {
            List<CompanyResponseDto> companies = companyService.getAllCompanies();
            if (!companies.isEmpty()) {
                response.setSuccess(true);
                response.setMessage("Companies retrieved successfully");
                response.setData(companies);
            } else {
                response.setSuccess(true);
                response.setMessage("No companies found");
                response.setData(companies);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to retrieve companies: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> updateCompany(
            @PathVariable String companyId,
            @RequestBody CompanyRequestDto companyRequestDto) {
        ApiResponse<CompanyResponseDto> response = new ApiResponse<>();
        try {
            CompanyResponseDto companyResponseDto = companyService.updateCompany(companyId, companyRequestDto);
            response.setSuccess(true);
            response.setMessage("Company updated successfully");
            response.setData(companyResponseDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to update company: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{companyId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable String companyId) {
        ApiResponse<Void> response = new ApiResponse<>();
        try {
            companyService.deleteCompany(companyId);
            response.setSuccess(true);
            response.setMessage("Company deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to delete company: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

