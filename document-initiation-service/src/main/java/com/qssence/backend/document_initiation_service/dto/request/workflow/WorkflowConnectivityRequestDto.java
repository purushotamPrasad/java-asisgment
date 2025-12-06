package com.qssence.backend.document_initiation_service.dto.request.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowConnectivityRequestDto {

    private Long workflowId; // Workflow select karne ke liye
    // workflow revision date
    private LocalDate revisionDate;
    private List<WorkflowStateConnectivityRequestDTO> states;
}
