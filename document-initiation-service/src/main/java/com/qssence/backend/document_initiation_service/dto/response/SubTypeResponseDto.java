package com.qssence.backend.document_initiation_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubTypeResponseDto {
    private Long id;
    private String name;
    private List<ClassificationResponseDto> classifications;
}