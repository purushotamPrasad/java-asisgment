package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;

import java.util.List;

public interface WorkflowService {

    ApiResponse<WorkflowResponseDto> createWorkflow(WorkflowRequestDto requestDto);

    ApiResponse<List<WorkflowResponseDto>> getAllWorkflows();

    ApiResponse<WorkflowResponseDto> getWorkflowById(Long id);

    ApiResponse<String> deleteWorkflowById(Long id);

    ApiResponse<WorkflowResponseDto> updateWorkflow(Long id, WorkflowRequestDto requestDto);


}
