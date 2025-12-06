package com.qssence.backend.authservice.service.implementation;
import com.qssence.backend.authservice.dto.request.RoleRequest;
import com.qssence.backend.authservice.dto.responce.RoleResponse;
import com.qssence.backend.authservice.dto.responce.UserResponse;
import com.qssence.backend.authservice.service.IRoleService;
import jakarta.annotation.PreDestroy;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    @Value("${keycloak.realm}")
    private String realmName;

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RestTemplate restTemplate;
    private final Keycloak keycloak;

    @Autowired
    public RoleService(RestTemplate restTemplate, Keycloak keycloak) {
        this.restTemplate = restTemplate;
        this.keycloak = keycloak;
    }

    @Override
    public Response createRole(RoleRequest roleRequest) {
        try {
            logger.info("Creating role...");
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            RolesResource rolesResource = realmResource.roles();
            boolean roleExists = checkIfRoleExists(rolesResource, roleRequest.getName());
            if (roleExists) {
                logger.warn("Role already exists.");
                return Response.status(Response.Status.CONFLICT)
                        .entity("Role already exists.")
                        .build();
            }
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            String trimmedName = StringUtils.trimToEmpty(roleRequest.getName());
            String trimmedDescription = StringUtils.trimToEmpty(roleRequest.getDescription());
            roleRepresentation.setName(trimmedName);
            roleRepresentation.setDescription(trimmedDescription);
            rolesResource.create(roleRepresentation);
            logger.info("Role created successfully.");
            return Response.status(Response.Status.CREATED).build();
        }catch (Exception e) {
            logger.error("Error occurred while creating role: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create role: " + e.getMessage())
                    .build();
        }
    }
    private boolean checkIfRoleExists(RolesResource rolesResource, String roleName) {
        List<RoleRepresentation> existingRoles = rolesResource.list();
        return existingRoles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }


    @Override
    public List<RoleRepresentation> getAllRoles() {
        try {
            logger.info("Retrieving all roles...");
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            RolesResource rolesResource = realmResource.roles();
            List<RoleRepresentation> roles = rolesResource.list();
            Collections.sort(roles, new Comparator<RoleRepresentation>() {
                @Override
                public int compare(RoleRepresentation role1, RoleRepresentation role2) {
                    return role1.getName().compareToIgnoreCase(role2.getName());
                }
            });

            logger.info("All roles retrieved successfully.");
            return roles;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all roles: " + e.getMessage(), e);
            return null;
        }
    }

    public boolean isRoleExists(String roleId) {
        try {
            RoleRepresentation realmResource = keycloak.realm(realmName).rolesById().getRole(roleId);
            return realmResource != null;
        } catch (Exception e) {
            logger.error("Error show checking if role exists: " + e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RoleRepresentation getRoleByName(String name) {
        try {
            logger.info("Retrieving role by name...");
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            RolesResource rolesResource = realmResource.roles();

            RoleRepresentation role = rolesResource.get(name).toRepresentation();
            logger.info("Role retrieved successfully.");
            return role;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving role by name: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ResponseEntity<RoleRepresentation> updateRoleByName(String name, RoleRequest roleRequest) {
        try {
            logger.info("Updating role by name...");
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            RolesResource rolesResource = realmResource.roles();
            RoleRepresentation roleRepresentation = rolesResource.get(name).toRepresentation();
            String trimmedName = StringUtils.trimToEmpty(roleRequest.getName());
            String trimmedDescription = StringUtils.trimToEmpty(roleRequest.getDescription());
            roleRepresentation.setName(trimmedName);
            roleRepresentation.setDescription(trimmedDescription);
            rolesResource.get(name).update(roleRepresentation);
            logger.info("Role updated successfully.");
            return ResponseEntity.ok(roleRepresentation);
        } catch (NotFoundException e) {
            logger.error("Role not found: " + e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            logger.error("Validation error: " + e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
        catch (Exception e) {
            if (e.getMessage().contains("409")) {
                logger.error("Conflict occurred while updating role by name: " + e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {
                logger.error("Error occurred while updating role by name: " + e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }


    @Override
    public void deleteRoleByName(String name) {
        try {
            logger.info("Deleting role by name...");
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            RolesResource rolesResource = realmResource.roles();

            rolesResource.deleteRole(name);
            logger.info("Role deleted successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting role by name: " + e.getMessage(), e);
        }
    }

    @Override
    public RoleRepresentation getRoleById(UUID roleId) {
        try {
            logger.info("Retrieving role by ID...");
            RoleRepresentation realmResource = keycloak.realm(realmName).roles().get(roleId.toString()).toRepresentation();
            logger.info("Role retrieved successfully.");
            return realmResource;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving role by ID: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RoleRepresentation getRole(String roleId) {
        try {
            logger.info("Retrieving role by ID...");
            RoleRepresentation realmResource = keycloak.realm(realmName).rolesById().getRole(roleId);
            logger.info("Role retrieved successfully.");
            return realmResource;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving role by ID: " + e.getMessage(), e);
            return null;
        }
    }

    public boolean assignRole(String roleId, String userId){
        RoleRepresentation role = keycloak.realm(realmName).rolesById().getRole(roleId);
        UsersResource usersResource = keycloak.realm(realmName).users();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(role));
        return true;
    }

    public boolean removeRole(String roleId, String userId){
        RoleRepresentation role = keycloak.realm(realmName).rolesById().getRole(roleId);
        UsersResource usersResource = keycloak.realm(realmName).users();
        usersResource.get(userId).roles().realmLevel().remove(Collections.singletonList(role));
        return true;
    }

    public List<RoleResponse> getAssignedRoles(String userId) {
        UsersResource usersResource = keycloak.realm(realmName).users();
        RoleMappingResource roleMappingResource = usersResource.get(userId).roles();
        List<RoleRepresentation> assignedRoles = roleMappingResource.realmLevel().listAll();

        return assignedRoles.stream()
                .map(role -> new RoleResponse(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }
    public List<UserResponse> getAssignedUsers(String roleId) {
        RolesResource rolesResource = keycloak.realm(realmName).roles();
        List<UserRepresentation> assignedRoles = rolesResource.get(roleId).getUserMembers();
        return assignedRoles.stream()
                .map(user -> new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList());
    }


    @Override
        public ResponseEntity<RoleRepresentation> updateRoleById (UUID roleId, RoleRequest roleRequest){
            try {
                logger.info("Updating role by ID: " + roleId);
                RealmResource realmResource = keycloak.realm("QssenceRealm");
                RolesResource rolesResource = realmResource.roles();
                logger.info("Get role by ID: " + rolesResource);
                RoleRepresentation roleRepresentation = keycloak.realm("QssenceRealm").rolesById().getRole(roleId.toString());
                String trimmedName = StringUtils.trimToEmpty(roleRequest.getName());
                String trimmedDescription = StringUtils.trimToEmpty(roleRequest.getDescription());
                roleRepresentation.setName(trimmedName);
                roleRepresentation.setDescription(trimmedDescription);
                RoleByIdResource roleByIdResource = realmResource.rolesById();
                roleByIdResource.updateRole(roleId.toString(), roleRepresentation);

                return ResponseEntity.ok(roleRepresentation);
            } catch (NotFoundException e) {
                logger.error("Role not found: " + e.getMessage(), e);
                return ResponseEntity.notFound().build();
            } catch (ValidationException e) {
                logger.error("Validation error: " + e.getMessage(), e);
                return ResponseEntity.badRequest().build();
            } catch (Exception e) {
                if (e.getMessage().contains("409")) {
                    logger.error("Conflict occurred while updating role by name: " + e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                } else {
                    logger.error("Error occurred while updating role by name: " + e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
        }


    @Override
    public ResponseEntity<Object> deleteRoleById(UUID roleId) {
        try {
            logger.info("Deleting role by ID: " + roleId);
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            RoleByIdResource roleByIdResource = realmResource.rolesById();
            RoleRepresentation roleRepresentation = roleByIdResource.getRole(roleId.toString());
            if (roleRepresentation == null) {
                logger.error("Role not found with ID: " + roleId);
                return ResponseEntity.notFound().build();
            }
            roleByIdResource.deleteRole(roleId.toString());
            logger.info("Role deleted successfully.");
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            logger.error("Role not found: " + e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error occurred while deleting role by ID: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    public boolean roleExists(UUID roleId) {
        try {
            RealmResource realmResource = keycloak.realm("QssenceRealm");
            RoleRepresentation roleRepresentation = realmResource.rolesById().getRole(roleId.toString());
            return roleRepresentation != null;
        } catch (NotFoundException e) {
            return false;
        } catch (Exception e) {
            logger.error("Error occurred while checking role existence: " + e.getMessage(), e);
            return false;
        }
    }


    @PreDestroy
        public void cleanup () {
            restTemplate = null;
        }

        @Override
        public List<RoleRepresentation> getRolesByUserId (String userId){
            MappingsRepresentation mappingsRepresentation = keycloak.realm("QssenceRealm").users().get(userId).roles().getAll();
            List<RoleRepresentation> roleRepresentation = new ArrayList<>();

            Map<String, ClientMappingsRepresentation> clientRoles = mappingsRepresentation.getClientMappings();
            List<RoleRepresentation> realmRoles = mappingsRepresentation.getRealmMappings();
            if (realmRoles != null) {
                roleRepresentation.addAll(realmRoles);
            } else if (clientRoles != null || !clientRoles.isEmpty()) {
                roleRepresentation.addAll(clientRoles.get("auth-client").getMappings());
            }

            return roleRepresentation;
        }

        @Override
        public List<GroupRepresentation> getAllGroupsByRoleId (String roleId){
            try {
                logger.info("Retrieving all groups for role with ID {}", roleId);
                RealmResource realmResource = keycloak.realm("QssenceRealm");

                List<GroupRepresentation> allGroups = realmResource.groups().groups();
                List<GroupRepresentation> groupsWithRole = new ArrayList<>();
                for (GroupRepresentation group : allGroups) {
                    if (group.getRealmRoles().contains(roleId)) {
                        groupsWithRole.add(group);
                    }
                }

                logger.info("All groups for role retrieved successfully.");
                return groupsWithRole;
            } catch (Exception e) {
                logger.error("Error occurred while retrieving all groups for role: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        }


        @Override
        public void assignGroupToRole (String roleId, String groupId){
            try {
                logger.info("Assigning group with ID {} to role with ID {}", groupId, roleId);
                RealmResource realmResource = keycloak.realm("QssenceRealm");

                RoleResource roleResource = realmResource.roles().get(roleId);

                RoleRepresentation roleRepresentation = roleResource.toRepresentation();
                roleRepresentation.getComposites().getRealm().add(groupId); // Change here
                roleResource.update(roleRepresentation);

                logger.info("Group assigned to role successfully.");
            } catch (Exception e) {
                logger.error("Error occurred while assigning group to role: {}", e.getMessage(), e);
            }
        }

        @Override
        public void deleteRoleByRoleIdAndUserId (String roleId, String userId){
            try {
                List<RoleRepresentation> userRoles = keycloak.realm("QssenceRealm").users().get(userId)
                        .roles().realmLevel().listEffective();

                for (RoleRepresentation role : userRoles) {
                    if (role.getId().equals(roleId)) {
                        keycloak.realm("QssenceRealm").users().get(userId).roles().realmLevel().remove(Collections.singletonList(role));
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete role for user", e);
            }
        }
        @Override
        public void deleteRoleFromGroup (String roleId, String groupId){
            try {
                RealmResource realmResource = keycloak.realm("QssenceRealm");
                RoleMappingResource roleMappingResource = realmResource.groups().group(groupId).roles();

                List<RoleRepresentation> groupRoles = roleMappingResource.realmLevel().listEffective();

                for (RoleRepresentation role : groupRoles) {
                    if (role.getId().equals(roleId)) {
                        roleMappingResource.realmLevel().remove(Collections.singletonList(role));
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete role from group", e);
            }
        }

        public boolean removeUsersFromRoleByRoleId(String roleId, List<String> userIds) {
            try {
                RoleResource roleResource = keycloak.realm("QssenceRealm").roles().get(roleId);
                for (String userId : userIds) {
                    deleteRoleByRoleIdAndUserId(roleId,userId);
                }
                return true;
            } catch (NotFoundException e) {
                throw new RuntimeException("Failed to remove user from role", e);
            } catch (Exception e) {
                throw new RuntimeException("Failed to remove user from role", e);
            }
        }
    public boolean removeRolesFromUserByUserId(String userId, List<String> roleIds) {
        try {
            UserResource userResource = keycloak.realm("QssenceRealm").users().get(userId);
            for (String roleId : roleIds) {
                // Remove the role from the user
                deleteRoleByRoleIdAndUserId(roleId, userId);
            }
            return true;
        } catch (NotFoundException e) {
            // Handle not found exception
            throw e;
        } catch (Exception e) {
            // Handle other exceptions
            throw e;
        }
    }

}
