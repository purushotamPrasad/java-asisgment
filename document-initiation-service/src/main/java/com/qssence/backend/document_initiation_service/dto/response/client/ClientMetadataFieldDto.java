package com.qssence.backend.document_initiation_service.dto.response.client;

import com.qssence.backend.document_initiation_service.enums.FieldType;
import com.qssence.backend.document_initiation_service.enums.FieldWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientMetadataFieldDto {
    private Long id;
    private String fieldName;
    private FieldType fieldType;
    private FieldWidth width;
    private Boolean required;
    private Integer displayOrder;
    private Long minLength;
    private Long maxLength;
    private String selectLine;
    private List<String> options;
}
