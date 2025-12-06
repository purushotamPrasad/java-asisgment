package com.qssence.backend.document_initiation_service.controller;

import com.qssence.backend.document_initiation_service.dto.request.HeadingWithFieldsDto;
import com.qssence.backend.document_initiation_service.dto.request.MetadataBulkStructureRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.MetadataBulkStructureResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.MetadataHeadingResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataService metadataService;

    @PostMapping("/bulk-create")
    public ResponseEntity<ApiResponse<MetadataBulkStructureResponseDto>> bulkCreate(@RequestBody MetadataBulkStructureRequestDto dto) {
        try {
            MetadataBulkStructureResponseDto data = metadataService.bulkCreateMetadata(dto);
            return ResponseEntity.ok(ApiResponse.<MetadataBulkStructureResponseDto>builder()
                    .success(true)
                    .message("Bulk metadata structure created successfully")
                    .data(data)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<MetadataBulkStructureResponseDto>builder()
                    .success(false)
                    .message("Failed to create bulk metadata structure: " + e.getMessage())
                    .build());
        }
    }

    // ✅ Put specific routes BEFORE path variable routes
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MetadataHeadingResponseDto>>> getAll(
            @RequestParam(required = false) Long documentTypeId,
            @RequestParam(required = false) Long subTypeId,
            @RequestParam(required = false) Long classificationId
    ) {
        try {
            List<MetadataHeadingResponseDto> data = metadataService.getAll(documentTypeId, subTypeId, classificationId);
            return ResponseEntity.ok(ApiResponse.<List<MetadataHeadingResponseDto>>builder()
                    .success(true).message("Fetched successfully").data(data).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<List<MetadataHeadingResponseDto>>builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    // ✅ Path variable route comes AFTER specific routes
    @GetMapping("/{headingId}")
    public ResponseEntity<ApiResponse<MetadataHeadingResponseDto>> getById(@PathVariable Long headingId) {
        try {
            MetadataHeadingResponseDto data = metadataService.getById(headingId);
            return ResponseEntity.ok(ApiResponse.<MetadataHeadingResponseDto>builder()
                    .success(true).message("Fetched successfully").data(data).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<MetadataHeadingResponseDto>builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @PutMapping("/{headingId}")
    public ResponseEntity<ApiResponse<MetadataHeadingResponseDto>> update(
            @PathVariable Long headingId,
            @RequestBody HeadingWithFieldsDto headingDto
    ) {
        try {
            MetadataHeadingResponseDto data = metadataService.updateHeading(headingId, headingDto);
            return ResponseEntity.ok(ApiResponse.<MetadataHeadingResponseDto>builder()
                    .success(true).message("Updated successfully").data(data).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<MetadataHeadingResponseDto>builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @DeleteMapping("/{headingId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long headingId) {
        try {
            metadataService.deleteHeading(headingId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true).message("Deleted successfully").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false).message(e.getMessage()).build());
        }
    }
}
