package com.qssence.backend.subscription_panel.controller;

import com.qssence.backend.subscription_panel.dto.request.UserLicenseRequestDto;
import com.qssence.backend.subscription_panel.dto.response.UserLicenseResponseDto;
import com.qssence.backend.subscription_panel.exception.ApiResponse;
import com.qssence.backend.subscription_panel.service.UserLicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/licenses")
@RequiredArgsConstructor
public class UserLicenseController {

    private final UserLicenseService userLicenseService;

    // ✅ Create License
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserLicenseResponseDto>> createUserLicense(
            @RequestBody UserLicenseRequestDto request) {
        try {
            UserLicenseResponseDto response = userLicenseService.createUserLicense(request);
            return ResponseEntity.ok(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(true)
                    .message("User License created successfully")
                    .data(response)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message("Unexpected error while creating license")
                    .build());
        }
    }

    // ✅ Get All Licenses (active, inactive, expired sab dikhayega)
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<UserLicenseResponseDto>>> getAllUserLicenses() {
        try {
            List<UserLicenseResponseDto> licenses = userLicenseService.getAllUserLicenses();
            return ResponseEntity.ok(ApiResponse.<List<UserLicenseResponseDto>>builder()
                    .success(true)
                    .message(licenses.isEmpty() ? "No User Licenses found" : "All User Licenses retrieved successfully")
                    .data(licenses)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<List<UserLicenseResponseDto>>builder()
                    .success(false)
                    .message("Unexpected error while fetching licenses")
                    .build());
        }
    }

    // ✅ Get License by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserLicenseResponseDto>> getUserLicenseById(@PathVariable Long id) {
        try {
            UserLicenseResponseDto license = userLicenseService.getUserLicenseById(id);
            return ResponseEntity.ok(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(true)
                    .message("User License retrieved successfully")
                    .data(license)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message("Unexpected error while fetching license by ID")
                    .build());
        }
    }

    // ✅ Decode License Key
    @GetMapping("/decode/{key}")
    public ResponseEntity<ApiResponse<UserLicenseResponseDto>> decodeLicenseKey(@PathVariable String key) {
        try {
            UserLicenseResponseDto license = userLicenseService.decodeLicenseKey(key);
            return ResponseEntity.ok(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(true)
                    .message("License key decoded successfully")
                    .data(license)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message("Unexpected error while decoding license key")
                    .build());
        }
    }

    // ✅ Update License
    @PutMapping("/update/{licenseId}")
    public ResponseEntity<ApiResponse<UserLicenseResponseDto>> updateUserLicense(
            @PathVariable Long licenseId,
            @RequestBody UserLicenseRequestDto request) {
        try {
            UserLicenseResponseDto response = userLicenseService.updateUserLicense(licenseId, request);
            return ResponseEntity.ok(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(true)
                    .message("User License updated successfully. Old key disabled, new key generated.")
                    .data(response)
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<UserLicenseResponseDto>builder()
                    .success(false)
                    .message("Unexpected error while updating license")
                    .build());
        }
    }

    // ✅ Delete (Soft Delete -> Inactive)
    @DeleteMapping("/delete/{licenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserLicense(@PathVariable Long licenseId) {
        try {
            userLicenseService.deleteUserLicense(licenseId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("User License marked as inactive successfully")
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Unexpected error while deleting license")
                    .build());
        }
    }
}
