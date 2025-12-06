package com.qssence.backend.document_initiation_service.dto.request.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDocumentRequestDto {
    private Long documentTypeId;
    private Long subTypeId;
    private Long classificationId;
    private String documentName;
    private Map<String, Object> metadataValues; // fieldName -> value mapping
    private String templatePath; // uploaded template file path
}
