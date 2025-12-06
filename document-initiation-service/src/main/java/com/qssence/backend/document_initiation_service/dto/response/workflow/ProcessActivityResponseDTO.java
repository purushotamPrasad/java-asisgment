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
public class ProcessActivityResponseDTO {
    private Long stateActivityId;   // mapped to master state activity
    private String activityName;    // fetched from master state activity
    private List<DynamicDataFieldResponseDTO> fields;
}
