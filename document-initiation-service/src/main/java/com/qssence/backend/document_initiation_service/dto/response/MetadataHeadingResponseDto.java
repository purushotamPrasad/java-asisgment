package com.qssence.backend.document_initiation_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ Don't include null fields in JSON response
public class MetadataHeadingResponseDto {
    private Long id;
    private String documentTypeName;
    private String subTypeName;
    private String classificationName;
    private String headingName;
    private Integer displayOrder;
    private List<MetadataFieldResponseDto> metadataFields;
} 