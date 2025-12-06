package com.qssence.backend.document_initiation_service.dto.response;

import com.qssence.backend.document_initiation_service.dto.UserDto;
import lombok.Data;

import java.util.List;

@Data
public class GroupUsersResponseDto {
    private Boolean success;
    private String message;
    private GroupUsersData data;

    @Data
    public static class GroupUsersData {
        private Long groupsId;
        private String name;
        private String description;
        private List<UserDto> users;
    }
} 