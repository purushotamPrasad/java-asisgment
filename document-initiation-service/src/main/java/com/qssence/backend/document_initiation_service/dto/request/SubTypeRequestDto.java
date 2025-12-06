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
public class SubTypeRequestDto {

    private Long id;
    private String name;
    private List<ClassificationRequestDto> classifications;
}
