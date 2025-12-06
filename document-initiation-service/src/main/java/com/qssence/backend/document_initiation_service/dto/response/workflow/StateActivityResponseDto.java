package com.qssence.backend.document_initiation_service.dto.response.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateActivityResponseDto {
    private Long id;
    private String activityName;
}
