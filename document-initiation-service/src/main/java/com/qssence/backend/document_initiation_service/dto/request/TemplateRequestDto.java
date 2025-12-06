package com.qssence.backend.document_initiation_service.dto.request;

import lombok.Data;

@Data
public class TemplateRequestDto {

    private Long documentTypeId;
    private Long subTypeId;
    private Long classificationId;
    private String templateFile;


}
