package com.qssence.backend.document_initiation_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionRequest {
    private Long userId;
    private Long documentId;
    private String action;


}
