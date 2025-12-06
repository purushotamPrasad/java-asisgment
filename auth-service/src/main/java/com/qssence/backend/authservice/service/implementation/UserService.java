package com.qssence.backend.authservice.service.implementation;


import com.qssence.backend.authservice.dto.request.UserRequest;
import com.qssence.backend.authservice.dto.responce.Permission.PermissionGroup;
import com.qssence.backend.authservice.dto.responce.RoleResponse;
import com.qssence.backend.authservice.dto.responce.User.UserDetailsResponse;
import com.qssence.backend.authservice.dto.responce.UserResponse;
import com.qssence.backend.authservice.dbo.KeyCloakUser;
import com.qssence.backend.authservice.exception.UserNotFoundException;
import com.qssence.backend.authservice.repository.KeyCloakUserRepository;
import com.qssence.backend.authservice.service.IUserService;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * A service for handling user.
 * Implements the {@link IUserService} interface.
 * @author ayushtamrakar
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    
    
    private final KeyCloakUserRepository userRepository;
    
    private final Keycloak keycloak;
    
    private final RoleService roleService;
    private final PermissionService permissionService;

    @Value("${keycloak.realm}")
    private String realmName;

    /**
     * @param userRequest
     * @return something
     */
    @Override
    public Response createUser(UserRequest userRequest) {
        String trimmedFirstName = StringUtils.trimToEmpty(userRequest.getFirstName());
        String trimmedLastName = StringUtils.trimToEmpty(userRequest.getLastName());
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(trimmedFirstName);
        userRepresentation.setLastName(trimmedLastName);
        userRepresentation.setEmail(userRequest.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(userRequest.getUsername());
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userRequest.getPassword());
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        Response response = keycloak.realm(realmName).users().create(userRepresentation);

        return response;
    }

    /**
     * doc
     *
     */
    @Override
    public List<UserResponse> getAllUsers() {
        List<UserRepresentation> userRepresentations = keycloak.realm(realmName).users().list();

        List<UserResponse> userResponses = userRepresentations.stream()
                .map(this::mapToUserResponseFromRepresentation)
                .sorted(Comparator.comparing(UserResponse::getUsername))
                .collect(Collectors.toList());

        return userResponses;
    }

    private UserResponse mapToUserResponseFromRepresentation(UserRepresentation userRepresentation) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(userRepresentation.getUsername());
        userResponse.setId(userRepresentation.getId());
        userResponse.setEmail(userRepresentation.getEmail());
        userResponse.setFullName(userRepresentation.getFirstName()+ " " + userRepresentation.getLastName());
        userResponse.setStatus(userRepresentation.isEnabled());
        return userResponse;
    }

    /**
     * @return
     */
    @Override
    public ResponseEntity<UserResponse> getUserById(String userId) {
        try {
            UserRepresentation userRepresentation = keycloak.realm(realmName).users().get(userId).toRepresentation();
            UserResponse userResponse = new UserResponse();
            userResponse.setId(userRepresentation.getId());
            userResponse.setEmail(userRepresentation.getEmail());
            userResponse.setUsername(userRepresentation.getUsername());
            userResponse.setFullName(userRepresentation.getFirstName()+ " " + userRepresentation.getLastName());
            userResponse.setStatus(userRepresentation.isEnabled());
            userResponse.setRole("ADMIN");
            return ResponseEntity.ok(userResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public boolean isUserExist(String userId) {
        try {
            UserResource userResource = keycloak.realm(realmName).users().get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            return userRepresentation != null;
        } catch (NotFoundException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserDetailsResponse getUserDetailsById(UUID userId) {
        UserRepresentation userRepresentation = keycloak.realm(realmName).users().get(userId.toString()).toRepresentation();
        UserDetailsResponse userResponse = new UserDetailsResponse();
        userResponse.setId(userRepresentation.getId());
        userResponse.setEmail(userRepresentation.getEmail());
        userResponse.setUsername(userRepresentation.getUsername());
        userResponse.setFullName(userRepresentation.getFirstName()+ " " + userRepresentation.getLastName());
        userResponse.setStatus(userRepresentation.isEnabled());
        userResponse.setRole("ADMIN");

        List<RoleRepresentation> rolesRepresentation = roleService.getRolesByUserId(userId.toString());
        userResponse.setRoles(mapToRoleResponses(rolesRepresentation));


        List<PermissionGroup> permissions = permissionService.getAllPermissionsByUserId(userId);
        userResponse.setPermissions(permissions);
        return userResponse;
    }

    private List<RoleResponse> mapToRoleResponses(List<RoleRepresentation> roleRepresentations) {
        List<RoleResponse> roleResponses = new ArrayList<>();
        for (RoleRepresentation roleRepresentation : roleRepresentations) {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(roleRepresentation.getId());
            roleResponse.setName(roleRepresentation.getName());
            roleResponses.add(roleResponse);
        }
        return roleResponses;
    }



    public boolean isUserExists(String userId) {
        try {
            UserRepresentation userRepresentation = keycloak.realm(realmName).users().get(userId.toString()).toRepresentation();
            return userRepresentation != null;
        } catch (NotFoundException ex) {
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public ResponseEntity<UserResponse> getUserByUsername(String username) {
        try {
            List<UserRepresentation> users = keycloak.realm(realmName).users().search(username);
            if (users != null && !users.isEmpty()) {
                UserRepresentation userRepresentation = users.get(0);

                UserResponse userResponse = new UserResponse();
                userResponse.setId(userRepresentation.getId());
                userResponse.setEmail(userRepresentation.getEmail());
                userResponse.setUsername(userRepresentation.getUsername());
                userResponse.setFullName(userRepresentation.getFirstName() + " " + userRepresentation.getLastName());
                userResponse.setStatus(userRepresentation.isEnabled());

                userResponse.setRole("ADMIN");

                return ResponseEntity.ok(userResponse);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *doc
     */
    public ResponseEntity<UserRepresentation> updateUserById(String userId, UserRequest userRequest) {
        try {
            UserRepresentation userRepresentation = keycloak.realm(realmName).users().get(userId).toRepresentation();
            String trimmedFirstName = StringUtils.trimToEmpty(userRequest.getFirstName());
            String trimmedLastName = StringUtils.trimToEmpty(userRequest.getLastName());
            if(userRepresentation != null){
                userRepresentation.setFirstName(trimmedFirstName);
                userRepresentation.setLastName(trimmedLastName);
                userRepresentation.setEmail(userRequest.getEmail());
                userRepresentation.setEmailVerified(true);
                userRepresentation.setEnabled(true);
                userRepresentation.setUsername(userRequest.getUsername());

                CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                credentialRepresentation.setValue(userRequest.getPassword());
                credentialRepresentation.setTemporary(false);
                userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

                keycloak.realm(realmName).users().get(userId).update(userRepresentation);
                return ResponseEntity.ok(userRepresentation);
            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * @param userRequest
     * @return
     */
    @Override
    public ResponseEntity<UserRepresentation> updateUserByUsername(String username, UserRequest userRequest) {
        try {
            UserRepresentation userRepresentation = keycloak.realm(realmName).users().search(username).stream()
                    .filter(user -> username.equals(user.getUsername()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User with username " + username + " not found"));
            if(userRepresentation != null){
                String trimmedFirstName = StringUtils.trimToEmpty(userRequest.getFirstName());
                String trimmedLastName = StringUtils.trimToEmpty(userRequest.getLastName());
                userRepresentation.setFirstName(trimmedFirstName);
                userRepresentation.setLastName(trimmedLastName);
                userRepresentation.setEmail(userRequest.getEmail());
                userRepresentation.setEmailVerified(true);
                userRepresentation.setEnabled(true);
                userRepresentation.setUsername(userRequest.getUsername());

                CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                credentialRepresentation.setValue(userRequest.getPassword());
                credentialRepresentation.setTemporary(false);
                userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

                keycloak.realm(realmName).users().get(userRepresentation.getId()).update(userRepresentation);

                return ResponseEntity.ok(userRepresentation);
            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (UserNotFoundException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception exception){
            log.error(exception.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @return
     */
    public ResponseEntity<Void> deleteUserById(String userId) {
        try {
            UserRepresentation user = keycloak.realm(realmName).users().get(userId).toRepresentation();
            keycloak.realm(realmName).users().get(userId).remove();
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * @return
     */
    @Override
    public ResponseEntity<Void> deleteUserByUsername(String username) {
        try {
            List<UserRepresentation> users = keycloak.realm(realmName).users().search(username);
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String userId = users.get(0).getId();
            return deleteUserById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public List<UserResponse> getUsersByRoleId(String roleId) {
        UsersResource usersResource = keycloak.realm(realmName).users();
        List<UserRepresentation> allUsers = usersResource.list();

        List<UserRepresentation> filteredUsers = new ArrayList<>();

        for (UserRepresentation user : allUsers) {
            List<RoleRepresentation> userRoles = usersResource.get(user.getId()).roles().realmLevel().listEffective();

            for (RoleRepresentation role : userRoles) {
                if (role.getId().equals(roleId)) {
                    filteredUsers.add(user);
                    break;
                }
            }
        }

        // Convert UserRepresentation to KeyCloakUser
        List<KeyCloakUser> keyCloakUsers = filteredUsers.stream()
                .map(userRepresentation -> {
                    KeyCloakUser keyCloakUser = new KeyCloakUser();
                    keyCloakUser.setId(userRepresentation.getId());
                    // Set other properties as needed
                    return keyCloakUser;
                })
                .collect(Collectors.toList());
        return keyCloakUsers.stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteUserFromRole(String roleId, String userId) {
        try {
            UsersResource usersResource = keycloak.realm(realmName).users();

            UserResource userResource = usersResource.get(userId);

            List<RoleRepresentation> userRoles = userResource.roles().realmLevel().listEffective();

            for (RoleRepresentation role : userRoles) {
                if (role.getId().equals(roleId)) {
                    userResource.roles().realmLevel().remove(Collections.singletonList(role));
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user from role", e);
        }
    }

    @Override
    public void deleteUserByGroupAndUserId(String userId, String groupId) {
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

    private UserResponse mapToUserResponse(KeyCloakUser user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFirst_name()+" "+user.getLast_name())
                .status(user.isEnabled())
                .role("ADMIN")
                .build();
    }

}
