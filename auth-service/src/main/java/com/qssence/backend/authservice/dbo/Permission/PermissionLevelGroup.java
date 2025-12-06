package com.qssence.backend.authservice.dbo.Permission;


import com.qssence.backend.authservice.enums.PermissionAccessLevelType;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(PermissionLevelGroupId.class)
@Table(name = "PermissionLevelGroup")
public class PermissionLevelGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Hidden
    private UUID permissionLevelId;

    @Id
    @Hidden
    private UUID permissionId;

    private PermissionAccessLevelType accessLevel;

    private boolean accessLevelValue;
}
