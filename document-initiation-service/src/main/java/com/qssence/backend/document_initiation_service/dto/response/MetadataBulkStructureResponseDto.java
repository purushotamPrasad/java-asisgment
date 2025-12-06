package com.qssence.backend.document_initiation_service.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class MetadataBulkStructureResponseDto {
    private Long documentTypeId;
    private String documentTypeName; // ✅ Added for clarity
    private Long subTypeId;
    private String subTypeName; // ✅ Added for clarity
    private Long classificationId;
    private String classificationName; // ✅ Added for clarity
    private List<MetadataHeadingResponseDto> headings;  // same as you already created
}
