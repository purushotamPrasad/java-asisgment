package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dbo.Status;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.AddUserToGroupRequest;
import com.qssence.backend.authservice.dto.responce.AddUserToGroupResponse;

import java.util.Set;

public interface IAddUserToGroupService {

    ApiResponse<AddUserToGroupResponse> assignUserToGroups(AddUserToGroupRequest request);
    ApiResponse<AddUserToGroupResponse> getUserGroups(Long userId);
//    ApiResponse<AddUserToGroupResponse> updateUserGroups(AddUserToGroupRequest request);

    ApiResponse<String> updateUserGroupStatus(Long userId, Long groupId, Status status);

//    ApiResponse<String> removeUserFromGroups(Long userId, Set<Long> groupIds);

    ApiResponse<String> removeUserFromGroup(Long userId, Long groupId);
}
