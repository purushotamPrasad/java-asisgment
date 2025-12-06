package com.qssence.backend.document_initiation_service.dto.response.workflow;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowConnectivityResponseDto {

    private Long workflowId;
    private String workflowName;
    private LocalDate revisionDate;
    private List<WorkflowStateConnectivityResponseDTO> states;
}

