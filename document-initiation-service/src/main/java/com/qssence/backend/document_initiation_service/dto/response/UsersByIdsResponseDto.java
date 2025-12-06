package com.qssence.backend.document_initiation_service.dto.response;

import com.qssence.backend.document_initiation_service.dto.UserMasterDto;
import lombok.Data;

import java.util.List;

@Data
public class UsersByIdsResponseDto {
    private Boolean success;
    private String message;
    private List<UserMasterDto> data;
} 