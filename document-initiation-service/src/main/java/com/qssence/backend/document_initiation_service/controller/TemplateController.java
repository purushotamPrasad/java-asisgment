package com.qssence.backend.document_initiation_service.controller;

import com.qssence.backend.document_initiation_service.dto.request.TemplateRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.TemplateResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    // ✅ Upload Template
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<TemplateResponseDto>> uploadTemplate(
            @RequestParam Long documentTypeId,
            @RequestParam(required = false) Long subTypeId,
            @RequestParam(required = false) Long classificationId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            TemplateResponseDto dto = templateService.uploadTemplate(documentTypeId, subTypeId, classificationId, file);
            return ResponseEntity.ok(ApiResponse.<TemplateResponseDto>builder()
                    .success(true)
                    .message("Template uploaded successfully")
                    .data(dto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<TemplateResponseDto>builder()
                    .success(false)
                    .message("Failed to upload template: " + e.getMessage())
                    .build());
        }
    }

    // ✅ Update Template
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TemplateResponseDto>> updateTemplate(
            @PathVariable Long id,
            @ModelAttribute TemplateRequestDto dto,
            @RequestParam(required = false) MultipartFile file
    ) {
        try {
            TemplateResponseDto updated = templateService.updateTemplate(id, dto, file);
            return ResponseEntity.ok(ApiResponse.<TemplateResponseDto>builder()
                    .success(true)
                    .message("Template updated successfully")
                    .data(updated)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<TemplateResponseDto>builder()
                    .success(false)
                    .message("Failed to update template: " + e.getMessage())
                    .build());
        }
    }

    // ✅ Delete Template
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable Long id) {
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Template deleted successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete template: " + e.getMessage())
                    .build());
        }
    }

    // ✅ Get All Templates
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<TemplateResponseDto>>> getAllTemplates() {
        try {
            List<TemplateResponseDto> data = templateService.getAllTemplates();
            return ResponseEntity.ok(ApiResponse.<List<TemplateResponseDto>>builder()
                    .success(true)
                    .message("Fetched all templates successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<List<TemplateResponseDto>>builder()
                    .success(false)
                    .message("Failed to fetch templates: " + e.getMessage())
                    .build());
        }
    }

    // ✅ Get All Templates by DocumentType
    @GetMapping("/by-document-type/{documentTypeId}")
    public ResponseEntity<ApiResponse<List<TemplateResponseDto>>> getTemplatesByDocumentType(@PathVariable Long documentTypeId) {
        try {
            List<TemplateResponseDto> data = templateService.getTemplatesByDocumentType(documentTypeId);
            return ResponseEntity.ok(ApiResponse.<List<TemplateResponseDto>>builder()
                    .success(true)
                    .message("Fetched templates successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<List<TemplateResponseDto>>builder()
                    .success(false)
                    .message("Failed to fetch templates: " + e.getMessage())
                    .build());
        }
    }

    // ✅ Get Single Template by Template ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TemplateResponseDto>> getTemplateById(@PathVariable Long id) {
        try {
            TemplateResponseDto data = templateService.getTemplateById(id);
            return ResponseEntity.ok(ApiResponse.<TemplateResponseDto>builder()
                    .success(true)
                    .message("Fetched template successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<TemplateResponseDto>builder()
                    .success(false)
                    .message("Failed to fetch template: " + e.getMessage())
                    .build());
        }
    }
}
