package com.qssence.backend.authservice.dto.request;

import com.qssence.backend.authservice.enums.PermissionAccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionLevelRequest {
    private PermissionAccessLevel accessLevel;
    private boolean accessLevelValue;
}
