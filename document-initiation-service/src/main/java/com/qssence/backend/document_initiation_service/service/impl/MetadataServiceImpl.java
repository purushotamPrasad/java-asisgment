package com.qssence.backend.document_initiation_service.service.impl;

import com.qssence.backend.document_initiation_service.dto.request.*;
import com.qssence.backend.document_initiation_service.dto.response.MetadataBulkStructureResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.MetadataHeadingResponseDto;
import com.qssence.backend.document_initiation_service.model.*;
import com.qssence.backend.document_initiation_service.repository.*;
import com.qssence.backend.document_initiation_service.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final MetadataHeadingRepository metadataHeadingRepository;
    private final MetadataFieldRepository metadataFieldRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final SubTypeRepository subTypeRepository;
    private final ClassificationRepository classificationRepository;

    @Override
    @Transactional
    public MetadataBulkStructureResponseDto bulkCreateMetadata(MetadataBulkStructureRequestDto dto) {
        DocumentType docType = documentTypeRepository.findById(dto.getDocumentTypeId())
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));
        SubType subType = subTypeRepository.findById(dto.getSubTypeId())
                .orElseThrow(() -> new RuntimeException("SubType not found"));
        Classification classification = classificationRepository.findById(dto.getClassificationId())
                .orElseThrow(() -> new RuntimeException("Classification not found"));

        // ✅ Check if metadata already exists for this combination
        List<MetadataHeading> existingHeadings = metadataHeadingRepository.findByDocumentTypeAndSubTypeAndClassification(
                docType.getId(), subType.getId(), classification.getId());
        
        if (!existingHeadings.isEmpty()) {
            throw new RuntimeException("Metadata already exists for DocumentType: " + docType.getName() + 
                    ", SubType: " + subType.getName() + ", Classification: " + classification.getName() + 
                    ". Please use update method to modify existing metadata or delete existing metadata first.");
        }

        // Check for duplicate heading names in the request
        List<String> headingNames = dto.getHeadings().stream()
                .map(HeadingWithFieldsDto::getHeadingName)
                .collect(Collectors.toList());
        long uniqueCount = headingNames.stream().distinct().count();
        if (headingNames.size() != uniqueCount) {
            throw new RuntimeException("Duplicate heading names found in the request. Each heading name must be unique.");
        }

        List<MetadataHeadingResponseDto> responseList = new ArrayList<>();

        for (HeadingWithFieldsDto headingDto : dto.getHeadings()) {
            MetadataHeading heading = new MetadataHeading();
            heading.setDocumentType(docType);
            heading.setSubType(subType);
            heading.setClassification(classification);
            heading.setHeadingName(headingDto.getHeadingName());
            heading.setDisplayOrder(headingDto.getHeadingDisplayOrder());
            MetadataHeading savedHeading = metadataHeadingRepository.save(heading);

            if (headingDto.getFields() != null && !headingDto.getFields().isEmpty()) {
                List<MetadataField> fields = headingDto.getFields().stream()
                        .map(fieldDto -> {
                            MetadataField field = new MetadataField();
                            field.setMetadataHeading(savedHeading);
                            field.setFieldName(fieldDto.getFieldName());
                            field.setFieldType(fieldDto.getFieldType());
                            field.setWidth(fieldDto.getWidth());
                            field.setRequired(fieldDto.getRequired());
                            field.setDisplayOrder(fieldDto.getDisplayOrder());
                            field.setMinLength(fieldDto.getMinLength());
                            field.setMaxLength(fieldDto.getMaxLength());
                            field.setSelectLine(fieldDto.getSelectLine());
                            field.setOptions(fieldDto.getOptions());
                            return field;
                        })
                        .collect(Collectors.toList());
                metadataFieldRepository.saveAll(fields);
            }

            // ✅ Use simplified response mapping for bulk create (without duplicate docType/SubType/Classification)
            responseList.add(mapToResponseDtoForBulk(savedHeading));
        }

        MetadataBulkStructureResponseDto response = new MetadataBulkStructureResponseDto();
        response.setDocumentTypeId(docType.getId());
        response.setDocumentTypeName(docType.getName()); // ✅ Set name at top level
        response.setSubTypeId(subType != null ? subType.getId() : null);
        response.setSubTypeName(subType != null ? subType.getName() : null); // ✅ Set name at top level
        response.setClassificationId(classification != null ? classification.getId() : null);
        response.setClassificationName(classification != null ? classification.getName() : null); // ✅ Set name at top level
        response.setHeadings(responseList);
        
        return response;
    }

    private MetadataHeadingResponseDto mapToResponseDto(MetadataHeading heading) {
        MetadataHeadingResponseDto dto = new MetadataHeadingResponseDto();
        dto.setId(heading.getId());
        dto.setDocumentTypeName(heading.getDocumentType() != null ? heading.getDocumentType().getName() : null);
        dto.setSubTypeName(heading.getSubType() != null ? heading.getSubType().getName() : null);
        dto.setClassificationName(heading.getClassification() != null ? heading.getClassification().getName() : null);
        dto.setHeadingName(heading.getHeadingName());
        dto.setDisplayOrder(heading.getDisplayOrder());
        List<MetadataField> fields = metadataFieldRepository.findByMetadataHeadingIdOrderByDisplayOrder(heading.getId());
        dto.setMetadataFields(fields.stream()
                .map(this::mapToFieldResponseDto)
                .collect(Collectors.toList()));
        return dto;
    }

    // ✅ Simplified response mapping for bulk create (removes duplicate docType/SubType/Classification from each heading)
    private MetadataHeadingResponseDto mapToResponseDtoForBulk(MetadataHeading heading) {
        MetadataHeadingResponseDto dto = new MetadataHeadingResponseDto();
        dto.setId(heading.getId());
        // Don't set documentTypeName, subTypeName, classificationName in bulk response
        // They are already present at the top level of MetadataBulkStructureResponseDto
        dto.setDocumentTypeName(null);
        dto.setSubTypeName(null);
        dto.setClassificationName(null);
        dto.setHeadingName(heading.getHeadingName());
        dto.setDisplayOrder(heading.getDisplayOrder());
        List<MetadataField> fields = metadataFieldRepository.findByMetadataHeadingIdOrderByDisplayOrder(heading.getId());
        // ✅ Use simplified field mapping without headingName (redundant in bulk response)
        dto.setMetadataFields(fields.stream()
                .map(this::mapToFieldResponseDtoForBulk)
                .collect(Collectors.toList()));
        return dto;
    }

    // ✅ Simplified field mapping for bulk response (removes redundant headingName)
    private com.qssence.backend.document_initiation_service.dto.response.MetadataFieldResponseDto mapToFieldResponseDtoForBulk(MetadataField field) {
        com.qssence.backend.document_initiation_service.dto.response.MetadataFieldResponseDto dto =
                new com.qssence.backend.document_initiation_service.dto.response.MetadataFieldResponseDto();
        dto.setId(field.getId());
        dto.setMetadataHeadingId(field.getMetadataHeading().getId());
        dto.setHeadingName(null); // ✅ Don't set headingName in bulk response (redundant)
        dto.setFieldName(field.getFieldName());
        dto.setFieldType(field.getFieldType());
        dto.setWidth(field.getWidth());
        dto.setRequired(field.getRequired());
        dto.setDisplayOrder(field.getDisplayOrder());
        dto.setMinLength(field.getMinLength());
        dto.setMaxLength(field.getMaxLength());
        dto.setSelectLine(field.getSelectLine());
        dto.setOptions(field.getOptions());
        return dto;
    }

    private com.qssence.backend.document_initiation_service.dto.response.MetadataFieldResponseDto mapToFieldResponseDto(MetadataField field) {
        com.qssence.backend.document_initiation_service.dto.response.MetadataFieldResponseDto dto =
                new com.qssence.backend.document_initiation_service.dto.response.MetadataFieldResponseDto();
        dto.setId(field.getId());
        dto.setMetadataHeadingId(field.getMetadataHeading().getId());
        dto.setHeadingName(field.getMetadataHeading().getHeadingName());
        dto.setFieldName(field.getFieldName());
        dto.setFieldType(field.getFieldType());
        dto.setWidth(field.getWidth());
        dto.setRequired(field.getRequired());
        dto.setDisplayOrder(field.getDisplayOrder());
        dto.setMinLength(field.getMinLength());
        dto.setMaxLength(field.getMaxLength());
        dto.setSelectLine(field.getSelectLine());
        dto.setOptions(field.getOptions());
        return dto;
    }

    @Override
    public MetadataHeadingResponseDto getById(Long headingId) {
        MetadataHeading heading = metadataHeadingRepository.findById(headingId)
                .orElseThrow(() -> new RuntimeException("Heading not found"));
        return mapToResponseDto(heading);
    }

    @Override
    public List<MetadataHeadingResponseDto> getAll(Long documentTypeId, Long subTypeId, Long classificationId) {
        List<MetadataHeading> headings = metadataHeadingRepository.findAll().stream()
                .filter(h -> (documentTypeId == null || (h.getDocumentType() != null && h.getDocumentType().getId().equals(documentTypeId))) &&
                        (subTypeId == null || (h.getSubType() != null && h.getSubType().getId().equals(subTypeId))) &&
                        (classificationId == null || (h.getClassification() != null && h.getClassification().getId().equals(classificationId))))
                .collect(Collectors.toList());
        return headings.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MetadataHeadingResponseDto updateHeading(Long headingId, HeadingWithFieldsDto headingDto) {
        MetadataHeading heading = metadataHeadingRepository.findById(headingId)
                .orElseThrow(() -> new RuntimeException("Heading not found"));
        heading.setHeadingName(headingDto.getHeadingName());
        heading.setDisplayOrder(headingDto.getHeadingDisplayOrder());
        // Remove old fields
        List<MetadataField> oldFields = metadataFieldRepository.findByMetadataHeadingIdOrderByDisplayOrder(headingId);
        metadataFieldRepository.deleteAll(oldFields);
        // Add new fields
        if (headingDto.getFields() != null && !headingDto.getFields().isEmpty()) {
            List<MetadataField> fields = headingDto.getFields().stream()
                    .map(fieldDto -> {
                        MetadataField field = new MetadataField();
                        field.setMetadataHeading(heading);
                        field.setFieldName(fieldDto.getFieldName());
                        field.setFieldType(fieldDto.getFieldType());
                        field.setWidth(fieldDto.getWidth());
                        field.setRequired(fieldDto.getRequired());
                        field.setDisplayOrder(fieldDto.getDisplayOrder());
                        field.setMinLength(fieldDto.getMinLength());
                        field.setMaxLength(fieldDto.getMaxLength());
                        field.setSelectLine(fieldDto.getSelectLine());
                        field.setOptions(fieldDto.getOptions());
                        return field;
                    })
                    .collect(Collectors.toList());
            metadataFieldRepository.saveAll(fields);
        }
        return mapToResponseDto(heading);
    }

    @Override
    @Transactional
    public void deleteHeading(Long headingId) {
        List<MetadataField> fields = metadataFieldRepository.findByMetadataHeadingIdOrderByDisplayOrder(headingId);
        metadataFieldRepository.deleteAll(fields);
        metadataHeadingRepository.deleteById(headingId);
    }
}
