package com.qssence.backend.document_initiation_service.service;

import com.qssence.backend.document_initiation_service.dto.request.client.ClientDocumentRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.client.*;

import java.util.List;

public interface ClientPanelService {
    
    // Get all document types with their subtypes and classifications
    List<ClientDocumentTypeDto> getAllDocumentTypes();
    
    // Get complete document type data with subtypes and classifications
    ClientDocumentTypeDto getDocumentTypeWithSubTypesAndClassifications(Long documentTypeId);
    
    // Get metadata fields and workflow states based on document type, subtype, and classification
    ClientMetadataWorkflowResponseDto getMetadataAndWorkflow(Long documentTypeId, Long subTypeId, Long classificationId);
    
    // Create a new document record with user-filled data
    Long createDocumentRecord(ClientDocumentRequestDto requestDto);
    
    // Get document records for a user
    List<ClientDocumentRecordDto> getUserDocuments(Long userId);
}
