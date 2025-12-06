package com.qssence.backend.authservice.controller.Key;

import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.Key.ImportedLicenseDto;
import com.qssence.backend.authservice.service.Key.LicenseImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/license")
@RequiredArgsConstructor
public class AdminLicenseController {

    private final LicenseImportService licenseImportService;

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importLicense(@RequestParam String key) {
        try {
            String message = licenseImportService.importLicense(key);
            return ResponseEntity.ok(
                    ApiResponse.<String>builder()
                            .success(true)
                            .message("License imported successfully.")
                            .data(message)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<String>builder()
                            .success(false)
                            .message("Invalid license key format.")
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<String>builder()
                            .success(false)
                            .message("Error importing license: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }


//    @GetMapping("/{licenseId}")
//    public ResponseEntity<ApiResponse<ImportedLicenseDto>> getLicenseById(@PathVariable Long licenseId) {
//        try {
//            ImportedLicenseDto licenseDto = licenseImportService.getImportedLicenseById(licenseId);
//            return ResponseEntity.ok(
//                    ApiResponse.<ImportedLicenseDto>builder()
//                            .success(true)
//                            .message("License fetched successfully.")
//                            .data(licenseDto)
//                            .build()
//            );
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    ApiResponse.<ImportedLicenseDto>builder()
//                            .success(false)
//                            .message("License not found: " + e.getMessage())
//                            .data(null)
//                            .build()
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    ApiResponse.<ImportedLicenseDto>builder()
//                            .success(false)
//                            .message("Error fetching license: " + e.getMessage())
//                            .data(null)
//                            .build()
//            );
//        }
//    }

    // ✅ New: Get all imported licenses
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ImportedLicenseDto>>> getAllLicenses() {
        try {
            List<ImportedLicenseDto> licenses = licenseImportService.getAllImportedLicenses();
            return ResponseEntity.ok(
                    ApiResponse.<List<ImportedLicenseDto>>builder()
                            .success(true)
                            .message("All licenses fetched successfully.")
                            .data(licenses)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<ImportedLicenseDto>>builder()
                            .success(false)
                            .message("Error fetching licenses: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

}
