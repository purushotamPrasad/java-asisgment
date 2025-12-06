package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.DocumentTypeRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.DocumentTypeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DocumentTypeService {

    List<DocumentTypeResponseDto> getAllDocumentTypes();
    DocumentTypeResponseDto getDocumentTypeById(Long id);

    // ✅ Single DocumentType Create
    DocumentTypeResponseDto createDocumentType(DocumentTypeRequestDto dto);

    DocumentTypeResponseDto updateDocumentType(Long id, DocumentTypeRequestDto requestDto);
    void deleteDocumentType(Long id);
}
