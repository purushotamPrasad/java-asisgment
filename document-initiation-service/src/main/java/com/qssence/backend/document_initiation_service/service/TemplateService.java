package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.TemplateRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.TemplateResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TemplateService {

    TemplateResponseDto uploadTemplate(Long documentTypeId, Long subTypeId, Long classificationId, MultipartFile file);

    TemplateResponseDto updateTemplate(Long id, TemplateRequestDto dto, MultipartFile file);

    void deleteTemplate(Long id);

    List<TemplateResponseDto> getAllTemplates();

    List<TemplateResponseDto> getTemplatesByDocumentType(Long documentTypeId);

    TemplateResponseDto getTemplateById(Long templateId);
}
