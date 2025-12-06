package com.qssence.backend.authservice.dbo.Permission;

import com.qssence.backend.authservice.enums.PermissionType;
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
@Table(name = "permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Hidden
    private UUID permissionId;

    private String permissionName;

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;

}
