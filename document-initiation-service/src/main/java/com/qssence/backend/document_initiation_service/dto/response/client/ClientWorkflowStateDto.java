package com.qssence.backend.document_initiation_service.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientWorkflowStateDto {
    private Long id;
    private String stateName;
    private Integer orderNumber;
    private List<String> branches;
    private List<String> child;
    private Long groupId;
    private String groupName;
    private List<Long> memberIds;
}
