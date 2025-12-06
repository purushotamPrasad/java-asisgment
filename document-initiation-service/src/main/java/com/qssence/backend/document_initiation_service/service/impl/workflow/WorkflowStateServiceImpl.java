package com.qssence.backend.document_initiation_service.service.impl.workflow;

import com.qssence.backend.document_initiation_service.dto.UserDto;
import com.qssence.backend.document_initiation_service.dto.request.workflow.WorkflowStateRequestDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.StateActivityResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowStateResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.model.workflow.Workflow;
import com.qssence.backend.document_initiation_service.model.workflow.StateActivity;
import com.qssence.backend.document_initiation_service.model.workflow.WorkflowState;
import com.qssence.backend.document_initiation_service.model.workflow.Branch;
import com.qssence.backend.document_initiation_service.model.workflow.Child;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowRepository;
import com.qssence.backend.document_initiation_service.repository.global.WorkflowStateRepository;
import com.qssence.backend.document_initiation_service.service.AuthIntegrationService;
import com.qssence.backend.document_initiation_service.service.WorkflowStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkflowStateServiceImpl implements WorkflowStateService {

    @Autowired
    private WorkflowStateRepository stateRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private AuthIntegrationService authIntegrationService;

    @Override
    public ApiResponse<List<WorkflowStateResponseDto>> createStates(Long workflowId, List<WorkflowStateRequestDto> requestDtos) {
        Optional<Workflow> workflowOpt = workflowRepository.findById(workflowId);
        if (workflowOpt.isEmpty()) {
            return new ApiResponse<>(false, "Workflow not found", null);
        }

        List<WorkflowStateResponseDto> savedStates = requestDtos.stream().map(requestDto -> {
            WorkflowState state = new WorkflowState();
            state.setWorkflow(workflowOpt.get());

            setCommonFieldsWithValidation(state, requestDto);

            WorkflowState saved = stateRepository.save(state);
            return mapToResponseDtoWithUsers(saved);
        }).collect(Collectors.toList());

        return new ApiResponse<>(true, "States created successfully", savedStates);
    }

    @Override
    public ApiResponse<List<WorkflowStateResponseDto>> getStatesByWorkflowId(Long workflowId) {
        List<WorkflowState> states = stateRepository.findByWorkflowId(workflowId);
        List<WorkflowStateResponseDto> responseDtos = states.stream()
                .map(this::mapToResponseDtoWithUsers)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "States fetched successfully", responseDtos);
    }

    @Override
    public ApiResponse<List<WorkflowStateResponseDto>> getAllStates() {
        List<WorkflowState> states = stateRepository.findAll();
        List<WorkflowStateResponseDto> responseDtos = states.stream()
                .map(this::mapToResponseDtoWithUsers)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "All states fetched successfully", responseDtos);
    }

    @Override
    public ApiResponse<List<WorkflowStateResponseDto>> updateStatesByWorkflowId(Long workflowId, List<WorkflowStateRequestDto> requestDtos) {
        Optional<Workflow> workflowOpt = workflowRepository.findById(workflowId);
        if (workflowOpt.isEmpty()) {
            return new ApiResponse<>(false, "Workflow not found", null);
        }

        List<WorkflowState> existingStates = stateRepository.findByWorkflowId(workflowId);

        // find IDs present in request
        List<Long> requestIds = requestDtos.stream()
                .map(WorkflowStateRequestDto::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        // delete states not in request
        List<WorkflowState> statesToDelete = existingStates.stream()
                .filter(state -> !requestIds.contains(state.getId()))
                .collect(Collectors.toList());

        if (!statesToDelete.isEmpty()) {
            stateRepository.deleteAll(statesToDelete);
        }

        // update or create
        List<WorkflowStateResponseDto> updatedStates = requestDtos.stream().map(requestDto -> {
            WorkflowState state;
            if (requestDto.getId() != null) {
                state = existingStates.stream()
                        .filter(s -> s.getId().equals(requestDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("State with ID " + requestDto.getId() + " not found"));
            } else {
                state = new WorkflowState();
                state.setWorkflow(workflowOpt.get());
            }

            setCommonFieldsWithValidation(state, requestDto);

            WorkflowState saved = stateRepository.save(state);
            return mapToResponseDtoWithUsers(saved);
        }).collect(Collectors.toList());

        return new ApiResponse<>(true, "States updated successfully", updatedStates);
    }

    @Override
    public ApiResponse<String> deleteState(Long stateId) {
        Optional<WorkflowState> stateOpt = stateRepository.findById(stateId);
        if (stateOpt.isEmpty()) {
            return new ApiResponse<>(false, "State not found with ID: " + stateId, null);
        }
        stateRepository.deleteById(stateId);
        return new ApiResponse<>(true, "State deleted successfully", "Deleted ID: " + stateId);
    }

    /**
     * Map fields + validation
     */
    private void setCommonFieldsWithValidation(WorkflowState state, WorkflowStateRequestDto requestDto) {
        state.setStateName(requestDto.getStateName());
        state.setOrderNumber(requestDto.getOrderNumber());

        // Handle branches as entities
        if (requestDto.getBranches() != null && !requestDto.getBranches().isEmpty()) {
            List<Branch> branches = requestDto.getBranches().stream()
                    .map(branchDto -> Branch.builder()
                            .id(branchDto.getId())
                            .branchName(branchDto.getBranchName())
                            .workflowState(state)
                            .build())
                    .collect(Collectors.toList());
            state.setBranches(branches);
        } else {
            state.setBranches(new ArrayList<>());
        }

        // Handle child as entities
        if (requestDto.getChild() != null && !requestDto.getChild().isEmpty()) {
            List<Child> children = requestDto.getChild().stream()
                    .map(childDto -> Child.builder()
                            .id(childDto.getId())
                            .childName(childDto.getChildName())
                            .workflowState(state)
                            .build())
                    .collect(Collectors.toList());
            state.setChild(children);
        } else {
            state.setChild(new ArrayList<>());
        }

        // handle activities properly
        if (requestDto.getActivities() != null) {
            List<StateActivity> activities = requestDto.getActivities().stream()
                    .map(actDto -> StateActivity.builder()
                            .id(actDto.getId())
                            .activityName(actDto.getActivityName())
                            .state(state) // maintain bidirectional relationship
                            .build())
                    .collect(Collectors.toList());
            state.setActivities(activities);
        } else {
            state.setActivities(new ArrayList<>());
        }

        log.info("Incoming State - GroupId: {}, MemberIds: {}", requestDto.getGroupId(), requestDto.getMemberIds());

        if (requestDto.getGroupId() != null) {
            var groupData = authIntegrationService.getGroupById(requestDto.getGroupId());
            if (groupData == null) {
                throw new RuntimeException("Invalid Group ID: " + requestDto.getGroupId());
            }

            state.setGroupId(requestDto.getGroupId());
            state.setGroupName(groupData.getName());

            // Try to get users from getUsersByGroupId endpoint
            List<UserDto> groupUsers = authIntegrationService.getUsersByGroupId(requestDto.getGroupId());
            List<Long> groupUserIds;
            
            if (groupUsers != null && !groupUsers.isEmpty()) {
                groupUserIds = groupUsers.stream()
                        .map(UserDto::getUserId)
                        .filter(userId -> userId != null)
                        .collect(Collectors.toList());
                log.info("Group '{}' (ID: {}) has {} users from getUsersByGroupId. User IDs: {}", 
                        groupData.getName(), requestDto.getGroupId(), groupUserIds.size(), groupUserIds);
            } else {
                // Fallback: Get userIds from getAllGroups if getUsersByGroupId returns empty
                log.warn("getUsersByGroupId returned empty for group ID: {}. Trying fallback with getAllGroups", requestDto.getGroupId());
                var allGroups = authIntegrationService.getAllGroups();
                if (allGroups != null) {
                    var groupFromAll = allGroups.stream()
                            .filter(g -> g.getGroupsId() != null && g.getGroupsId().equals(requestDto.getGroupId()))
                            .findFirst();
                    
                    if (groupFromAll.isPresent() && groupFromAll.get().getUserIds() != null) {
                        groupUserIds = new ArrayList<>(groupFromAll.get().getUserIds());
                        log.info("Group '{}' (ID: {}) has {} users from getAllGroups fallback. User IDs: {}", 
                                groupData.getName(), requestDto.getGroupId(), groupUserIds.size(), groupUserIds);
                    } else {
                        log.warn("Group ID {} not found in getAllGroups or has no userIds", requestDto.getGroupId());
                        groupUserIds = new ArrayList<>();
                    }
                } else {
                    groupUserIds = new ArrayList<>();
                }
            }
            
            final List<Long> finalGroupUserIds = groupUserIds;

            if (requestDto.getMemberIds() != null && !requestDto.getMemberIds().isEmpty()) {
                // Find which members don't belong to the group
                List<Long> invalidMemberIds = requestDto.getMemberIds().stream()
                        .filter(memberId -> !finalGroupUserIds.contains(memberId))
                        .collect(Collectors.toList());

                if (!invalidMemberIds.isEmpty()) {
                    log.error("Validation failed - Members {} do not belong to group '{}' (ID: {}). Group members: {}", 
                            invalidMemberIds, groupData.getName(), requestDto.getGroupId(), finalGroupUserIds);
                    throw new RuntimeException("Members " + invalidMemberIds + " do not belong to the assigned group: " + groupData.getName() + " (ID: " + requestDto.getGroupId() + ")");
                }
                state.setMemberIds(requestDto.getMemberIds());
            } else {
                state.setMemberIds(null);
            }
        } else {
            state.setGroupId(null);
            state.setGroupName(null);
            state.setMemberIds(null);
        }

        log.info("Saved State => groupId={}, groupName={}, memberIds={}",
                state.getGroupId(), state.getGroupName(), state.getMemberIds());
    }

    private WorkflowStateResponseDto mapToResponseDto(WorkflowState state) {
        // Map branches from entities
        List<WorkflowResponseDto.BranchDto> branchDtos = null;
        if (state.getBranches() != null && !state.getBranches().isEmpty()) {
            branchDtos = state.getBranches().stream()
                    .map(branch -> new WorkflowResponseDto.BranchDto(branch.getId(), branch.getBranchName()))
                    .collect(Collectors.toList());
        }
        
        // Map child from entities
        List<WorkflowResponseDto.ChildDto> childDtos = null;
        if (state.getChild() != null && !state.getChild().isEmpty()) {
            childDtos = state.getChild().stream()
                    .map(child -> new WorkflowResponseDto.ChildDto(child.getId(), child.getChildName()))
                    .collect(Collectors.toList());
        }
        
        return WorkflowStateResponseDto.builder()
                .id(state.getId())
                .stateName(state.getStateName())
                .orderNumber(state.getOrderNumber())
                .branches(branchDtos)
                .activities(state.getActivities() != null ?
                        state.getActivities().stream()
                                .map(act -> new StateActivityResponseDto(act.getId(), act.getActivityName()))
                                .collect(Collectors.toList()) : null)
                .child(childDtos)
                .groupId(state.getGroupId())
                .groupName(state.getGroupName())
                .memberIds(state.getMemberIds())
                .build();
    }

    private WorkflowStateResponseDto mapToResponseDtoWithUsers(WorkflowState state) {
        WorkflowStateResponseDto dto = mapToResponseDto(state);
        if (state.getMemberIds() != null && !state.getMemberIds().isEmpty()) {
            log.info("Fetching user details for memberIds: {} in state ID: {}", state.getMemberIds(), state.getId());
            List<UserDto> users = authIntegrationService.getUsersDetails(state.getMemberIds());
            log.info("Retrieved {} users for state ID: {}. Users: {}", users.size(), state.getId(), users);
            dto.setUsers(users);
        } else {
            log.debug("No memberIds found for state ID: {}, setting empty users list", state.getId());
            dto.setUsers(new ArrayList<>());
        }
        return dto;
    }
}
