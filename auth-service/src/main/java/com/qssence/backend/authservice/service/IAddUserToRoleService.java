package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dbo.Status;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.AddUserToRoleRequest;
import com.qssence.backend.authservice.dto.responce.AddUserToRoleResponse;

public interface IAddUserToRoleService {

    ApiResponse<AddUserToRoleResponse> assignUserToRoles(AddUserToRoleRequest request);
    ApiResponse<String> updateUserRoleStatus(Long userId, Long roleId, Status status);
    ApiResponse<AddUserToRoleResponse> getUserRoles(Long userId);
    ApiResponse<String> removeUserRole(Long userId, Long roleId);
}
