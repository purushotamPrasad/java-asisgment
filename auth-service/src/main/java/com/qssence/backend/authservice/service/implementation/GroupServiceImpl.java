package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dbo.Group;
import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dto.UserMasterDto;
import com.qssence.backend.authservice.dto.request.GroupsRequest;
import com.qssence.backend.authservice.dto.responce.GroupResponseDto;
import com.qssence.backend.authservice.exception.ResourceNotFoundException;
import com.qssence.backend.authservice.mapper.UserMapper;
import com.qssence.backend.authservice.repository.GroupRepository;
import com.qssence.backend.authservice.repository.UserMasterRepository;
import com.qssence.backend.authservice.service.GroupNotificationService;
import com.qssence.backend.authservice.service.IGroupService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements IGroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupNotificationService notificationService;

    @Override
    public GroupResponseDto createGroup(GroupsRequest groupRequestDto) {
        Group group = Group.builder()
                .name(groupRequestDto.getName())
                .description(groupRequestDto.getDescription())
                .createdBy("admin") // TODO: Get from current user context
                .createdAt(java.time.LocalDateTime.now())
                .status("ACTIVE")
                .build();

        Group savedGroup = groupRepository.save(group);

        // Debug log to check the saved group's ID
        System.out.println("Saved Group ID: " + savedGroup.getGroupsId());

        // Add users to the group if provided
        if (groupRequestDto.getUserIds() != null && !groupRequestDto.getUserIds().isEmpty()) {
            addUsersToGroup(savedGroup.getGroupsId(), groupRequestDto.getUserIds());
            
            // Send notification to all users added to the group
            Set<UserMaster> users = userMasterRepository.findAllById(groupRequestDto.getUserIds()).stream()
                    .collect(Collectors.toSet());
            notificationService.sendBulkNotifications(users, 
                "You have been added to group: " + savedGroup.getName(), 
                "GROUP_MEMBERSHIP_ADDED");
        }

        // Add roles to the group if provided
        if (groupRequestDto.getRoleIds() != null && !groupRequestDto.getRoleIds().isEmpty()) {
            assignRolesToGroup(savedGroup.getGroupsId(), groupRequestDto.getRoleIds());
        }

        return mapToResponseDto(savedGroup);
    }


    @Override
    public List<GroupResponseDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(group -> {
                    GroupResponseDto dto = new GroupResponseDto();
                    dto.setGroupsId(group.getGroupsId());
                    dto.setName(group.getName());
                    dto.setDescription(group.getDescription());
                    dto.setStatus(group.getStatus()); // ✅ Include status in response
                    // Set of user IDs
                    dto.setUserIds(group.getUsers().stream()
                            .map(UserMaster::getUserId)
                            .collect(Collectors.toSet()));
                    // Set of full user details
                    dto.setUsers(group.getUsers().stream()
                            .map(userMapper::toDto)
                            .collect(Collectors.toSet()));
                    return dto;
                }).collect(Collectors.toList());
    }


    @Override
    public GroupResponseDto getGroupById(Long groupsId) {
        Group group = groupRepository.findById(groupsId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + groupsId));
        return mapToResponseDto(group);
    }


    @Override
    public void deleteGroupById(Long groupsId) {
        Group group = groupRepository.findById(groupsId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + groupsId));
        groupRepository.delete(group);
    }


    @Override
    public GroupResponseDto updateGroupById(Long groupsId, GroupsRequest groupRequestDto) {
        Group group = groupRepository.findById(groupsId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + groupsId));

        // Update name if provided
        if (groupRequestDto.getName() != null && !groupRequestDto.getName().trim().isEmpty()) {
            group.setName(groupRequestDto.getName());
        }
        
        // Update description if provided
        if (groupRequestDto.getDescription() != null) {
            group.setDescription(groupRequestDto.getDescription());
        }
        
        // ✅ Update status if provided (ACTIVE or INACTIVE)
        if (groupRequestDto.getStatus() != null && !groupRequestDto.getStatus().trim().isEmpty()) {
            String status = groupRequestDto.getStatus().toUpperCase();
            if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
                group.setStatus(status);
            } else {
                throw new IllegalArgumentException("Status must be either 'ACTIVE' or 'INACTIVE'");
            }
        }

        Group updatedGroup = groupRepository.save(group);
        return mapToResponseDto(updatedGroup);
    }

    @Override
    public GroupResponseDto addUsersToGroup(Long groupsId, List<Long> userIds) {
        Group group = groupRepository.findById(groupsId).orElseThrow(() -> new RuntimeException("Group not found"));
        Set<UserMaster> users = userMasterRepository.findAllById(userIds).stream().collect(Collectors.toSet());

        if (group.getUsers() == null) {
            group.setUsers(new HashSet<>());
        }

        group.getUsers().addAll(users);
        group = groupRepository.save(group);
        
        // 🔔 Send notification to users that they have been added to group
        for (UserMaster user : users) {
            notificationService.notifyGroupMembershipChange(user, group, "added to");
        }
        
        return mapToResponseDto(group);
    }

    @Override
    public GroupResponseDto removeUsersFromGroup(Long groupsId, List<Long> userIds) {
        Group group = groupRepository.findById(groupsId).orElseThrow(() -> new RuntimeException("Group not found"));
        Set<UserMaster> users = userMasterRepository.findAllById(userIds).stream().collect(Collectors.toSet());

        if (group.getUsers() != null) {
            group.getUsers().removeAll(users);
        }

        group = groupRepository.save(group);
        
        // 🔔 Send notification to users that they have been removed from group
        for (UserMaster user : users) {
            notificationService.notifyGroupMembershipChange(user, group, "removed from");
        }
        
        return mapToResponseDto(group);
    }

    @Override
    public GroupResponseDto getAllUsersInGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Set<UserMaster> users = group.getUsers();

        Set<UserMasterDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toSet());

        return GroupResponseDto.builder()
                .groupsId(group.getGroupsId())
                .name(group.getName())
                .users(userDtos)
                .build();
    }



    // Helper method to convert Group entity to GroupResponseDto

    // Enhanced: Assign roles to group
    public GroupResponseDto assignRolesToGroup(Long groupId, List<Long> roleIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // TODO: Implement role assignment logic
        // This would require UserRoleRepository and role assignment logic
        
        return mapToResponseDto(group);
    }

    // Enhanced: Bulk user assignment to multiple groups
    public String assignUsersToMultipleGroups(List<Long> userIds, List<Long> groupIds) {
        try {
            Set<UserMaster> users = userMasterRepository.findAllById(userIds).stream()
                    .collect(Collectors.toSet());
            
            List<Group> groups = groupRepository.findAllById(groupIds);
            
            for (Group group : groups) {
                if (group.getUsers() == null) {
                    group.setUsers(new HashSet<>());
                }
                group.getUsers().addAll(users);
                groupRepository.save(group);
            }
            
            return "Successfully assigned " + users.size() + " users to " + groups.size() + " groups";
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign users to groups: " + e.getMessage());
        }
    }

    // Enhanced: Remove users from multiple groups
    public String removeUsersFromMultipleGroups(List<Long> userIds, List<Long> groupIds) {
        try {
            Set<UserMaster> users = userMasterRepository.findAllById(userIds).stream()
                    .collect(Collectors.toSet());
            
            List<Group> groups = groupRepository.findAllById(groupIds);
            
            for (Group group : groups) {
                if (group.getUsers() != null) {
                    group.getUsers().removeAll(users);
                    groupRepository.save(group);
                }
            }
            
            return "Successfully removed " + users.size() + " users from " + groups.size() + " groups";
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove users from groups: " + e.getMessage());
        }
    }

    // Enhanced: Get group with all details (users, roles, permissions)
    public GroupResponseDto getGroupWithDetails(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        GroupResponseDto dto = mapToResponseDto(group);
        
        // Add role information
        if (group.getRoles() != null) {
            dto.setRoleIds(group.getRoles().stream()
                    .map(role -> role.getUserRoleId())
                    .collect(Collectors.toSet()));
        }
//
//        // Add permission information
//        if (group.getPermissions() != null && !group.getPermissions().isEmpty()) {
//            Set<Long> permissionIds = new HashSet<>();
//            for (Object obj : group.getPermissions()) {
//                Permission permission = (Permission) obj;
//                permissionIds.add((long) permission.getPermissionId().hashCode());
//            }
//            dto.setPermissionIds(permissionIds);
//        }
//
        return dto;
    }

    // Enhanced: Check for permission conflicts in group
    public List<String> checkGroupPermissionConflicts(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        List<String> conflicts = new java.util.ArrayList<>();
        
        // Check for overlapping permissions between roles
        if (group.getRoles() != null && group.getRoles().size() > 1) {
            // TODO: Implement conflict detection logic
            conflicts.add("Potential role permission conflicts detected");
        }
        
        return conflicts;
    }

    // Helper method to convert Group entity to GroupResponseDto
    private GroupResponseDto mapToResponseDto(Group group) {
        return GroupResponseDto.builder()
                .groupsId(group.getGroupsId())
                .name(group.getName())
                .description(group.getDescription())
                .status(group.getStatus()) // ✅ Include status in response
                .userIds(group.getUsers() != null ? group.getUsers().stream()
                        .map(UserMaster::getUserId)
                        .collect(Collectors.toSet()) : new HashSet<>())
                .build();
    }

}
