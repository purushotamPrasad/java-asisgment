package com.qssence.backend.document_initiation_service.dto.request.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateActivityRequestDto {
    private Long id;              // for updates
    private String activityName;  // same as entity
}