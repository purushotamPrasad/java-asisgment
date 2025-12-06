package com.qssence.backend.document_initiation_service.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientMetadataHeadingDto {
    private Long id;
    private String headingName;
    private Integer displayOrder;
    private List<ClientMetadataFieldDto> metadataFields;
}
