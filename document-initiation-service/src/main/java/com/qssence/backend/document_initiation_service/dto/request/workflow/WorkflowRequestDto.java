package com.qssence.backend.document_initiation_service.dto.request.workflow;

import com.qssence.backend.document_initiation_service.enums.WorkflowType;
import lombok.Data;

@Data
public class WorkflowRequestDto {

    private String workflowName;
    private WorkflowType workflowType;
    private String region;
    private String country;
    private Long plantId;
}
