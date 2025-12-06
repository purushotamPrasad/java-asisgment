package com.qssence.backend.document_initiation_service.controller;

import com.qssence.backend.document_initiation_service.dto.request.DocumentTypeRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.DocumentTypeResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDto>> createDocumentType(
            @RequestBody DocumentTypeRequestDto request) {
        try {
            DocumentTypeResponseDto response = documentTypeService.createDocumentType(request);
            ApiResponse<DocumentTypeResponseDto> apiResponse = ApiResponse.<DocumentTypeResponseDto>builder()
                    .success(true)
                    .message("Document type created successfully")
                    .data(response)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<DocumentTypeResponseDto> apiResponse = ApiResponse.<DocumentTypeResponseDto>builder()
                    .success(false)
                    .message("Failed to create document type: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DocumentTypeResponseDto>>> getAllDocumentTypes() {
        try {
            List<DocumentTypeResponseDto> data = documentTypeService.getAllDocumentTypes();
            ApiResponse<List<DocumentTypeResponseDto>> apiResponse = ApiResponse.<List<DocumentTypeResponseDto>>builder()
                    .success(true)
                    .message("Fetched all document types successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<List<DocumentTypeResponseDto>> apiResponse = ApiResponse.<List<DocumentTypeResponseDto>>builder()
                    .success(false)
                    .message("Failed to fetch document types: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDto>> getDocumentTypeById(@PathVariable Long id) {
        try {
            DocumentTypeResponseDto data = documentTypeService.getDocumentTypeById(id);
            ApiResponse<DocumentTypeResponseDto> apiResponse = ApiResponse.<DocumentTypeResponseDto>builder()
                    .success(true)
                    .message("Fetched document type by ID: " + id)
                    .data(data)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<DocumentTypeResponseDto> apiResponse = ApiResponse.<DocumentTypeResponseDto>builder()
                    .success(false)
                    .message("Failed to fetch document type: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponseDto>> updateDocumentType(
            @PathVariable Long id,
            @RequestBody DocumentTypeRequestDto requestDto) {
        try {
            DocumentTypeResponseDto data = documentTypeService.updateDocumentType(id, requestDto);
            ApiResponse<DocumentTypeResponseDto> apiResponse = ApiResponse.<DocumentTypeResponseDto>builder()
                    .success(true)
                    .message("Document type updated successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<DocumentTypeResponseDto> apiResponse = ApiResponse.<DocumentTypeResponseDto>builder()
                    .success(false)
                    .message("Failed to update document type: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocumentType(@PathVariable Long id) {
        try {
            documentTypeService.deleteDocumentType(id);
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .success(true)
                    .message("Document type deleted successfully")
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete document type: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}