package com.qssence.backend.authservice.controller;

import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.UserMasterDto;
import com.qssence.backend.authservice.dto.request.GroupsRequest;
import com.qssence.backend.authservice.dto.responce.GroupResponseDto;
import com.qssence.backend.authservice.service.implementation.GroupServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    @Autowired
    private GroupServiceImpl groupService;

    // Create a new group
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<GroupResponseDto>> createGroup(@Valid @RequestBody GroupsRequest groupRequestDto) {
        ApiResponse<GroupResponseDto> response = new ApiResponse<>();

        try {
            // Check if the group name is provided
            if (groupRequestDto.getName() == null || groupRequestDto.getName().trim().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Group name is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Create the group
            GroupResponseDto createdGroup = groupService.createGroup(groupRequestDto);
            if (createdGroup != null) {
                response.setSuccess(true);
                response.setMessage("Group created successfully");
                response.setData(createdGroup);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.setSuccess(false);
                response.setMessage("Group creation failed");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to create group: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all groups
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<GroupResponseDto>>> getAllGroups() {
        ApiResponse<List<GroupResponseDto>> response = new ApiResponse<>();

        try {
            List<GroupResponseDto> groups = groupService.getAllGroups();

            // Log the retrieved groups for debugging
            System.out.println("Retrieved Groups: " + groups);

            if (!groups.isEmpty()) {
                response.setSuccess(true);
                response.setMessage("Groups retrieved successfully");
                response.setData(groups);
            } else {
                response.setSuccess(true);
                response.setMessage("No groups found");
                response.setData(groups);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Log the error for debugging
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Failed to retrieve groups: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Get group by ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponse<GroupResponseDto>> getGroupById(@PathVariable("id") Long groupsId) {
        ApiResponse<GroupResponseDto> response = new ApiResponse<>();

        try {
            GroupResponseDto group = groupService.getGroupById(groupsId);
            if (group != null) {
                response.setSuccess(true);
                response.setMessage("Group retrieved successfully");
                response.setData(group);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setSuccess(false);
                response.setMessage("Group not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to retrieve group: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update group by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<GroupResponseDto>> updateGroupById(@PathVariable("id") Long groupsId, @RequestBody GroupsRequest groupRequestDto) {
        ApiResponse<GroupResponseDto> response = new ApiResponse<>();
        try {
            GroupResponseDto updatedGroup = groupService.updateGroupById(groupsId, groupRequestDto);
            if (updatedGroup != null) {
                response.setSuccess(true);
                response.setMessage("Group updated successfully");
                response.setData(updatedGroup);
                return ResponseEntity.ok(response);
            } else {
                response.setSuccess(false);
                response.setMessage("Group update failed");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to update group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Delete group by ID
    @DeleteMapping("/deleteGroup/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGroupById(@PathVariable("id") Long groupsId) {
        ApiResponse<Void> response = new ApiResponse<>();
        try {
            groupService.deleteGroupById(groupsId);
            response.setSuccess(true);
            response.setMessage("Group deleted successfully");
            return ResponseEntity.ok(response); // Change to HttpStatus.OK
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to delete group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Enhanced: Assign users to multiple groups (bulk operation)
    @PostMapping("/bulk/assignUsers")
    public ResponseEntity<ApiResponse<String>> assignUsersToMultipleGroups(
            @RequestBody BulkGroupAssignmentRequest request) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            String result = groupService.assignUsersToMultipleGroups(request.getUserIds(), request.getGroupIds());
            response.setSuccess(true);
            response.setMessage(result);
            response.setData(result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to assign users to groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Enhanced: Remove users from multiple groups (bulk operation)
    @PostMapping("/bulk/removeUsers")
    public ResponseEntity<ApiResponse<String>> removeUsersFromMultipleGroups(
            @RequestBody BulkGroupAssignmentRequest request) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            String result = groupService.removeUsersFromMultipleGroups(request.getUserIds(), request.getGroupIds());
            response.setSuccess(true);
            response.setMessage(result);
            response.setData(result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to remove users from groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Enhanced: Get group with all details (users, roles, permissions)
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<GroupResponseDto>> getGroupWithDetails(@PathVariable Long id) {
        ApiResponse<GroupResponseDto> response = new ApiResponse<>();
        try {
            GroupResponseDto group = groupService.getGroupWithDetails(id);
            response.setSuccess(true);
            response.setMessage("Group details retrieved successfully");
            response.setData(group);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to retrieve group details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Enhanced: Check for permission conflicts in group
    @GetMapping("/{id}/conflicts")
    public ResponseEntity<ApiResponse<List<String>>> checkGroupPermissionConflicts(@PathVariable Long id) {
        ApiResponse<List<String>> response = new ApiResponse<>();
        try {
            List<String> conflicts = groupService.checkGroupPermissionConflicts(id);
            response.setSuccess(true);
            response.setMessage("Permission conflicts checked successfully");
            response.setData(conflicts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to check permission conflicts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Enhanced: Assign roles to group
    @PostMapping("/{id}/assignRoles")
    public ResponseEntity<ApiResponse<GroupResponseDto>> assignRolesToGroup(
            @PathVariable Long id, @RequestBody List<Long> roleIds) {
        ApiResponse<GroupResponseDto> response = new ApiResponse<>();
        try {
            GroupResponseDto group = groupService.assignRolesToGroup(id, roleIds);
            response.setSuccess(true);
            response.setMessage("Roles assigned to group successfully");
            response.setData(group);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to assign roles to group: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Enhanced: Get all groups with user count
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<GroupSummaryDto>>> getGroupsSummary() {
        ApiResponse<List<GroupSummaryDto>> response = new ApiResponse<>();
        try {
            List<GroupResponseDto> groups = groupService.getAllGroups();
            List<GroupSummaryDto> summaries = groups.stream()
                    .map(group -> GroupSummaryDto.builder()
                            .groupId(group.getGroupsId())
                            .groupName(group.getName())
                            .userCount(group.getUserIds() != null ? group.getUserIds().size() : 0)
                            .description(group.getDescription())
                            .build())
                    .collect(java.util.stream.Collectors.toList());
            
            response.setSuccess(true);
            response.setMessage("Groups summary retrieved successfully");
            response.setData(summaries);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to retrieve groups summary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // DTO for bulk operations
    public static class BulkGroupAssignmentRequest {
        private List<Long> userIds;
        private List<Long> groupIds;
        
        // Getters and setters
        public List<Long> getUserIds() { return userIds; }
        public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
        public List<Long> getGroupIds() { return groupIds; }
        public void setGroupIds(List<Long> groupIds) { this.groupIds = groupIds; }
    }

    // DTO for group summary
    public static class GroupSummaryDto {
        private Long groupId;
        private String groupName;
        private int userCount;
        private String description;
        
        // Builder pattern
        public static GroupSummaryDtoBuilder builder() {
            return new GroupSummaryDtoBuilder();
        }
        
        public static class GroupSummaryDtoBuilder {
            private Long groupId;
            private String groupName;
            private int userCount;
            private String description;
            
            public GroupSummaryDtoBuilder groupId(Long groupId) { this.groupId = groupId; return this; }
            public GroupSummaryDtoBuilder groupName(String groupName) { this.groupName = groupName; return this; }
            public GroupSummaryDtoBuilder userCount(int userCount) { this.userCount = userCount; return this; }
            public GroupSummaryDtoBuilder description(String description) { this.description = description; return this; }
            
            public GroupSummaryDto build() {
                GroupSummaryDto dto = new GroupSummaryDto();
                dto.groupId = this.groupId;
                dto.groupName = this.groupName;
                dto.userCount = this.userCount;
                dto.description = this.description;
                return dto;
            }
        }
        
        // Getters and setters
        public Long getGroupId() { return groupId; }
        public void setGroupId(Long groupId) { this.groupId = groupId; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public int getUserCount() { return userCount; }
        public void setUserCount(int userCount) { this.userCount = userCount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

}
