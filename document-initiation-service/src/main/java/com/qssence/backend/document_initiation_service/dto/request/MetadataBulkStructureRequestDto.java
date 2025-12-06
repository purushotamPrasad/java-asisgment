package com.qssence.backend.document_initiation_service.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class MetadataBulkStructureRequestDto {
    private Long documentTypeId;
    private Long subTypeId;
    private Long classificationId;
    private List<HeadingWithFieldsDto> headings;
} 