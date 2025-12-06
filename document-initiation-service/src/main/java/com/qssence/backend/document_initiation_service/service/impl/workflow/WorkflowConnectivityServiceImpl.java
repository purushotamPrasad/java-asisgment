package com.qssence.backend.document_initiation_service.service.impl.workflow;

import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowConnectivityRequestDto;
import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowStateConnectivityRequestDTO;
import com.qssence.backend.document_initiation_service.dto.response.workflow.*;
import com.qssence.backend.document_initiation_service.enums.FieldType;
import com.qssence.backend.document_initiation_service.enums.FieldWidth;
import com.qssence.backend.document_initiation_service.model.workflow.*;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowRepository;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowConnectivityRepository;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowStateRepository;
import com.qssence.backend.document_initiation_service.service.WorkflowConnectivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowConnectivityServiceImpl implements WorkflowConnectivityService {

    private final WorkflowConnectivityRepository workflowConnectivityRepository;
    private final WorkflowRepository globalWorkflowRepository;
    private final WorkflowStateRepository workflowStateRepository;

    @Override
    @Transactional
    public WorkflowConnectivityResponseDto createWorkflowConnectivity(WorkflowConnectivityRequestDto request) {
        WorkflowConnectivity connectivity = workflowConnectivityRepository.findByWorkflow_Id(request.getWorkflowId())
                .orElse(null);

        if (connectivity == null) {
            Workflow workflow = globalWorkflowRepository.findById(request.getWorkflowId())
                    .orElseThrow(() -> new RuntimeException("Workflow not found"));

            connectivity = WorkflowConnectivity.builder()
                    .workflow(workflow)
                    .revisionDate(request.getRevisionDate())
                    .build();
        } else {
            connectivity.setRevisionDate(request.getRevisionDate());
            connectivity.getStates().clear();
        }

        connectivity.setStates(buildStatesFromRequest(request.getStates(), connectivity));

        WorkflowConnectivity saved = workflowConnectivityRepository.save(connectivity);
        return mapToResponse(saved);
    }

    @Override
    public List<WorkflowConnectivityResponseDto> getAll() {
        return workflowConnectivityRepository.findAll()
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public WorkflowConnectivityResponseDto updateByWorkflowId(Long workflowId, WorkflowConnectivityRequestDto requestDto) {
        WorkflowConnectivity connectivity = workflowConnectivityRepository.findByWorkflow_Id(workflowId)
                .orElseThrow(() -> new RuntimeException("WorkflowConnectivity not found"));

        connectivity.setRevisionDate(requestDto.getRevisionDate());

        connectivity.getStates().clear();
        connectivity.getStates().addAll(buildStatesFromRequest(requestDto.getStates(), connectivity));

        WorkflowConnectivity updated = workflowConnectivityRepository.save(connectivity);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteByWorkflowId(Long workflowId) {
        workflowConnectivityRepository.deleteByWorkflow_Id(workflowId);
    }

    @Override
    public WorkflowConnectivityResponseDto getWorkflowConnectivityByWorkflowId(Long workflowId) {
        WorkflowConnectivity connectivity = workflowConnectivityRepository.findByWorkflow_Id(workflowId)
                .orElseThrow(() -> new RuntimeException("WorkflowConnectivity not found"));
        return mapToResponse(connectivity);
    }

    // ===================== Helper Methods =====================

    private List<WorkflowStateConnectivity> buildStatesFromRequest(
            List<WorkflowStateConnectivityRequestDTO> stateRequests,
            WorkflowConnectivity connectivity
    ) {
        if (stateRequests == null) return List.of();

        return stateRequests.stream().map(stateReq -> {
            WorkflowState state = workflowStateRepository.findById(stateReq.getStateId())
                    .orElseThrow(() -> new RuntimeException("State not found"));

            WorkflowStateConnectivity stateConnectivity = WorkflowStateConnectivity.builder()
                    .workflowConnectivity(connectivity)
                    .state(state)
                    .electronicSignature(stateReq.getElectronicSignature())
                    .build();

            // ✅ Process Activities
            List<ProcessActivity> processActivities = stateReq.getProcessActivities() != null
                    ? stateReq.getProcessActivities().stream().map(actReq -> {
                StateActivity masterActivity = state.getActivities().stream()
                        .filter(a -> a.getId().equals(actReq.getStateActivityId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("StateActivity not found for id " + actReq.getStateActivityId()));

                ProcessActivity activity = ProcessActivity.builder()
                        .stateConnectivity(stateConnectivity)
                        .stateActivity(masterActivity) // relation optional
                        .activityName(masterActivity.getActivityName())
                        .build();

                List<DynamicDataField> fields = actReq.getFields() != null
                        ? actReq.getFields().stream().map(f -> DynamicDataField.builder()
                        .fieldName(f.getFieldName())
                        .fieldType(safeFieldType(f.getFieldType()))
                        .width(safeFieldWidth(f.getWidth()))
                        .minLength(f.getMinLength())
                        .maxLength(f.getMaxLength())
                        .selectLine(f.getSelectLine())
                        .options(f.getOptions())
                        .processActivity(activity)
                        .build()).toList()
                        : List.of();

                activity.setFields(fields);
                return activity;
            }).toList()
                    : List.of();

            stateConnectivity.setProcessActivities(processActivities);

            // ✅ Workflow Completes
            List<WorkflowComplete> completes = stateReq.getWorkflowCompletes() != null
                    ? stateReq.getWorkflowCompletes().stream().map(c -> {
                WorkflowComplete complete = WorkflowComplete.builder()
                        .completeName(c.getCompleteName())
                        .stateConnectivity(stateConnectivity)
                        .build();

                List<DynamicDataField> fields = c.getFields() != null
                        ? c.getFields().stream().map(f -> DynamicDataField.builder()
                        .fieldName(f.getFieldName())
                        .fieldType(safeFieldType(f.getFieldType()))
                        .width(safeFieldWidth(f.getWidth()))
                        .minLength(f.getMinLength())
                        .maxLength(f.getMaxLength())
                        .selectLine(f.getSelectLine())
                        .options(f.getOptions())
                        .workflowComplete(complete)
                        .build()).toList()
                        : List.of();

                complete.setFields(fields);
                return complete;
            }).toList()
                    : List.of();

            stateConnectivity.setWorkflowCompletes(completes);

            return stateConnectivity;
        }).toList();
    }

    private FieldType safeFieldType(String type) {
        try {
            return type != null ? FieldType.valueOf(type.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private FieldWidth safeFieldWidth(String width) {
        try {
            return width != null ? FieldWidth.valueOf(width.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ===================== Mapping to Response =====================

    private WorkflowConnectivityResponseDto mapToResponse(WorkflowConnectivity connectivity) {
        return WorkflowConnectivityResponseDto.builder()
                .workflowId(connectivity.getId())
                .workflowName(connectivity.getWorkflow() != null ? connectivity.getWorkflow().getWorkflowName() : null)
                .revisionDate(connectivity.getRevisionDate())
                .states(connectivity.getStates() != null
                        ? connectivity.getStates().stream().map(this::mapToResponse).toList()
                        : List.of())
                .build();
    }

    private WorkflowStateConnectivityResponseDTO mapToResponse(WorkflowStateConnectivity entity) {
        return WorkflowStateConnectivityResponseDTO.builder()
                .stateId(entity.getState() != null ? entity.getState().getId() : null)
                .stateName(entity.getState() != null ? entity.getState().getStateName() : null)

                // ✅ Master activities list mapped to StateActivityResponseDTO
                .stateActivities(entity.getState() != null && entity.getState().getActivities() != null
                        ? entity.getState().getActivities().stream()
                        .map(act -> StateActivityResponseDto.builder()
                                .id(act.getId())
                                .activityName(act.getActivityName())
                                .build())
                        .toList()
                        : List.of())

                .electronicSignature(entity.getElectronicSignature())

                // ✅ User configured process activities
                .processActivities(entity.getProcessActivities() != null
                        ? entity.getProcessActivities().stream()
                        .map(this::mapProcessActivityToResponse)
                        .toList()
                        : List.of())

                .workflowCompletes(entity.getWorkflowCompletes() != null
                        ? entity.getWorkflowCompletes().stream()
                        .map(this::mapWorkflowCompleteToResponse)
                        .toList()
                        : List.of())
                .build();
    }


    private ProcessActivityResponseDTO mapProcessActivityToResponse(ProcessActivity act) {
        return ProcessActivityResponseDTO.builder()
                .stateActivityId(act.getId())
                .stateActivityId(act.getStateActivity() != null ? act.getStateActivity().getId() : null)
                .activityName(act.getActivityName())
                .fields(act.getFields() != null
                        ? act.getFields().stream().map(f ->
                        DynamicDataFieldResponseDTO.builder()
                                .fieldId(f.getId())
                                .fieldName(f.getFieldName())
                                .fieldType(f.getFieldType() != null ? f.getFieldType().name() : null)
                                .width(f.getWidth() != null ? f.getWidth().name() : null)
                                .minLength(f.getMinLength())
                                .maxLength(f.getMaxLength())
                                .selectLine(f.getSelectLine())
                                .options(f.getOptions())
                                .build()
                ).toList()
                        : List.of())
                .build();
    }

    private WorkflowCompleteResponseDTO mapWorkflowCompleteToResponse(WorkflowComplete comp) {
        return WorkflowCompleteResponseDTO.builder()
                .completeId(comp.getId())
                .completeName(comp.getCompleteName())
                .fields(comp.getFields() != null
                        ? comp.getFields().stream().map(f ->
                        DynamicDataFieldResponseDTO.builder()
                                .fieldId(f.getId())
                                .fieldName(f.getFieldName())
                                .fieldType(f.getFieldType() != null ? f.getFieldType().name() : null)
                                .width(f.getWidth() != null ? f.getWidth().name() : null)
                                .minLength(f.getMinLength())
                                .maxLength(f.getMaxLength())
                                .selectLine(f.getSelectLine())
                                .options(f.getOptions())
                                .build()
                ).toList()
                        : List.of())
                .build();
    }
}
