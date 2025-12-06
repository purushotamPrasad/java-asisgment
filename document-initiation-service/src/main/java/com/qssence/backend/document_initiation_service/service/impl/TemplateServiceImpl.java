package com.qssence.backend.document_initiation_service.service.impl;

import com.qssence.backend.document_initiation_service.dto.request.TemplateRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.TemplateResponseDto;
import com.qssence.backend.document_initiation_service.model.Classification;
import com.qssence.backend.document_initiation_service.model.DocumentType;
import com.qssence.backend.document_initiation_service.model.SubType;
import com.qssence.backend.document_initiation_service.model.Template;
import com.qssence.backend.document_initiation_service.repository.ClassificationRepository;
import com.qssence.backend.document_initiation_service.repository.DocumentTypeRepository;
import com.qssence.backend.document_initiation_service.repository.SubTypeRepository;
import com.qssence.backend.document_initiation_service.repository.TemplateRepository;
import com.qssence.backend.document_initiation_service.service.S3Service;
import com.qssence.backend.document_initiation_service.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final SubTypeRepository subTypeRepository;
    private final ClassificationRepository classificationRepository;
    private final S3Service s3Service;

    @Override
    public TemplateResponseDto uploadTemplate(Long documentTypeId, Long subTypeId, Long classificationId, MultipartFile file) {
        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new RuntimeException("DocumentType not found with id: " + documentTypeId));
        SubType subType = subTypeId != null ? subTypeRepository.findById(subTypeId).orElse(null) : null;
        Classification classification = classificationId != null ? classificationRepository.findById(classificationId).orElse(null) : null;

        // Check if template already exists for this combination
        Optional<Template> existingTemplate = templateRepository.findByDocumentTypeAndSubTypeAndClassification(
                documentTypeId, subTypeId, classificationId);

        if (existingTemplate.isPresent()) {
            throw new RuntimeException("Template already exists for DocumentType: " + documentType.getName() +
                    (subType != null ? ", SubType: " + subType.getName() : "") +
                    (classification != null ? ", Classification: " + classification.getName() : "") +
                    ". Please use update API to modify existing template or delete it first.");
        }

        // File ko S3 me upload karo
        String s3Url = s3Service.uploadFile(file, "templates/" + documentType.getName());

        Template template = Template.builder()
                .documentType(documentType)
                .subType(subType)
                .classification(classification)
                .templateFile(file.getOriginalFilename()) // 👈 sirf uploaded file ka naam
                .s3Url(s3Url)                             // 👈 S3 URL
                .uploadedAt(LocalDateTime.now())
                .build();

        template = templateRepository.save(template);

        return mapToResponseDto(template);
    }


    @Override
    public List<TemplateResponseDto> getAllTemplates() {
        List<Template> templates = templateRepository.findAll();
        return templates.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateResponseDto> getTemplatesByDocumentType(Long documentTypeId) {
        List<Template> templates = templateRepository.findAll().stream()
                .filter(t -> t.getDocumentType() != null && t.getDocumentType().getId().equals(documentTypeId))
                .collect(Collectors.toList());
        return templates.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public TemplateResponseDto getTemplateById(Long templateId) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + templateId));
        return mapToResponseDto(template);
    }


    @Override
    public TemplateResponseDto updateTemplate(Long id, TemplateRequestDto dto, MultipartFile file) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (dto.getDocumentTypeId() != null) {
            DocumentType documentType = documentTypeRepository.findById(dto.getDocumentTypeId()).orElse(null);
            template.setDocumentType(documentType);
        }
        if (dto.getSubTypeId() != null) {
            SubType subType = subTypeRepository.findById(dto.getSubTypeId()).orElse(null);
            template.setSubType(subType);
        }
        if (dto.getClassificationId() != null) {
            Classification classification = classificationRepository.findById(dto.getClassificationId()).orElse(null);
            template.setClassification(classification);
        }
        if (dto.getTemplateFile() != null) {
            template.setTemplateFile(dto.getTemplateFile());
        }

        if (file != null && !file.isEmpty()) {
            String s3Url = s3Service.uploadFile(file, "templates/" +
                    (template.getDocumentType() != null ? template.getDocumentType().getName() : "unknown"));
            template.setS3Url(s3Url);
        }

        template.setUploadedAt(LocalDateTime.now());
        template = templateRepository.save(template);
        return mapToResponseDto(template);
    }

    @Override
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found");
        }
        templateRepository.deleteById(id);
    }

    private TemplateResponseDto mapToResponseDto(Template template) {
        TemplateResponseDto dto = new TemplateResponseDto();
        dto.setId(template.getId());
        dto.setDocumentTypeName(template.getDocumentType() != null ? template.getDocumentType().getName() : null);
        dto.setSubTypeName(template.getSubType() != null ? template.getSubType().getName() : null);
        dto.setClassificationName(template.getClassification() != null ? template.getClassification().getName() : null);
        dto.setTemplateFile(template.getTemplateFile());
        dto.setS3Url(template.getS3Url());
        dto.setUploadedAt(template.getUploadedAt());
        return dto;
    }
}

