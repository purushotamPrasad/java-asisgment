package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dto.UserMasterDto;
import com.qssence.backend.authservice.dto.request.GroupsRequest;
import com.qssence.backend.authservice.dto.responce.GroupResponseDto;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IGroupService {

    // Create a new group
    GroupResponseDto createGroup(GroupsRequest groupRequestDto);

    // Get all groups
    List<GroupResponseDto> getAllGroups();

    // Get group by ID
    GroupResponseDto getGroupById(Long groupsId);

    // Delete group by ID
    void deleteGroupById(Long groupsId);


    // Update group by ID
    GroupResponseDto updateGroupById(Long groupsId, GroupsRequest groupRequestDto);

    // Add users to group
    GroupResponseDto addUsersToGroup(Long groupsId, List<Long> userIds);

    // Remove users from group
    GroupResponseDto removeUsersFromGroup(Long groupsId, List<Long> userIds);

    GroupResponseDto getAllUsersInGroup(Long groupId);

    // Get all users in a group
//    List<UserMasterDto> getAllUsersInGroup(Long groupsId);

}
