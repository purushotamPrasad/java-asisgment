package com.qssence.backend.authservice.dto.responce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddUserToRoleResponse {

    private Long userId;
    private String userName;
    private Set<RoleInfo> roles;  // Ab yahan groupId aur groupName dono milenge
    private String status;


}
