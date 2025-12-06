package com.qssence.backend.document_initiation_service.controller.global;

import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowConnectivityRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowConnectivityResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.WorkflowConnectivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow-connectivity")
@RequiredArgsConstructor
public class WorkflowConnectivityController {

    private final WorkflowConnectivityService connectivityService;

    /**
     * Create workflow connectivity
     */
    @PostMapping("/create")
    public ApiResponse<WorkflowConnectivityResponseDto> create(
            @Valid @RequestBody WorkflowConnectivityRequestDto request) {
        return ApiResponse.success(
                "Workflow connectivity created successfully",
                connectivityService.createWorkflowConnectivity(request)
        );
    }

    /**
     * Get workflow connectivity by workflowId
     */
    @GetMapping("/{workflowId}")
    public ApiResponse<WorkflowConnectivityResponseDto> getByWorkflowId(
            @PathVariable Long workflowId) {
        return ApiResponse.success(
                "Workflow connectivity fetched successfully",
                connectivityService.getWorkflowConnectivityByWorkflowId(workflowId)
        );
    }

    /**
     * Get all workflow connectivity
     */
    @GetMapping("/getAll")
    public ApiResponse<List<WorkflowConnectivityResponseDto>> getAll() {
        return ApiResponse.success(
                "All workflow connectivity fetched successfully",
                connectivityService.getAll()
        );
    }

    /**
     * Update workflow connectivity by workflowId
     */
    @PutMapping("/{workflowId}")
    public ApiResponse<WorkflowConnectivityResponseDto> update(
            @PathVariable Long workflowId,
            @Valid @RequestBody WorkflowConnectivityRequestDto request) {
        return ApiResponse.success(
                "Workflow connectivity updated successfully",
                connectivityService.updateByWorkflowId(workflowId, request)
        );
    }

    /**
     * Delete workflow connectivity by workflowId
     */
    @DeleteMapping("/{workflowId}")
    public ApiResponse<Void> delete(@PathVariable Long workflowId) {
        connectivityService.deleteByWorkflowId(workflowId);
        return ApiResponse.success(
                "Workflow connectivity deleted successfully",
                null
        );
    }
}
