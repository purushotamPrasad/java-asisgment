package com.qssence.backend.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleMappingDto implements Serializable {
    private String roleId;
    private String userId;
}
