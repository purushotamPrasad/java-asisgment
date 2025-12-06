package com.qssence.backend.document_initiation_service.dto.request.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowStateRequestDto {

    private Long workflowId;
    private Long id;
    private String stateName;
    private Integer orderNumber;
    private List<StateActivityRequestDto> activities;

    private List<BranchDto> branches;
    private List<ChildDto> child;

    private Long groupId;
    private List<Long> memberIds;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BranchDto {
        private Long id;
        private String branchName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChildDto {
        private Long id;
        private String childName;
    }
}
