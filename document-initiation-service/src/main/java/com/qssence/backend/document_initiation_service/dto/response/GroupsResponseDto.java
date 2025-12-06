package com.qssence.backend.document_initiation_service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupsResponseDto {
    private Boolean success;
    private String message;
    private List<GroupDto> data;

    @Data
    public static class GroupDto {
        private Long groupsId;
        private String name;
        private String description;
        private List<Long> userIds;
    
    }
} 