package com.qssence.backend.document_initiation_service.controller;

import com.qssence.backend.document_initiation_service.dto.UserDto;
import com.qssence.backend.document_initiation_service.dto.response.GroupsResponseDto;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import com.qssence.backend.document_initiation_service.service.AuthIntegrationService;
import com.qssence.backend.document_initiation_service.dto.UserMasterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth-integration")
@RequiredArgsConstructor
public class AuthIntegrationController {

    private final AuthIntegrationService authIntegrationService;

    /**
     * Get all groups from auth-service for dropdown selection
     */
    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<List<GroupsResponseDto.GroupDto>>> getAllGroups() {
        try {
            List<GroupsResponseDto.GroupDto> groups = authIntegrationService.getAllGroups();
            return ResponseEntity.ok(new ApiResponse<>(true, "Groups fetched successfully", groups));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Failed to fetch groups: " + e.getMessage(), null));
        }
    }

    /**
     * Get users by group ID from auth-service
     */
    @GetMapping("/groups/{groupId}/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByGroupId(@PathVariable Long groupId) {
        try {
            List<UserDto> users = authIntegrationService.getUsersByGroupId(groupId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", users));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Failed to fetch users: " + e.getMessage(), null));
        }
    }

    @GetMapping("/all-Users")
public ResponseEntity<ApiResponse<List<UserMasterDto>>> getAllUsers() {
    try {
        List<UserMasterDto> users = authIntegrationService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", users));
    } catch (Exception e) {
        return ResponseEntity.ok(new ApiResponse<>(false, "Failed to fetch users: " + e.getMessage(), null));
    }
  }

    /**
     * Debug endpoint to test auth-service connection
     */
    @GetMapping("/debug/health")
    public ResponseEntity<ApiResponse<String>> testAuthServiceConnection() {
        try {
            String health = authIntegrationService.testAuthServiceConnection();
            return ResponseEntity.ok(new ApiResponse<>(true, "Auth service is accessible", health));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Auth service connection failed: " + e.getMessage(), null));
        }
    }

    /**
     * Debug endpoint to test group fetching
     */
    @GetMapping("/debug/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> testGroupFetching(@PathVariable Long groupId) {
        try {
            var groupData = authIntegrationService.getGroupById(groupId);
            if (groupData != null) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Group found", groupData));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(false, "Group not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Error fetching group: " + e.getMessage(), null));
        }
    }

    /**
     * Debug endpoint to test user fetching
     */
    @GetMapping("/debug/users")
    public ResponseEntity<ApiResponse<Object>> testUserFetching(@RequestParam List<Long> userIds) {
        try {
            List<UserDto> users = authIntegrationService.getUsersDetails(userIds);
            return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched", users));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Error fetching users: " + e.getMessage(), null));
        }
    }
}