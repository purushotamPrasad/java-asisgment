package com.qssence.backend.authservice.dto.responce.User;


import com.qssence.backend.authservice.dto.responce.Permission.PermissionGroup;
import com.qssence.backend.authservice.dto.responce.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {
    private String id;
    private String email;
    private String fullName;
    private String username;
    private Boolean status;
    private String role;
    List<RoleResponse> roles = new ArrayList<>();
    List<RoleResponse> groups = new ArrayList<>();
    List<PermissionGroup> permissions = new ArrayList<>();
}
