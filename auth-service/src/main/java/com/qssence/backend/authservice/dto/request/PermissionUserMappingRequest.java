package com.qssence.backend.authservice.dto.request;

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
public class PermissionUserMappingRequest implements Serializable {
    private UUID userId;
    private UUID permissionId;
}
