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
public class DynamicDataFieldResponseDTO {
    private Long fieldId;
    private String fieldName;
    private String fieldType;
    private String width;
    private Long minLength;
    private Long maxLength;
    private String selectLine;
    private List<String> options;
}
