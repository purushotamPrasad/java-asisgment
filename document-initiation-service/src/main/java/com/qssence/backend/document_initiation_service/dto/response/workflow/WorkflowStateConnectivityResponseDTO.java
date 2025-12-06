package com.qssence.backend.document_initiation_service.dto.response.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStateConnectivityResponseDTO {
    private Long stateId;
    private String stateName;
    private Boolean electronicSignature;

    private List<StateActivityResponseDto> stateActivities;   // master activities of state
    private List<ProcessActivityResponseDTO> processActivities;
    private List<WorkflowCompleteResponseDTO> workflowCompletes;
}
