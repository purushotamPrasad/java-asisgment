package com.qssence.backend.authservice.dto.responce.Permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRoleMappingResponse implements Serializable {
    private UUID roleId;
    private UUID permissionId;
    private String permissionName;
}
