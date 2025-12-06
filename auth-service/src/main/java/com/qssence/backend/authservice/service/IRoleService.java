package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dto.request.RoleRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface IRoleService {

    public Response createRole(RoleRequest roleRequest);

    List<RoleRepresentation> getAllRoles();

    RoleRepresentation getRoleByName(String name);

    public ResponseEntity<RoleRepresentation> updateRoleByName(String name, RoleRequest roleRequest);

    void deleteRoleByName(String name);

    RoleRepresentation getRoleById(UUID roleId);
    RoleRepresentation getRole(String roleId);
    boolean isRoleExists(String roleId);

    public ResponseEntity<RoleRepresentation> updateRoleById(UUID roleId, RoleRequest roleRequest);


    ResponseEntity<Object> deleteRoleById(UUID roleId);

    void cleanup();

    List<RoleRepresentation> getRolesByUserId(String userId);

    List<GroupRepresentation> getAllGroupsByRoleId(String roleId);

    void assignGroupToRole(String roleId, String groupId);

    void deleteRoleByRoleIdAndUserId(String roleId, String userId);

    void deleteRoleFromGroup(String roleId, String groupId);
    boolean removeRolesFromUserByUserId(String userId, List<String> roleIds);
}
