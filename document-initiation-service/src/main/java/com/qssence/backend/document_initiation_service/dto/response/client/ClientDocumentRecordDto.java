package com.qssence.backend.document_initiation_service.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDocumentRecordDto {
    private Long id;
    private String documentName;
    private String documentTypeName;
    private String subTypeName;
    private String classificationName;
    private String workflowName;
    private String currentState;
    private String templatePath;
    private String metadataValues; // JSON string
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
}
