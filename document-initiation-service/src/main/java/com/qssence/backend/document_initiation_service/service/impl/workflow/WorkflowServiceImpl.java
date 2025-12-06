package com.qssence.backend.document_initiation_service.service.impl.workflow;

import com.qssence.backend.document_initiation_service.dto.UserDto;
import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.PlantResponse;
import com.qssence.backend.document_initiation_service.dto.response.workflow.StateActivityResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowStateResponseDto;
import com.qssence.backend.document_initiation_service.enums.WorkflowType;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.model.workflow.Workflow;
import com.qssence.backend.document_initiation_service.model.workflow.WorkflowState;
import com.qssence.backend.document_initiation_service.model.workflow.Branch;
import com.qssence.backend.document_initiation_service.model.workflow.Child;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowRepository;
import com.qssence.backend.document_initiation_service.service.AuthIntegrationService;
import com.qssence.backend.document_initiation_service.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Autowired
    private WorkflowRepository repository;

    @Autowired
    private AuthIntegrationService authIntegrationService;

    @Override
    public ApiResponse<WorkflowResponseDto> createWorkflow(WorkflowRequestDto requestDto) {
        if (repository.existsByWorkflowName(requestDto.getWorkflowName())) {
            return new ApiResponse<>(false, "Workflow with this name already exists", null);
        }

        Workflow workflow = new Workflow();
        workflow.setWorkflowName(requestDto.getWorkflowName());
        workflow.setWorkflowType(requestDto.getWorkflowType());

        switch (requestDto.getWorkflowType()) {
            case GLOBAL -> {
                workflow.setRegion(null);
                workflow.setCountry(null);
                workflow.setPlantId(null);
            }
            case REGION -> {
                workflow.setRegion(requestDto.getRegion());
                workflow.setCountry(requestDto.getCountry());
                workflow.setPlantId(null);
            }
            case LOCAL -> {
                workflow.setRegion(requestDto.getRegion());
                workflow.setCountry(requestDto.getCountry());

                // ✅ Plant validation
                if (requestDto.getPlantId() == null) {
                    return new ApiResponse<>(false, "PlantId is required for LOCAL workflow", null);
                }

                PlantResponse plant = authIntegrationService.getPlantById(requestDto.getPlantId());
                if (plant == null) {
                    return new ApiResponse<>(false, "Invalid PlantId. No plant found in auth-service", null);
                }

                workflow.setPlantId(requestDto.getPlantId());
            }
        }

        Workflow saved = repository.save(workflow);
        WorkflowResponseDto responseDto = mapWorkflowToResponse(saved);

        return new ApiResponse<>(true, "Workflow created successfully", responseDto);
    }

    @Override
    public ApiResponse<List<WorkflowResponseDto>> getAllWorkflows() {
        List<Workflow> workflows = repository.findAll();

        List<WorkflowResponseDto> dtos = workflows.stream()
                .map(this::mapWorkflowToResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(true, "All workflows fetched successfully", dtos);
    }

    @Override
    public ApiResponse<WorkflowResponseDto> getWorkflowById(Long id) {
        Optional<Workflow> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return new ApiResponse<>(false, "Workflow not found with ID: " + id, null);
        }

        WorkflowResponseDto dto = mapWorkflowToResponse(optional.get());
        return new ApiResponse<>(true, "Workflow fetched successfully", dto);
    }

    @Override
    public ApiResponse<WorkflowResponseDto> updateWorkflow(Long id, WorkflowRequestDto requestDto) {
        Optional<Workflow> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return new ApiResponse<>(false, "Workflow not found with ID: " + id, null);
        }

        Workflow wf = optional.get();
        wf.setWorkflowName(requestDto.getWorkflowName());
        wf.setWorkflowType(requestDto.getWorkflowType());

        switch (requestDto.getWorkflowType()) {
            case GLOBAL -> {
                wf.setRegion(null);
                wf.setCountry(null);
                wf.setPlantId(null);
            }
            case REGION -> {
                wf.setRegion(requestDto.getRegion());
                wf.setCountry(requestDto.getCountry());
                wf.setPlantId(null);
            }
            case LOCAL -> {
                wf.setRegion(requestDto.getRegion());
                wf.setCountry(requestDto.getCountry());

                // ✅ Plant validation
                if (requestDto.getPlantId() == null) {
                    return new ApiResponse<>(false, "PlantId is required for LOCAL workflow", null);
                }

                PlantResponse plant = authIntegrationService.getPlantById(requestDto.getPlantId());
                if (plant == null) {
                    return new ApiResponse<>(false, "Invalid PlantId. No plant found in auth-service", null);
                }

                wf.setPlantId(requestDto.getPlantId());
            }
        }

        Workflow saved = repository.save(wf);
        WorkflowResponseDto dto = mapWorkflowToResponse(saved);

        return new ApiResponse<>(true, "Workflow updated successfully", dto);
    }

    @Override
    public ApiResponse<String> deleteWorkflowById(Long id) {
        Optional<Workflow> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return new ApiResponse<>(false, "Workflow not found with ID: " + id, null);
        }

        repository.deleteById(id);
        return new ApiResponse<>(true, "Workflow deleted successfully", "Deleted ID: " + id);
    }

    // -------------------- Helper Methods --------------------
    private WorkflowResponseDto mapWorkflowToResponse(Workflow wf) {
        WorkflowResponseDto dto = new WorkflowResponseDto();
        dto.setId(wf.getId());
        dto.setWorkflowName(wf.getWorkflowName());
        dto.setWorkflowType(wf.getWorkflowType());
        dto.setRegion(wf.getRegion());
        dto.setCountry(wf.getCountry());
        dto.setPlantId(wf.getPlantId());

        // 🔹 Add plant details if LOCAL
        if (wf.getWorkflowType() == WorkflowType.LOCAL && wf.getPlantId() != null) {
            PlantResponse plant = authIntegrationService.getPlantById(wf.getPlantId());
            dto.setPlant(plant);
        }

        // 🔹 States mapping (with users details also)
        if (wf.getStates() != null) {
            dto.setStates(wf.getStates().stream()
                    .map(this::mapToResponseDtoWithUsers)  // ✅ use updated method
                    .collect(Collectors.toList()));
        } else {
            dto.setStates(new ArrayList<>());
        }

        return dto;
    }


    private WorkflowStateResponseDto mapStateToResponseDto(WorkflowState state) {
        return WorkflowStateResponseDto.builder()
                .id(state.getId())
                .stateName(state.getStateName())
                .orderNumber(state.getOrderNumber())
                .branches(state.getBranches() != null ?
                        state.getBranches().stream()
                                .map(branch -> new WorkflowResponseDto.BranchDto(branch.getId(), branch.getBranchName()))
                                .collect(Collectors.toList()) : null)
                .activities(state.getActivities() != null ?
                        state.getActivities().stream()
                                .map(act -> new StateActivityResponseDto(act.getId(), act.getActivityName()))
                                .collect(Collectors.toList()) : null)
                .child(state.getChild() != null ?
                        state.getChild().stream()
                                .map(child -> new WorkflowResponseDto.ChildDto(child.getId(), child.getChildName()))
                                .collect(Collectors.toList()) : null)
                .groupId(state.getGroupId())
                .groupName(state.getGroupName())
                .memberIds(state.getMemberIds())
                .build();
    }

    private WorkflowStateResponseDto mapToResponseDtoWithUsers(WorkflowState state) {
        WorkflowStateResponseDto dto = mapStateToResponseDto(state);
        if (state.getMemberIds() != null && !state.getMemberIds().isEmpty()) {
            dto.setUsers(authIntegrationService.getUsersDetails(state.getMemberIds()));
        }
        return dto;
    }

}
