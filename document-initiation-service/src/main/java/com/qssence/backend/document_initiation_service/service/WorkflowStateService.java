package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowStateRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowStateResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;

import java.util.List;

public interface WorkflowStateService {

  //  ApiResponse<List<WorkflowStateResponseDto>> createState(Long workflowId, WorkflowStateRequestDto requestDto);

    ApiResponse<List<WorkflowStateResponseDto>> createStates(Long workflowId, List<WorkflowStateRequestDto> requestDtos);

    ApiResponse<List<WorkflowStateResponseDto>> getStatesByWorkflowId(Long workflowId);
    ApiResponse<List<WorkflowStateResponseDto>> getAllStates();
    /**
     * Update multiple states of a workflow in a single request
     * @param workflowId - ID of the workflow whose states are being updated
     * @param requestDtos - List of updated state data
     * @return Updated states for the workflow
     */
    ApiResponse<List<WorkflowStateResponseDto>> updateStatesByWorkflowId(
            Long workflowId, List<WorkflowStateRequestDto> requestDtos
    );
    ApiResponse<String> deleteState(Long stateId);
}
