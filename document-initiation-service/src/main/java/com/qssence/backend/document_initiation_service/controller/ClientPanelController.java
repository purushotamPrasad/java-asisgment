package com.qssence.backend.document_initiation_service.controller;

import com.qssence.backend.document_initiation_service.dto.request.client.ClientDocumentRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.client.*;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.ClientPanelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow CORS for client panel
public class ClientPanelController {

    private final ClientPanelService clientPanelService;

    /**
     * Get all document types with their subtypes and classifications
     * This is the first API called when user opens the client panel
     */
    @GetMapping("/document-types")
    public ResponseEntity<ApiResponse<List<ClientDocumentTypeDto>>> getAllDocumentTypes() {
        try {
            List<ClientDocumentTypeDto> documentTypes = clientPanelService.getAllDocumentTypes();
            return ResponseEntity.ok(new ApiResponse<>(true, "Document types fetched successfully", documentTypes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch document types: " + e.getMessage(), null));
        }
    }

    /**
     * Get complete document type data with subtypes and classifications
     * Called when user selects a document type - returns everything in one call
     */
    @GetMapping("/document-types/{documentTypeId}")
    public ResponseEntity<ApiResponse<ClientDocumentTypeDto>> getDocumentTypeWithSubTypesAndClassifications(@PathVariable Long documentTypeId) {
        try {
            ClientDocumentTypeDto documentType = clientPanelService.getDocumentTypeWithSubTypesAndClassifications(documentTypeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Document type with subtypes and classifications fetched successfully", documentType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch document type data: " + e.getMessage(), null));
        }
    }

    /**
     * Get metadata fields and workflow states based on document type, subtype, and classification
     * This is the main API that returns all the form fields and workflow states
     * Called when user has selected document type, subtype, and classification
     */
    @GetMapping("/metadata-workflow")
    public ResponseEntity<ApiResponse<ClientMetadataWorkflowResponseDto>> getMetadataAndWorkflow(
            @RequestParam Long documentTypeId,
            @RequestParam Long subTypeId,
            @RequestParam Long classificationId) {
        try {
            ClientMetadataWorkflowResponseDto response = clientPanelService.getMetadataAndWorkflow(
                    documentTypeId, subTypeId, classificationId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Metadata and workflow fetched successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch metadata and workflow: " + e.getMessage(), null));
        }
    }

    /**
     * Create a new document record with user-filled data
     * This API stores the user's filled form data in the database
     */
    @PostMapping("/documents")
    public ResponseEntity<ApiResponse<Long>> createDocumentRecord(@RequestBody ClientDocumentRequestDto requestDto) {
        try {
            Long documentId = clientPanelService.createDocumentRecord(requestDto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Document created successfully", documentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to create document: " + e.getMessage(), null));
        }
    }

    /**
     * Get user's created documents
     * This API returns all documents created by a specific user
     */
    @GetMapping("/documents/{userId}")
    public ResponseEntity<ApiResponse<List<ClientDocumentRecordDto>>> getUserDocuments(@PathVariable Long userId) {
        try {
            List<ClientDocumentRecordDto> documents = clientPanelService.getUserDocuments(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "User documents fetched successfully", documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch user documents: " + e.getMessage(), null));
        }
    }
}