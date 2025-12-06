package com.qssence.backend.document_initiation_service.dto.request.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFlowRequestDto {

        private Long documentTypeId;
        private Long subTypeId;
        private Long classificationId;
        private Long workflowId;
        private List<Long> queryMemberIds; // user IDs to be fetched later

}
