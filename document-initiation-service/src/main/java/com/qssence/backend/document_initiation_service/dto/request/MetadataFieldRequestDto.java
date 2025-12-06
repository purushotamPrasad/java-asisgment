package com.qssence.backend.document_initiation_service.dto.request;

import com.qssence.backend.document_initiation_service.enums.FieldType;
import com.qssence.backend.document_initiation_service.enums.FieldWidth;
import lombok.Data;

import java.util.List;

@Data
public class MetadataFieldRequestDto {
    private Long metadataHeadingId; // Reference to the heading this field belongs to
    private String fieldName;
    private FieldType fieldType;
    private FieldWidth width;
    private Boolean required;
    private Integer displayOrder;

    // Dynamic fields
    private Long minLength;
    private Long maxLength;
    private String selectLine; // for text
    private List<String> options; // for dropdown, checkbox, radio
}
