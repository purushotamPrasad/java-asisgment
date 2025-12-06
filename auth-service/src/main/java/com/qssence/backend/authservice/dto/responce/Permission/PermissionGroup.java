package com.qssence.backend.authservice.dto.responce.Permission;


import com.qssence.backend.authservice.enums.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionGroup {
    private UUID permissionId;
    private String permissionName;
    private PermissionType permissionType;
    private List<PermissionLevelGroupResponse> levelGroups;
}
