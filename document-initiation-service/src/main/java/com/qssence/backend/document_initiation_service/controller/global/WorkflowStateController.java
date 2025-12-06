package com.qssence.backend.document_initiation_service.controller.global;

import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowStateRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowStateResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.WorkflowStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/globalWorkflows/{workflowId}/states")
public class WorkflowStateController {

    @Autowired
    private WorkflowStateService stateService;

    // Create states
    @PostMapping("/create")
    public ApiResponse<List<WorkflowStateResponseDto>> createStates(
            @PathVariable Long workflowId,
            @RequestBody List<WorkflowStateRequestDto> requestDtos) {
        return stateService.createStates(workflowId, requestDtos);
    }

    // Update states
    @PutMapping("/update")
    public ApiResponse<List<WorkflowStateResponseDto>> updateStates(
            @PathVariable Long workflowId,
            @RequestBody List<WorkflowStateRequestDto> requestDtos) {
        return stateService.updateStatesByWorkflowId(workflowId, requestDtos);
    }

    // Get states by workflowId
    @GetMapping("/getAll")
    public ApiResponse<List<WorkflowStateResponseDto>> getStatesByWorkflowId(
            @PathVariable Long workflowId) {
        return stateService.getStatesByWorkflowId(workflowId);
    }

    // Delete state by stateId
    @DeleteMapping("/{stateId}")
    public ApiResponse<String> deleteState(@PathVariable Long stateId) {
        return stateService.deleteState(stateId);
    }
}
