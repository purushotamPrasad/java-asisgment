package com.qssence.backend.document_initiation_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.document_initiation_service.dto.request.client.ClientDocumentRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.client.*;
import com.qssence.backend.document_initiation_service.model.*;
import com.qssence.backend.document_initiation_service.model.document.DocumentFlow;
import com.qssence.backend.document_initiation_service.model.document.DocumentRecord;
import com.qssence.backend.document_initiation_service.model.workflow.Workflow;
import com.qssence.backend.document_initiation_service.model.workflow.WorkflowState;
import com.qssence.backend.document_initiation_service.model.workflow.Branch;
import com.qssence.backend.document_initiation_service.model.workflow.Child;
import com.qssence.backend.document_initiation_service.repository.*;
import com.qssence.backend.document_initiation_service.repository.document.DocumentFlowRepository;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowRepository;
import com.qssence.backend.document_initiation_service.service.ClientPanelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientPanelServiceImpl implements ClientPanelService {

    private final DocumentTypeRepository documentTypeRepository;
    private final SubTypeRepository subTypeRepository;
    private final ClassificationRepository classificationRepository;
    private final MetadataHeadingRepository metadataHeadingRepository;
    private final WorkflowRepository workflowRepository;
    private final DocumentFlowRepository documentFlowRepository;
    private final DocumentRecordRepository documentRecordRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<ClientDocumentTypeDto> getAllDocumentTypes() {
        List<DocumentType> documentTypes = documentTypeRepository.findAll();
        return documentTypes.stream()
                .map(this::mapToClientDocumentTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDocumentTypeDto getDocumentTypeWithSubTypesAndClassifications(Long documentTypeId) {
        DocumentType documentType = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));
        return mapToClientDocumentTypeDto(documentType);
    }

    @Override
    public ClientMetadataWorkflowResponseDto getMetadataAndWorkflow(Long documentTypeId, Long subTypeId, Long classificationId) {
        // Get metadata headings for the selected combination
        List<MetadataHeading> headings = metadataHeadingRepository.findByDocumentTypeAndSubTypeAndClassification(
                documentTypeId, subTypeId, classificationId);
        
        // Get workflow from document flow
        Workflow workflow = getWorkflowForDocumentFlow(documentTypeId, subTypeId, classificationId);
        
        List<ClientMetadataHeadingDto> headingDtos = headings.stream()
                .map(this::mapToClientMetadataHeadingDto)
                .collect(Collectors.toList());
        
        List<ClientWorkflowStateDto> workflowStates = workflow.getStates().stream()
                .map(this::mapToClientWorkflowStateDto)
                .collect(Collectors.toList());
        
        return ClientMetadataWorkflowResponseDto.builder()
                .metadataHeadings(headingDtos)
                .workflowStates(workflowStates)
                .build();
    }

    @Override
    @Transactional
    public Long createDocumentRecord(ClientDocumentRequestDto requestDto) {
        try {
            // Get the entities
            DocumentType documentType = documentTypeRepository.findById(requestDto.getDocumentTypeId())
                    .orElseThrow(() -> new RuntimeException("DocumentType not found"));
            
            SubType subType = subTypeRepository.findById(requestDto.getSubTypeId())
                    .orElseThrow(() -> new RuntimeException("SubType not found"));
            
            Classification classification = classificationRepository.findById(requestDto.getClassificationId())
                    .orElseThrow(() -> new RuntimeException("Classification not found"));
            
            Workflow workflow = getWorkflowForDocumentFlow(requestDto.getDocumentTypeId(), 
                    requestDto.getSubTypeId(), requestDto.getClassificationId());
            
            // Convert metadata values to JSON string
            String metadataJson = objectMapper.writeValueAsString(requestDto.getMetadataValues());
            
            // Create document record
            DocumentRecord documentRecord = DocumentRecord.builder()
                    .documentName(requestDto.getDocumentName())
                    .documentType(documentType)
                    .subType(subType)
                    .classification(classification)
                    .workflow(workflow)
                    .currentState(workflow.getStates().get(0).getStateName()) // First state
                    .templatePath(requestDto.getTemplatePath())
                    .metadataValues(metadataJson)
                    .createdBy(1L) // TODO: Get from authentication context
                    .status("DRAFT")
                    .build();
            
            DocumentRecord saved = documentRecordRepository.save(documentRecord);
            return saved.getId();
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting metadata to JSON", e);
        }
    }

    @Override
    public List<ClientDocumentRecordDto> getUserDocuments(Long userId) {
        List<DocumentRecord> records = documentRecordRepository.findByCreatedBy(userId);
        return records.stream()
                .map(this::mapToClientDocumentRecordDto)
                .collect(Collectors.toList());
    }

    // Helper methods for mapping
    private ClientDocumentTypeDto mapToClientDocumentTypeDto(DocumentType documentType) {
        List<ClientSubTypeDto> subTypeDtos = documentType.getSubTypes().stream()
                .map(this::mapToClientSubTypeDto)
                .collect(Collectors.toList());
        
        return ClientDocumentTypeDto.builder()
                .id(documentType.getId())
                .name(documentType.getName())
                .subTypes(subTypeDtos)
                .build();
    }

    private ClientSubTypeDto mapToClientSubTypeDto(SubType subType) {
        List<ClientClassificationDto> classificationDtos = subType.getClassifications().stream()
                .map(this::mapToClientClassificationDto)
                .collect(Collectors.toList());
        
        return ClientSubTypeDto.builder()
                .id(subType.getId())
                .name(subType.getName())
                .classifications(classificationDtos)
                .build();
    }

    private ClientClassificationDto mapToClientClassificationDto(Classification classification) {
        return ClientClassificationDto.builder()
                .id(classification.getId())
                .name(classification.getName())
                .build();
    }

    private ClientMetadataHeadingDto mapToClientMetadataHeadingDto(MetadataHeading heading) {
        List<ClientMetadataFieldDto> fieldDtos = heading.getMetadataFields().stream()
                .map(this::mapToClientMetadataFieldDto)
                .collect(Collectors.toList());
        
        return ClientMetadataHeadingDto.builder()
                .id(heading.getId())
                .headingName(heading.getHeadingName())
                .displayOrder(heading.getDisplayOrder())
                .metadataFields(fieldDtos)
                .build();
    }

    private ClientMetadataFieldDto mapToClientMetadataFieldDto(MetadataField field) {
        return ClientMetadataFieldDto.builder()
                .id(field.getId())
                .fieldName(field.getFieldName())
                .fieldType(field.getFieldType())
                .width(field.getWidth())
                .required(field.getRequired())
                .displayOrder(field.getDisplayOrder())
                .minLength(field.getMinLength())
                .maxLength(field.getMaxLength())
                .selectLine(field.getSelectLine())
                .options(field.getOptions())
                .build();
    }

    private ClientWorkflowStateDto mapToClientWorkflowStateDto(WorkflowState state) {
        // Convert Branch entities to String list
        List<String> branchNames = null;
        if (state.getBranches() != null && !state.getBranches().isEmpty()) {
            branchNames = state.getBranches().stream()
                    .map(Branch::getBranchName)
                    .collect(Collectors.toList());
        }
        
        // Convert Child entities to String list
        List<String> childNames = null;
        if (state.getChild() != null && !state.getChild().isEmpty()) {
            childNames = state.getChild().stream()
                    .map(Child::getChildName)
                    .collect(Collectors.toList());
        }
        
        return ClientWorkflowStateDto.builder()
                .id(state.getId())
                .stateName(state.getStateName())
                .orderNumber(state.getOrderNumber())
                .branches(branchNames)
                .child(childNames)
                .groupId(state.getGroupId())
                .groupName(state.getGroupName())
                .memberIds(state.getMemberIds())
                .build();
    }

    private Workflow getWorkflowForDocumentFlow(Long documentTypeId, Long subTypeId, Long classificationId) {
        // Find DocumentFlow by document type, subtype, and classification
        DocumentFlow documentFlow = documentFlowRepository.findByDocumentTypeIdAndSubTypeIdAndClassificationId(
                documentTypeId, subTypeId, classificationId)
                .orElseThrow(() -> new RuntimeException("No workflow found for the selected document type, subtype, and classification combination"));
        
        return documentFlow.getWorkflow();
    }

    private ClientDocumentRecordDto mapToClientDocumentRecordDto(DocumentRecord record) {
        return ClientDocumentRecordDto.builder()
                .id(record.getId())
                .documentName(record.getDocumentName())
                .documentTypeName(record.getDocumentType().getName())
                .subTypeName(record.getSubType().getName())
                .classificationName(record.getClassification().getName())
                .workflowName(record.getWorkflow().getWorkflowName())
                .currentState(record.getCurrentState())
                .templatePath(record.getTemplatePath())
                .metadataValues(record.getMetadataValues())
                .createdBy(record.getCreatedBy())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .status(record.getStatus())
                .build();
    }
}
