package com.qssence.backend.authservice.service.implementation;


import com.qssence.backend.authservice.dto.request.GroupsRequest;
import com.qssence.backend.authservice.service.IGroupsService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.ArrayList;
@Service
@RequiredArgsConstructor
@Slf4j
public class GroupsService implements IGroupsService {
    private static final Logger logger = LoggerFactory.getLogger(GroupsService.class);

    @Value("${keycloak.realm}")
    private String realmName;

    @Autowired
    private RestTemplate restTemplate;
    private final Keycloak keycloak;


    @Autowired
    public GroupsService(RestTemplate restTemplate, Keycloak keycloak) {
        this.restTemplate = restTemplate;
        this.keycloak = keycloak;
    }

    public Response createGroup(GroupsRequest groupsRequest) {
        try {
            logger.info("Creating group...");
            String trimmedName = StringUtils.trimToEmpty(groupsRequest.getName());
            String trimmedDescription = StringUtils.trimToEmpty(groupsRequest.getDescription());
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            GroupsResource groupsResource = realmResource.groups();
            GroupRepresentation groupExists = checkIfGroupExists(trimmedName);
            if (groupExists != null) {
                logger.warn("Group already exists.");
                return Response.status(Response.Status.CONFLICT)
                        .entity("Group already exists.")
                        .build();
            }
            Map<String, List<String>> attributes = new HashMap<>();
            if (trimmedDescription != null && !trimmedDescription.isEmpty()) {
                attributes.put("description", List.of(trimmedDescription));
            }
            GroupRepresentation groupRepresentation = new GroupRepresentation();
            groupRepresentation.setAttributes(attributes);
            groupRepresentation.setName(trimmedName);
            groupRepresentation.setPath("/" + trimmedName);
            groupsResource.add(groupRepresentation);
            logger.info("Group created successfully.");
            return Response.status(Response.Status.CREATED).build();
        }catch (Exception e) {
            logger.error("Error occurred while creating group: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create role: " + e.getMessage())
                    .build();
        }
    }
    private GroupRepresentation checkIfGroupExists(String groupsName) {
        GroupRepresentation existingGroup = getGroupsByName(groupsName);
        return existingGroup;
    }
    @Override
    public List<GroupRepresentation> getAllGroups() {
        try {
            logger.info("Retrieving all groups...");
            List<GroupRepresentation> groups = keycloak.realm("QssenceRealm").groups().groups();

            Collections.sort(groups, new Comparator<GroupRepresentation>() {
                @Override
                public int compare(GroupRepresentation group1, GroupRepresentation group2) {
                    return group1.getName().compareToIgnoreCase(group2.getName());
                }
            });

            return groups;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all groups: {}", e.getMessage(), e);
            return null;
        }
    }


    @Override
    public GroupRepresentation getGroupById(UUID groupsId) {
        try {
            logger.info("Retrieving group by ID: {}", groupsId);
            return keycloak.realm("QssenceRealm").groups().group(groupsId.toString()).toRepresentation();
        } catch (Exception e) {
            logger.error("Error occurred while retrieving group by ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public GroupRepresentation getGroupsByName(String name) {
        try {
            logger.info("Retrieving group by name: {}", name);
            return keycloak.realm("QssenceRealm").groups().groups().stream()
                    .filter(group -> group.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving group by name: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteGroupsById(UUID groupsId) {
        try {
            logger.info("Deleting group by ID: {}", groupsId);
            keycloak.realm("QssenceRealm").groups().group(groupsId.toString()).remove();
            logger.info("Group deleted successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting group by ID: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteGroupsByName(String name) {
        try {
            logger.info("Deleting group by name: {}", name);
            List<GroupRepresentation> groups = keycloak.realm("QssenceRealm").groups().groups();
            Optional<GroupRepresentation> groupToDelete = groups.stream()
                    .filter(group -> group.getName().equals(name))
                    .findFirst();
            if (groupToDelete.isPresent()) {
                keycloak.realm("QssenceRealm").groups().group(groupToDelete.get().getId()).remove();
                logger.info("Group deleted successfully.");
                return true;
            } else {
                logger.warn("Group with name {} not found.", name);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error occurred while deleting group by name: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public GroupRepresentation updateGroupsById(UUID groupsId, GroupsRequest groupsRequest) {
        try {
            logger.info("Updating group by ID: {}", groupsId);
            GroupRepresentation groupRepresentation = getGroupById(groupsId);
            if(groupRepresentation != null) {
                String trimmedName = StringUtils.trimToEmpty(groupsRequest.getName());
                String trimmedDescription = StringUtils.trimToEmpty(groupsRequest.getDescription());
                Map<String, List<String>> attributes = new HashMap<>();
                if (trimmedDescription != null && !trimmedDescription.isEmpty()) {
                    attributes.put("description", List.of(trimmedDescription));
                }
                groupRepresentation.setName(trimmedName);
                groupRepresentation.setPath("/" + trimmedName);
                groupRepresentation.setAttributes(attributes);
                keycloak.realm("QssenceRealm").groups().group(groupsId.toString()).update(groupRepresentation);
                logger.info("Group updated successfully.");
            }
            return groupRepresentation;
        } catch (Exception e) {
            logger.error("Error occurred while updating group by ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void assignRoleToGroup(String groupId, String roleId) {
        try {
            RealmResource realmResource = keycloak.realm(realmName);
            GroupsResource groupsResource = realmResource.groups();
            RoleMappingResource roleMappingResource = groupsResource.group(groupId).roles();
            RoleResource roleResource = realmResource.roles().get(roleId);
            RoleRepresentation roleRepresentation = roleResource.toRepresentation();
            roleMappingResource.realmLevel().add(Collections.singletonList(roleRepresentation));
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign role to group", e);
        }
    }

    @Override
    public GroupRepresentation updateGroupsByName(String name, GroupsRequest groupsRequest) {
        try {
            logger.info("Updating group by name: {}", name);
            GroupRepresentation groupRepresentation = getGroupsByName(name);
            if(groupRepresentation != null) {
                String trimmedName = StringUtils.trimToEmpty(groupsRequest.getName());
                String trimmedDescription = StringUtils.trimToEmpty(groupsRequest.getDescription());
                Map<String, List<String>> attributes = new HashMap<>();
                if (trimmedDescription != null && !trimmedDescription.isEmpty()) {
                    attributes.put("description", List.of(trimmedDescription));
                }
                groupRepresentation.setName(trimmedName);
                groupRepresentation.setPath("/" + trimmedName);
                groupRepresentation.setAttributes(attributes);
                keycloak.realm("QssenceRealm").groups().group(groupRepresentation.getId()).update(groupRepresentation);
                logger.info("Group updated successfully.");
            }
            return groupRepresentation;
        } catch (Exception e) {
            logger.error("Error occurred while updating group by name: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<GroupRepresentation> getGroupsByUserId(String userId) {
        try {
            return keycloak.realm("QssenceRealm").users().get(userId).groups();
        } catch (NotFoundException e) {
            logger.warn("User not found: " + userId);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error occurred while retrieving groups for user ID: " + userId, e);
            throw new RuntimeException("Failed to retrieve groups for user ID: " + userId, e);
        }
    }

    public List<UserRepresentation> addUsersToGroupByGroupId(String groupId, List<String> userIds) {
        GroupRepresentation groupRepresentation = keycloak.realm("QssenceRealm").groups().group(groupId).toRepresentation();
        if(groupRepresentation == null){
            return null;
        }
        List<UserRepresentation> addedUsers = new ArrayList<>();
        for (String userId : userIds) {
            try {
                UserRepresentation user = addUserToGroupByUserId(userId, groupId);
                if(user != null){
                    addedUsers.add(user);
                }else{
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return addedUsers;
    }


    @Override
    public UserRepresentation addUserToGroupByUserId(String userId, String addUserToGroupRequest) {
        try {
            UserResource userResource = keycloak.realm("QssenceRealm").users().get(userId);
            if(userResource == null){
                return null;
            }
            userResource.joinGroup(addUserToGroupRequest);
            return userResource.toRepresentation();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add user to group", e);
        }
    }
    @Override
    public List<GroupRepresentation> addGroupsToUserByUserId(String userId, List<String> groupIds) {
        try {
            UserResource userResource = keycloak.realm("QssenceRealm").users().get(userId);
            List<GroupRepresentation> addedGroups = new ArrayList<>();
            for (String groupId : groupIds) {
                userResource.joinGroup(groupId);
                addedGroups.add(keycloak.realm("QssenceRealm").groups().group(groupId).toRepresentation());
            }
            return addedGroups;
        } catch (NotFoundException e) {
            // Handle not found exception
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean removeUsersFromGroupByGroupId(String groupId, List<String> userIds) {
        try {
            GroupResource groupResource = keycloak.realm("QssenceRealm").groups().group(groupId);
            for (String userId : userIds) {
                deleteUserFromGroupByUserId(userId,groupId);
            }
            return true;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean removeGroupsFromUserByUserId(String userId, List<String> groupIds) {
        try {
            UserResource userResource = keycloak.realm("QssenceRealm").users().get(userId);
            for (String groupId : groupIds) {
                userResource.leaveGroup(groupId);
            }
            return true;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public List<UserRepresentation> getAllGroupMembersByGroupId(UUID groupsId) {
        try {
            logger.info("Retrieving all group members by group ID: {}", groupsId);
            GroupResource groupResource = keycloak.realm("QssenceRealm").groups().group(groupsId.toString());
            return groupResource.members();
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all group members by group ID: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<RoleRepresentation> getAllRolesByGroupId(UUID groupsId) {
        try {
            logger.info("Retrieving all roles for group with ID {}", groupsId);
            GroupResource groupResource = keycloak.realm("QssenceRealm").groups().group(groupsId.toString());
            List<RoleRepresentation> groupRoles = groupResource.roles().realmLevel().listAll();
            return groupRoles;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all roles for group: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteUserFromGroupByUserId(String userId, String groupId) {
        try {
            UserResource userResource = keycloak.realm(realmName).users().get(userId);
            GroupResource groupResource = keycloak.realm(realmName).groups().group(groupId);
            userResource.leaveGroup(groupId);

            logger.info("User '{}' removed from Group '{}'", groupId, userId);
        } catch (Exception e) {
            logger.error("Failed to remove user '{}' from group '{}'", groupId, userId, e);
            throw new RuntimeException("Failed to remove user from group", e);
        }
    }

}
