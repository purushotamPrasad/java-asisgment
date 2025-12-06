package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dto.request.GroupsRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;


import java.util.List;
import java.util.UUID;

public interface IGroupsService {
    Response createGroup(GroupsRequest groupsRequest);
    List<GroupRepresentation> getAllGroups();
    GroupRepresentation getGroupById(UUID groupsId);
    GroupRepresentation getGroupsByName(String name);
    void deleteGroupsById(UUID groupsId);
    boolean deleteGroupsByName(String name);
    GroupRepresentation updateGroupsById(UUID groupsId, GroupsRequest groupsRequest);
    GroupRepresentation updateGroupsByName(String name, GroupsRequest groupsRequest);
    List<GroupRepresentation> getGroupsByUserId(String userId);
    UserRepresentation addUserToGroupByUserId(String userId, String addUserToGroupRequest);
    List<UserRepresentation> addUsersToGroupByGroupId(String groupId, List<String> userIds);
    List<UserRepresentation> getAllGroupMembersByGroupId(UUID groupsId);
    List<GroupRepresentation> addGroupsToUserByUserId(String userId, List<String> groupIds);
    List<RoleRepresentation> getAllRolesByGroupId(UUID groupsId);
    boolean removeUsersFromGroupByGroupId(String groupId, List<String> userIds);
    boolean removeGroupsFromUserByUserId(String userId, List<String> groupIds);
    void deleteUserFromGroupByUserId(String userId, String groupId);
    void assignRoleToGroup(String groupId, String roleId);
}
