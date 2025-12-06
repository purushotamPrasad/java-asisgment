package com.qssence.backend.document_initiation_service.dto.response.document;

import com.qssence.backend.document_initiation_service.dto.UserMasterDto;
import com.qssence.backend.document_initiation_service.dto.response.workflow.WorkflowResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFlowResponseDto {

        private Long id;

        private String documentTypeName;
        private String subTypeName;
        private String classificationName;
        private WorkflowResponseDto workflowDetails;
        private List<UserMasterDto> queryMembers;
}
