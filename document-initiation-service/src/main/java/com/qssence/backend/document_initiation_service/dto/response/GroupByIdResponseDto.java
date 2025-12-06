package com.qssence.backend.document_initiation_service.dto.response;

import lombok.Data;

@Data
public class GroupByIdResponseDto {
    private Boolean success;
    private String message;
    private GroupData data;

    @Data
    public static class GroupData {
        private Long groupsId;
        private String name;
        private String description;
    }
} 