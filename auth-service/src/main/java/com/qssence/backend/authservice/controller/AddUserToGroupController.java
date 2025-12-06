package com.qssence.backend.authservice.controller;

import com.qssence.backend.authservice.dbo.Status;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.AddUserToGroupRequest;
import com.qssence.backend.authservice.dto.responce.AddUserToGroupResponse;
import com.qssence.backend.authservice.service.implementation.AddUserToGroupServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/UserGroup")
@RequiredArgsConstructor
public class AddUserToGroupController {

    @Autowired
    private final AddUserToGroupServiceImpl addUserToGroupService;

    /**
     * Assign groups to a user
     */
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<AddUserToGroupResponse>> assignGroupsToUser(@RequestBody AddUserToGroupRequest request) {
        try {
            ApiResponse<AddUserToGroupResponse> response = addUserToGroupService.assignUserToGroups(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

    /**
     * Update groups for a user
     */
    // ✅ API to update a user's group status
    @PutMapping("/update/user/{userId}/group/{groupId}/status/{status}")
    public ResponseEntity<ApiResponse<String>> updateUserGroupStatus(
            @PathVariable Long userId,
            @PathVariable Long groupId,
            @PathVariable Status status) {

        ApiResponse<String> response = addUserToGroupService.updateUserGroupStatus(userId, groupId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Get groups assigned to a user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AddUserToGroupResponse>> getUserGroups(@PathVariable Long userId) {
        try {
            ApiResponse<AddUserToGroupResponse> response = addUserToGroupService.getUserGroups(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }

    /**
     * Remove groups from a user
     */
    @DeleteMapping("/remove/{userId}/group/{groupId}")
    public ResponseEntity<ApiResponse<String>> removeUserGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        try {
            ApiResponse<String> response = addUserToGroupService.removeUserFromGroup(userId, groupId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
        }
    }
}
