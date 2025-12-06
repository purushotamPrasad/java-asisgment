package com.qssence.backend.document_initiation_service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateResponseDto {
    private Long id;
    private String documentTypeName;
    private String subTypeName;
    private String classificationName;
    private String templateFile;
    private String s3Url;
    private LocalDateTime uploadedAt;
}
