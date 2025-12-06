package com.qssence.backend.authservice.service;
import com.qssence.backend.authservice.dbo.Permission.Permission;
import com.qssence.backend.authservice.dbo.Permission.PermissionLevelGroup;
import com.qssence.backend.authservice.dbo.PermissionGroupMapping;
import com.qssence.backend.authservice.dbo.PermissionRoleMapping;
import com.qssence.backend.authservice.dbo.PermissionUserMapping;
import com.qssence.backend.authservice.dto.request.AddPermissionsForRoleRequest;
import com.qssence.backend.authservice.dto.request.AddPermissionsForUserRequest;
import com.qssence.backend.authservice.dto.responce.Permission.PermissionGroup;
import com.qssence.backend.authservice.enums.PermissionAccessLevel;

import java.util.List;
import java.util.UUID;

public interface IPermissionService {
    Permission createPermission(Permission permission);
    void createPermissionLevelGroup(PermissionLevelGroup permissionLevelGroup);
    void updatePermissionLevels(UUID permissionId, PermissionAccessLevel levels);
    boolean isPermissionExist(UUID permissionId);
    List<PermissionLevelGroup> getPermissionLevel(UUID permissionId);


    Permission findPermissionByName(String name);

    Permission getPermissionById(UUID permissionId);

    List<Permission> getAllPermissions();

    Permission updatePermission(UUID permissionId, Permission updatedPermission);

    boolean deletePermission(UUID permissionId);

    PermissionGroupMapping setPermissionForGroup(PermissionGroupMapping permissionGroupMapping);
    List<PermissionGroup> getAllPermissionsByGroupId(UUID groupId);
    void deletePermissionByGroupIdAndPermissionId(UUID groupId, UUID permissionId);
    void deletePermissionByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
    void deletePermissionByUserIdAndPermissionId(UUID userId, UUID permissionId);


    List<PermissionRoleMapping> setPermissionsForRole(AddPermissionsForRoleRequest permissionRoleMapping);
    List<PermissionGroup> getAllPermissionsByRoleId(UUID roleId);
    boolean removePermissionsFromRole(AddPermissionsForRoleRequest permissionRoleMapping);


    List<PermissionUserMapping> setPermissionsForUser(AddPermissionsForUserRequest addPermissionsForUserRequest);
    List<PermissionGroup> getAllPermissionsByUserId(UUID userId);
    boolean removePermissionsFromUser(AddPermissionsForUserRequest addPermissionsForUserRequest);
}
