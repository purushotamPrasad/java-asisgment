package com.qssence.backend.document_initiation_service.controller.global;

import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.impl.workflow.WorkflowServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
public class WorkflowController {

    @Autowired
    private WorkflowServiceImpl workflowService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<WorkflowResponseDto>> createWorkflow(
            @Valid @RequestBody WorkflowRequestDto requestDto) {
        return ResponseEntity.ok(workflowService.createWorkflow(requestDto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<WorkflowResponseDto>>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkflowResponseDto>> getWorkflowById(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflowById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteWorkflowById(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.deleteWorkflowById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkflowResponseDto>> updateWorkflow(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowRequestDto requestDto) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, requestDto));
    }
}
