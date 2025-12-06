package com.qssence.backend.authservice.dto.responce.Permission;


import com.qssence.backend.authservice.enums.PermissionAccessLevelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionLevelGroupResponse {
    private PermissionAccessLevelType accessLevel;
    private boolean accessLevelValue;
}
