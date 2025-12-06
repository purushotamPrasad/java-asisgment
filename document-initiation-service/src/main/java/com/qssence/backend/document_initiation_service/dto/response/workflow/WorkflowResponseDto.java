package com.qssence.backend.document_initiation_service.dto.response.workflow;

import com.qssence.backend.document_initiation_service.dto.response.PlantResponse;
import com.qssence.backend.document_initiation_service.enums.WorkflowType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class WorkflowResponseDto {

    private Long id;
    private String workflowName;
    private WorkflowType workflowType;
    private String region;
    private String country;
    private Long plantId;
    // 🔹 नया field for Plant Details
    private PlantResponse plant;
    private List<WorkflowStateResponseDto> states;

    // 🔹 Branch DTO for WorkflowResponseDto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BranchDto {
        private Long id;
        private String branchName;
    }

    // 🔹 Child DTO for WorkflowResponseDto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChildDto {
        private Long id;
        private String childName;
    }
}
