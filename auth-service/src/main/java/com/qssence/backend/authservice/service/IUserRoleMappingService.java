package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dbo.UserRoleMapping;

public interface IUserRoleMappingService {
    UserRoleMapping assignRoleToUser(String roleId, String userId);
}
