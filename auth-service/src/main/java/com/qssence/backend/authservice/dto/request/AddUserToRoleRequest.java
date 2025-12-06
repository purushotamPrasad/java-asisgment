package com.qssence.backend.authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddUserToRoleRequest {
    private Long userId;  // Existing user
    private Set<Long> roleIds; // Multiple roles assigned to user
    private String status;  // "ACTIVE" or "INACTIVE"

}
