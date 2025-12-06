package com.qssence.backend.document_initiation_service.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class HeadingWithFieldsDto {
    private String headingName;
    private Integer headingDisplayOrder;
    private List<MetadataFieldRequestDto> fields;
} 