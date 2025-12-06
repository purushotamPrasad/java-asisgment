package com.qssence.backend.document_initiation_service.controller;

import com.qssence.backend.document_initiation_service.dto.request.document.DocumentFlowRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.document.DocumentFlowResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.DocumentFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/document-flows")
@RequiredArgsConstructor
public class DocumentFlowController {

    private final DocumentFlowService documentFlowService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DocumentFlowResponseDto>> create(@RequestBody DocumentFlowRequestDto requestDto) {
        DocumentFlowResponseDto created = documentFlowService.createDocumentFlow(requestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Document flow created", created));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<DocumentFlowResponseDto>>> getAll() {
        List<DocumentFlowResponseDto> list = documentFlowService.getAllDocumentFlows();
        return ResponseEntity.ok(new ApiResponse<>(true, "Document flows fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentFlowResponseDto>> getById(@PathVariable Long id) {
        DocumentFlowResponseDto dto = documentFlowService.getDocumentFlowById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Document flow fetched", dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentFlowResponseDto>> update(@PathVariable Long id, @RequestBody DocumentFlowRequestDto requestDto) {
        DocumentFlowResponseDto updated = documentFlowService.updateDocumentFlow(id, requestDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Document flow updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        documentFlowService.deleteDocumentFlow(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Document flow deleted", null));
    }
}
