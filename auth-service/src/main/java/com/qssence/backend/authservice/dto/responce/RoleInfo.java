package com.qssence.backend.authservice.dto.responce;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfo {

    private Long roleId;
    private String roleName;
    private String status;
}
