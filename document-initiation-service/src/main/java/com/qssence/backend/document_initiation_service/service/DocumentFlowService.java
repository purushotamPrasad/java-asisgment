package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.document.DocumentFlowRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.document.DocumentFlowResponseDto;

import java.util.List;

public interface DocumentFlowService {

    DocumentFlowResponseDto createDocumentFlow(DocumentFlowRequestDto requestDto);

    List<DocumentFlowResponseDto> getAllDocumentFlows();

    DocumentFlowResponseDto getDocumentFlowById(Long id);

    DocumentFlowResponseDto updateDocumentFlow(Long id, DocumentFlowRequestDto requestDto);

    void deleteDocumentFlow(Long id);
}
