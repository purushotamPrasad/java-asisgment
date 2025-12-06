package com.qssence.backend.document_initiation_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTypeRequestDto {

    private String name;
    private List<SubTypeRequestDto> subTypes;

}
