package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowConnectivityRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowConnectivityResponseDto;
import java.util.List;

public interface WorkflowConnectivityService {

    WorkflowConnectivityResponseDto createWorkflowConnectivity(WorkflowConnectivityRequestDto requestDto);


    List<WorkflowConnectivityResponseDto> getAll();

    WorkflowConnectivityResponseDto updateByWorkflowId(Long workflowId, WorkflowConnectivityRequestDto requestDto);

    void deleteByWorkflowId(Long workflowId);

    WorkflowConnectivityResponseDto getWorkflowConnectivityByWorkflowId(Long workflowId);
}