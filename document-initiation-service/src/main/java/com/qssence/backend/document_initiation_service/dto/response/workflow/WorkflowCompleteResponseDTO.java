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
public class WorkflowCompleteResponseDTO {
    private Long completeId;
    private String completeName;
    private List<DynamicDataFieldResponseDTO> fields;
}
