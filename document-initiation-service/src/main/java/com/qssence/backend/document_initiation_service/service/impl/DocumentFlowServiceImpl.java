package com.qssence.backend.document_initiation_service.service.impl;

import com.qssence.backend.document_initiation_service.dto.UserMasterDto;
import com.qssence.backend.document_initiation_service.dto.request.document.DocumentFlowRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.document.DocumentFlowResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.StateActivityResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowStateResponseDto;
import com.qssence.backend.document_initiation_service.model.*;
import com.qssence.backend.document_initiation_service.model.document.DocumentFlow;
import com.qssence.backend.document_initiation_service.model.workflow.Workflow;
import com.qssence.backend.document_initiation_service.model.workflow.WorkflowState;
import com.qssence.backend.document_initiation_service.model.workflow.Branch;
import com.qssence.backend.document_initiation_service.model.workflow.Child;
import com.qssence.backend.document_initiation_service.repository.*;
import com.qssence.backend.document_initiation_service.repository.document.DocumentFlowRepository;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowRepository;
import com.qssence.backend.document_initiation_service.service.AuthIntegrationService;
import com.qssence.backend.document_initiation_service.service.DocumentFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentFlowServiceImpl implements DocumentFlowService {

    private final DocumentFlowRepository documentFlowRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final SubTypeRepository subTypeRepository;
    private final ClassificationRepository classificationRepository;
    private final WorkflowRepository globalWorkflowRepository;
    private final AuthIntegrationService authIntegrationService;

    @Override
    public DocumentFlowResponseDto createDocumentFlow(DocumentFlowRequestDto dto) {
        DocumentType documentType = documentTypeRepository.findById(dto.getDocumentTypeId())
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));

        SubType subType = subTypeRepository.findById(dto.getSubTypeId())
                .orElseThrow(() -> new RuntimeException("SubType not found"));

        Classification classification = classificationRepository.findById(dto.getClassificationId())
                .orElseThrow(() -> new RuntimeException("Classification not found"));

        Workflow workflow = globalWorkflowRepository.findById(dto.getWorkflowId())
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        DocumentFlow flow = DocumentFlow.builder()
                .documentType(documentType)
                .subType(subType)
                .classification(classification)
                .workflow(workflow)
                .queryMemberIds(dto.getQueryMemberIds())
                .build();

        flow = documentFlowRepository.save(flow);
        return mapToResponseDto(flow);
    }

    @Override
    public List<DocumentFlowResponseDto> getAllDocumentFlows() {
        return documentFlowRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentFlowResponseDto getDocumentFlowById(Long id) {
        DocumentFlow flow = documentFlowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DocumentFlow not found"));
        return mapToResponseDto(flow);
    }

    @Override
    public DocumentFlowResponseDto updateDocumentFlow(Long id, DocumentFlowRequestDto dto) {
        DocumentFlow flow = documentFlowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DocumentFlow not found"));

        DocumentType documentType = documentTypeRepository.findById(dto.getDocumentTypeId())
                .orElseThrow(() -> new RuntimeException("DocumentType not found"));

        SubType subType = subTypeRepository.findById(dto.getSubTypeId())
                .orElseThrow(() -> new RuntimeException("SubType not found"));

        Classification classification = classificationRepository.findById(dto.getClassificationId())
                .orElseThrow(() -> new RuntimeException("Classification not found"));

        Workflow workflow = globalWorkflowRepository.findById(dto.getWorkflowId())
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        flow.setDocumentType(documentType);
        flow.setSubType(subType);
        flow.setClassification(classification);
        flow.setWorkflow(workflow);
        flow.setQueryMemberIds(dto.getQueryMemberIds());

        documentFlowRepository.save(flow);
        return mapToResponseDto(flow);
    }

    @Override
    public void deleteDocumentFlow(Long id) {
        documentFlowRepository.deleteById(id);
    }

    // -------------------- MAPPERS --------------------

    private DocumentFlowResponseDto mapToResponseDto(DocumentFlow flow) {
        DocumentFlowResponseDto dto = new DocumentFlowResponseDto();
        dto.setId(flow.getId());
        dto.setDocumentTypeName(flow.getDocumentType().getName());
        dto.setSubTypeName(flow.getSubType().getName());
        dto.setClassificationName(flow.getClassification().getName());
        dto.setWorkflowDetails(mapToWorkflowDto(flow.getWorkflow()));

        List<Long> memberIds = flow.getQueryMemberIds();
        dto.setQueryMembers(resolveUsers(memberIds));

        return dto;
    }

    private WorkflowResponseDto mapToWorkflowDto(Workflow workflow) {
        WorkflowResponseDto dto = new WorkflowResponseDto();
        dto.setId(workflow.getId());
        dto.setWorkflowName(workflow.getWorkflowName());

        List<WorkflowState> states = workflow.getStates();
        List<WorkflowStateResponseDto> stateDtos = new ArrayList<>();

        for (WorkflowState state : states) {
            WorkflowStateResponseDto stateDto = new WorkflowStateResponseDto();
            stateDto.setId(state.getId());
            stateDto.setStateName(state.getStateName());
            stateDto.setOrderNumber(state.getOrderNumber());

            stateDto.setBranches(
                    state.getBranches() == null ? null :
                            state.getBranches().stream()
                                    .map(branch -> new WorkflowResponseDto.BranchDto(branch.getId(), branch.getBranchName()))
                                    .collect(Collectors.toList())
            );

            // ✅ FIXED: Map List<StateActivity> to List<StateActivityResponseDto>
            stateDto.setActivities(
                    state.getActivities() == null ? null :
                            state.getActivities().stream()
                                    .map(act -> new StateActivityResponseDto(act.getId(), act.getActivityName()))
                                    .collect(Collectors.toList())
            );

            stateDto.setChild(
                    state.getChild() == null ? null :
                            state.getChild().stream()
                                    .map(child -> new WorkflowResponseDto.ChildDto(child.getId(), child.getChildName()))
                                    .collect(Collectors.toList())
            );

            stateDto.setGroupId(state.getGroupId());
            stateDto.setGroupName(state.getGroupName());

            // ✅ MemberIds भी map कर दो
            stateDto.setMemberIds(state.getMemberIds());

            // ✅ Assigned users लाओ (optional, आपके use-case पर depend करता है)
            if (state.getMemberIds() != null && !state.getMemberIds().isEmpty()) {
                stateDto.setUsers(authIntegrationService.getUsersDetails(state.getMemberIds()));
            } else {
                stateDto.setUsers(new ArrayList<>());
            }

            stateDtos.add(stateDto);
        }

        dto.setStates(stateDtos);
        return dto;
    }


    private List<UserMasterDto> resolveUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        List<UserMasterDto> allUsers = authIntegrationService.getAllUsers();
        return allUsers.stream()
                .filter(user -> ids.contains(user.getUserId()))
                .collect(Collectors.toList());
    }
}
