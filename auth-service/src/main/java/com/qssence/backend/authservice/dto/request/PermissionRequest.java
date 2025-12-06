package com.qssence.backend.authservice.dto.request;

import com.qssence.backend.authservice.enums.PermissionAccessLevel;
import jakarta.validation.Valid;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionRequest {
    private String permissionName;
    private String permissionType;

    @Valid
    private PermissionAccessLevel levels;
}