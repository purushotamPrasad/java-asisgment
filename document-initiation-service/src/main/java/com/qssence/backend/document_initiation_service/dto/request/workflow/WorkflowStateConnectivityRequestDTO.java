package com.qssence.backend.document_initiation_service.dto.request.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStateConnectivityRequestDTO {
    private Long stateId;
    private Boolean electronicSignature;

    private List<ProcessActivityRequestDTO> processActivities;
    private List<WorkflowCompleteRequestDTO> workflowCompletes;
}
