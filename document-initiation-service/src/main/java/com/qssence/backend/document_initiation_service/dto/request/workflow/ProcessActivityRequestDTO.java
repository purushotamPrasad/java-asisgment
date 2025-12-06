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
public class ProcessActivityRequestDTO {
    private Long stateActivityId;   // link with master StateActivity
    private List<DynamicDataFieldRequestDTO> fields;
}
