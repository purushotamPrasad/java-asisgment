package com.qssence.backend.document_initiation_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qssence.backend.document_initiation_service.enums.FieldType;
import com.qssence.backend.document_initiation_service.enums.FieldWidth;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ Don't include null fields in JSON response
public class MetadataFieldResponseDto {
    private Long id;
    private Long metadataHeadingId;
    private String headingName; // For convenience
    private String fieldName;
    private FieldType fieldType;
    private FieldWidth width;
    private Boolean required;
    private Integer displayOrder;

    // Dynamic fields
    private Long minLength;
    private Long maxLength;
    private String selectLine;
    private List<String> options;
}
