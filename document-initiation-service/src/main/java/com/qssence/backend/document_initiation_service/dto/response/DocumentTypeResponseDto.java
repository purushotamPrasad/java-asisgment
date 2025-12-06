package com.qssence.backend.document_initiation_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTypeResponseDto {
    private Long id;
    private String name;
    private List<SubTypeResponseDto> subTypes;

}
