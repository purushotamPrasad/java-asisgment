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
public class AddUserToGroupRequest {
    private Long userId;
    private Set<Long> groupIds;
    private String status;  // "ACTIVE" or "INACTIVE"

}
