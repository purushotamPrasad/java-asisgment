package com.qssence.backend.document_initiation_service.dto.response.workflow;

import com.qssence.backend.document_initiation_service.dto.UserDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowStateResponseDto {

    private Long id;
    private String stateName;
    private Integer orderNumber;
    private List<StateActivityResponseDto> activities;

    private List<WorkflowResponseDto.BranchDto> branches;
    private List<WorkflowResponseDto.ChildDto> child;

    private Long groupId;
    private String groupName;
    private List<Long> memberIds;
    private List<UserDto> users; // Add this line


}
