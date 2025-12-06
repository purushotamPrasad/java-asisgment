package com.qssence.backend.authservice.dbo.Permission;


import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@IdClass(PermissionLevelGroupId.class)
public class PermissionLevelGroupId implements Serializable {
    @Hidden
    private UUID permissionId;
    @Hidden
    private UUID permissionLevelId;

}
