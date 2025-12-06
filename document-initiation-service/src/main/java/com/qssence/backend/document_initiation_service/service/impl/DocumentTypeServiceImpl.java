package com.qssence.backend.document_initiation_service.service.impl;

import com.qssence.backend.document_initiation_service.dto.request.*;
import com.qssence.backend.document_initiation_service.dto.response.*;
import com.qssence.backend.document_initiation_service.model.*;
import com.qssence.backend.document_initiation_service.repository.*;
import com.qssence.backend.document_initiation_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;
    private final SubTypeRepository subTypeRepository;
    private final ClassificationRepository classificationRepository;
    private final AuthIntegrationService authIntegrationService;

    // ✅ Single DocumentType Create
    @Override
    public DocumentTypeResponseDto createDocumentType(DocumentTypeRequestDto dto) {
        DocumentType documentType = new DocumentType();
        documentType.setName(dto.getName());

        List<SubType> subTypes = new ArrayList<>();
        for (SubTypeRequestDto subDto : dto.getSubTypes()) {
            SubType subType = new SubType();
            subType.setName(subDto.getName());
            subType.setDocumentType(documentType);

            List<Classification> classifications = new ArrayList<>();
            for (ClassificationRequestDto classDto : subDto.getClassifications()) {
                Classification classification = new Classification();
                classification.setName(classDto.getName());
                classification.setSubType(subType);
                classifications.add(classification);
            }

            subType.setClassifications(classifications);
            subTypes.add(subType);
        }

        documentType.setSubTypes(subTypes);
        DocumentType saved = documentTypeRepository.save(documentType);
        return mapToResponseDto(saved);
    }

    // ✅ Update (with existing ID support)
    @Override
    public DocumentTypeResponseDto updateDocumentType(Long id, DocumentTypeRequestDto dto) {
        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));

        documentType.setName(dto.getName());

        List<SubType> newSubTypes = new ArrayList<>();

        for (SubTypeRequestDto subDto : dto.getSubTypes()) {
            SubType subType = subDto.getId() != null
                    ? subTypeRepository.findById(subDto.getId()).orElse(new SubType())
                    : new SubType();

            subType.setName(subDto.getName());
            subType.setDocumentType(documentType);

            // Important: clear & update instead of setting new list
            List<Classification> updatedClassifications = new ArrayList<>();
            for (ClassificationRequestDto classDto : subDto.getClassifications()) {
                Classification classification = classDto.getId() != null
                        ? classificationRepository.findById(classDto.getId()).orElse(new Classification())
                        : new Classification();

                classification.setName(classDto.getName());
                classification.setSubType(subType);
                updatedClassifications.add(classification);
            }

            if (subType.getClassifications() == null) {
                subType.setClassifications(new ArrayList<>());
            } else {
                subType.getClassifications().clear(); // ✅ THIS IS CRUCIAL
            }
            subType.getClassifications().addAll(updatedClassifications);

            newSubTypes.add(subType);
        }

        documentType.getSubTypes().clear();
        documentType.getSubTypes().addAll(newSubTypes);
        DocumentType saved = documentTypeRepository.save(documentType);

        return mapToResponseDto(saved);
    }

    @Override
    public List<DocumentTypeResponseDto> getAllDocumentTypes() {
        return documentTypeRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentTypeResponseDto getDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id)
                .map(this::mapToResponseDto)
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));
    }

    @Override
    public void deleteDocumentType(Long id) {
        documentTypeRepository.deleteById(id);
    }

    // ✅ DTO Mapper
    private DocumentTypeResponseDto mapToResponseDto(DocumentType documentType) {
        return DocumentTypeResponseDto.builder()
                .id(documentType.getId())
                .name(documentType.getName())
                .subTypes(documentType.getSubTypes().stream().map(subType ->
                                SubTypeResponseDto.builder()
                                        .id(subType.getId())
                                        .name(subType.getName())
                                        .classifications(subType.getClassifications().stream()
                                                .map(c -> new ClassificationResponseDto(c.getId(), c.getName()))
                                                .collect(Collectors.toList()))
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

