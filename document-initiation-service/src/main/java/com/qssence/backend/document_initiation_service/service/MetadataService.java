package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.MetadataBulkStructureRequestDto;
import com.qssence.backend.document_initiation_service.dto.request.HeadingWithFieldsDto;
import com.qssence.backend.document_initiation_service.dto.response.MetadataBulkStructureResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.MetadataHeadingResponseDto;

import java.util.List;

public interface MetadataService {
    MetadataBulkStructureResponseDto bulkCreateMetadata(MetadataBulkStructureRequestDto dto);
    MetadataHeadingResponseDto getById(Long headingId);
    List<MetadataHeadingResponseDto> getAll(Long documentTypeId, Long subTypeId, Long classificationId);
    MetadataHeadingResponseDto updateHeading(Long headingId, HeadingWithFieldsDto headingDto);
    void deleteHeading(Long headingId);
}
