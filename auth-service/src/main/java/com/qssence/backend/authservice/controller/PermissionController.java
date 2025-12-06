package com.qssence.backend.authservice.controller;
import com.qssence.backend.authservice.dbo.*;
import com.qssence.backend.authservice.dbo.Permission.Permission;
import com.qssence.backend.authservice.dbo.Permission.PermissionLevelGroup;
import com.qssence.backend.authservice.dto.APIError;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.AddPermissionsForRoleRequest;
import com.qssence.backend.authservice.dto.request.AddPermissionsForUserRequest;
import com.qssence.backend.authservice.dto.request.PermissionIdRequest;
import com.qssence.backend.authservice.dto.request.PermissionRequest;
import com.qssence.backend.authservice.dto.responce.Permission.PermissionGroup;
import com.qssence.backend.authservice.dto.responce.Permission.PermissionLevelGroupResponse;
import com.qssence.backend.authservice.enums.PermissionAccessLevelType;
import com.qssence.backend.authservice.enums.PermissionType;
import com.qssence.backend.authservice.service.IPermissionService;
import com.qssence.backend.authservice.service.implementation.RoleService;
import com.qssence.backend.authservice.service.implementation.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
    @Autowired
    private IPermissionService IPermissionService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;


    public List<PermissionGroup> getPermissions(){
        List<Permission> permissions = IPermissionService.getAllPermissions();
        List<PermissionGroup> permissionGroups = permissions.stream()
                .map(permission -> {
                    List<PermissionLevelGroup> accessLevels = IPermissionService.getPermissionLevel(permission.getPermissionId());

                    List<PermissionLevelGroupResponse> levelGroups = accessLevels.stream()
                            .map(levelGroup -> PermissionLevelGroupResponse.builder()
                                    .accessLevel(levelGroup.getAccessLevel())
                                    .accessLevelValue(levelGroup.isAccessLevelValue())
                                    .build())
                            .collect(Collectors.toList());

                    return PermissionGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .permissionName(permission.getPermissionName())
                            .permissionType(permission.getPermissionType())
                            .levelGroups(levelGroups)
                            .build();
                })
                .collect(Collectors.toList());
        return permissionGroups;
    }


    @PostMapping("/createPermission")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> createPermission(@Valid @RequestBody PermissionRequest permissionRequest) {
        logger.info("Creating permission...");
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();

        try {
            if (permissionRequest.getPermissionName() == null || permissionRequest.getPermissionName().isEmpty()) {
                response.setMessage("Permission name is required");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            Permission existingPermission = IPermissionService.findPermissionByName(permissionRequest.getPermissionName().trim());
            if (existingPermission != null) {
                response.setMessage("Permission name already exist");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            if (permissionRequest.getPermissionType() == null || permissionRequest.getPermissionType().isEmpty()) {
                response.setMessage("Permission type is required");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            Permission permission = Permission.builder()
                    .permissionName(permissionRequest.getPermissionName().trim())
                    .permissionType(PermissionType.valueOf(permissionRequest.getPermissionType().trim()))
                    .build();

            permission = IPermissionService.createPermission(permission);

            if (permission != null) {
                PermissionLevelGroup permissionLevelGroup;
                if (permissionRequest.getLevels().isCanCreate()){
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_CREATE)
                            .accessLevelValue(true)
                            .build();
                }else{
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_CREATE)
                            .accessLevelValue(false)
                            .build();
                }
                IPermissionService.createPermissionLevelGroup(permissionLevelGroup);

                if (permissionRequest.getLevels().isCanRead()){
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_READ)
                            .accessLevelValue(true)
                            .build();
                }else{
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_READ)
                            .accessLevelValue(false)
                            .build();
                }
                IPermissionService.createPermissionLevelGroup(permissionLevelGroup);

                if (permissionRequest.getLevels().isCanUpdate()){
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_UPDATE)
                            .accessLevelValue(true)
                            .build();
                }else{
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_UPDATE)
                            .accessLevelValue(false)
                            .build();
                }
                IPermissionService.createPermissionLevelGroup(permissionLevelGroup);

                if (permissionRequest.getLevels().isCanDelete()){
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_DELETE)
                            .accessLevelValue(true)
                            .build();
                }else{
                    permissionLevelGroup = PermissionLevelGroup.builder()
                            .permissionId(permission.getPermissionId())
                            .accessLevel(PermissionAccessLevelType.CAN_DELETE)
                            .accessLevelValue(false)
                            .build();
                }
                IPermissionService.createPermissionLevelGroup(permissionLevelGroup);

                response.setSuccess(true);
                response.setData(getPermissions());
                response.setMessage("Permission created successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }else{
                response.setSuccess(false);
                response.setMessage("Permission creation failed");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to create permission");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("getPermissionById/{permissionId}")
    public ResponseEntity<ApiResponse<PermissionGroup>> getPermissionById(@PathVariable UUID permissionId) {
        ApiResponse<PermissionGroup> response = new ApiResponse<>();
            try {
                if (permissionId == null || permissionId.toString().isEmpty()) {
                    response.setMessage("Permission id is required");
                    response.setSuccess(false);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }

                if (!IPermissionService.isPermissionExist(permissionId)) {
                    response.setMessage("Permission not exist in this id");
                    response.setSuccess(false);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }

                Permission permission = IPermissionService.getPermissionById(permissionId);
                response.setSuccess(true);
                response.setData(getPermissionGroup(permission.getPermissionId()));
                response.setMessage("Permission get successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);

            } catch (Exception e) {
                logger.error("Error occurred while retrieving permission by ID: {}", e.getMessage(), e);
                APIError error = new APIError();
                error.setError_code(5000);
                error.setError_name("INTERNAL_SERVER_ERROR");
                error.setError_description("Failed to retrieve permission: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
    }

    @GetMapping("/getAllPermissions")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> getAllPermissions() {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            logger.info("Retrieving all permissions...");
            response.setSuccess(true);
            response.setMessage("All permissions retrieved successfully.");
            response.setData(getPermissions());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all permissions: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve all permissions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/updatePermissionById/{permissionId}")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> updatePermission(@PathVariable UUID permissionId, @RequestBody PermissionRequest permissionRequest) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            logger.info("Updating permission by ID...");
            if (permissionId == null || permissionId.toString().isEmpty()) {
                response.setMessage("Permission id is required");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            if (!IPermissionService.isPermissionExist(permissionId)) {
                response.setMessage("Permission not exist in this id");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            Permission existingPermissionByName = IPermissionService.findPermissionByName(permissionRequest.getPermissionName().trim());
            if (existingPermissionByName != null && existingPermissionByName.getPermissionId() == permissionId) {
                response.setMessage("Permission name already exist");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }


            Permission existingPermission = IPermissionService.getPermissionById(permissionId);

            existingPermission.setPermissionName(permissionRequest.getPermissionName().trim());
            existingPermission.setPermissionType(PermissionType.valueOf(permissionRequest.getPermissionType()));

            IPermissionService.updatePermission(permissionId, existingPermission);
            IPermissionService.updatePermissionLevels(permissionId, permissionRequest.getLevels());

            response.setSuccess(true);
            response.setData(getPermissions());
            response.setMessage("Permission updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while updating permission by ID: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to update permission: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deletePermissionById/{permissionId}")
    public ResponseEntity<ApiResponse<List<Permission>>> deletePermissionById(@PathVariable("permissionId") UUID permissionId) {
        ApiResponse<List<Permission>> response = new ApiResponse<>();
        try {
            logger.info("Deleting permission by ID: {}", permissionId);
            Permission existingPermission = IPermissionService.getPermissionById(permissionId);
            if (existingPermission == null) {
                response.setSuccess(false);
                response.setMessage("Permission not found");
                response.setData(new ArrayList<>());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            IPermissionService.deletePermission(permissionId);
            logger.info("Permission deleted successfully.");
            response.setSuccess(true);
            response.setMessage("Permission deleted successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error occurred while deleting permission by ID: {}", e.getMessage(), e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Failed to delete permission by ID: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("Failed to delete permission by ID: " + e.getMessage());
            response.setData(new ArrayList<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    ///////////////////////////////////////
    @PostMapping("/setPermissionsForRole")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> setPermissionsForRole(@RequestBody AddPermissionsForRoleRequest permissionRoleMapping) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            boolean existingRoleRepresentation = roleService.isRoleExists(permissionRoleMapping.getRoleId().toString());
            if (!existingRoleRepresentation) {
                response.setMessage("Role not exist in this id.");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            roleService.getRole(permissionRoleMapping.getRoleId().toString());
            logger.info("Setting permission for role...");
            List<PermissionRoleMapping> updatedRoles = IPermissionService.setPermissionsForRole(permissionRoleMapping);
            response.setSuccess(true);
            response.setMessage("Permission assigned to role successfully");
            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByRoleId(permissionRoleMapping.getRoleId());
            response.setData(permissions);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            logger.error("IllegalArgumentException occurred while setting permission for role: {}", ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("Failed to assign permission to role: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            logger.error("Error occurred while setting permission for role: {}", ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("An error occurred while assigning permission to role: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/removePermissionsFromRole")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> removePermissionsFromRole(@RequestBody AddPermissionsForRoleRequest permissionRoleMapping) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            if (permissionRoleMapping.getRoleId() == null || permissionRoleMapping.getPermissionIds().isEmpty()) {
                throw new IllegalArgumentException("Role ID and Permission IDs are required");
            }
            boolean existingRoleRepresentation = roleService.isRoleExists(permissionRoleMapping.getRoleId().toString());
            if (!existingRoleRepresentation) {
                response.setMessage("Role not exist in this id.");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            IPermissionService.removePermissionsFromRole(permissionRoleMapping);
            response.setSuccess(true);
            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByRoleId(permissionRoleMapping.getRoleId());
            response.setData(permissions);
            response.setMessage("Permissions removed for role successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage("Failed to remove permissions for role");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getAllPermissionsByRoleId/{roleId}")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> getAllPermissionsByRoleId(@PathVariable UUID roleId) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            if (roleId == null || roleId.toString().isEmpty()) {
                response.setMessage("Role ID is required");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }


            if (!roleService.isRoleExists(roleId.toString())) {
                response.setMessage("Role not found with ID: " + roleId);
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            logger.info("Retrieving all permissions by role ID: {}", roleId);
            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByRoleId(roleId);
            response.setSuccess(true);
            response.setMessage("Permissions retrieved successfully");
            response.setData(permissions);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("Error occurred while retrieving permissions by role ID {}: {}", roleId, ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve permissions by role ID: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deletePermissionByRoleIdAndPermissionId/{roleId}")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> deletePermissionByRoleIdAndPermissionId(@PathVariable UUID roleId,@RequestBody PermissionIdRequest permissionId) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            boolean existingRoleRepresentation = roleService.isRoleExists(roleId.toString());
            if (!existingRoleRepresentation) {
                response.setMessage("Role not exist in this id.");
                response.setSuccess(false);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            logger.info("Deleting permissions from role with ID: {}", roleId);
            IPermissionService.deletePermissionByRoleIdAndPermissionId(roleId, permissionId.getPermissionId());
            List<PermissionGroup> remainingPermissions = IPermissionService.getAllPermissionsByRoleId(roleId);
            response.setSuccess(true);
            response.setMessage("Permissions deleted from role successfully");
            response.setData(remainingPermissions);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error occurred while deleting permissions from role with ID {}: {}", roleId, ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("An error occurred while deleting permissions from role: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //////////////////////////////////////


    //////////////////////////////////////
    @PostMapping("/setPermissionsForUser")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> setPermissionsForUser(@RequestBody AddPermissionsForUserRequest addPermissionsForUserRequest) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            if (addPermissionsForUserRequest.getUserId() == null || addPermissionsForUserRequest.getPermissionIds().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("User ID and Permission IDs are required");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (!userService.isUserExist(addPermissionsForUserRequest.getUserId().toString())){
                response.setSuccess(false);
                response.setMessage("User not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            logger.info("Setting permission for user...");
            IPermissionService.setPermissionsForUser(addPermissionsForUserRequest);
            response.setSuccess(true);
            response.setMessage("Permission assigned to user successfully");
            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByUserId(addPermissionsForUserRequest.getUserId());
            response.setData(permissions);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            logger.error("IllegalArgumentException occurred while setting permission for user: {}", ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("Failed to assign permission to user: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            logger.error("Error occurred while setting permission for user: {}", ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("An error occurred while assigning permission to user: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getAllPermissionsByUserId/{userId}")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> getAllPermissionsByUserId(@PathVariable UUID userId) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            if (!userService.isUserExist(userId.toString())){
                response.setSuccess(false);
                response.setMessage("User not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            logger.info("Retrieving all permissions by user ID: {}", userId);
            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByUserId(userId);
            response.setSuccess(true);
            response.setMessage("Permissions retrieved successfully");
            response.setData(permissions);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error occurred while retrieving permissions by user ID {}: {}", userId, ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve permissions by user ID: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/removePermissionsFromUser")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> removePermissionsFromUser(@RequestBody AddPermissionsForUserRequest addPermissionsForUserRequest) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            if (addPermissionsForUserRequest.getUserId() == null || addPermissionsForUserRequest.getPermissionIds().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("User ID and Permission IDs are required");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (!userService.isUserExist(addPermissionsForUserRequest.getUserId().toString())){
                response.setSuccess(false);
                response.setMessage("User not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            IPermissionService.removePermissionsFromUser(addPermissionsForUserRequest);
            response.setSuccess(true);
            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByUserId(addPermissionsForUserRequest.getUserId());
            response.setData(permissions);
            response.setMessage("Permissions removed for user successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            response.setSuccess(false);
            response.setMessage("Failed to remove permissions for role");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deletePermissionByUserIdAndPermissionId/{userId}")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> deletePermissionByUserIdAndPermissionId(@PathVariable UUID userId,@RequestBody PermissionIdRequest permissionId) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            if (!userService.isUserExist(userId.toString())){
                response.setSuccess(false);
                response.setMessage("User not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            logger.info("Deleting permission from user with ID: {}", userId);
            IPermissionService.deletePermissionByUserIdAndPermissionId(userId, permissionId.getPermissionId());
            List<PermissionGroup> remainingPermissions = IPermissionService.getAllPermissionsByUserId(userId);
            response.setSuccess(true);
            response.setMessage("Permission deleted from user successfully");
            response.setData(remainingPermissions);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error occurred while deleting permission from user with ID {}: {}", userId, ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("An error occurred while deleting permission from user: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    ////////////////////////////////////////


    @PostMapping("/setPermissionForGroup")
    public ResponseEntity<ApiResponse<PermissionGroupMapping>> setPermissionForGroup(@RequestBody PermissionGroupMapping permissionGroupMapping) {
        ApiResponse<PermissionGroupMapping> response = new ApiResponse<>();
        try {
            logger.info("Setting permission for group...");
            PermissionGroupMapping updatedGroup = IPermissionService.setPermissionForGroup(permissionGroupMapping);
            response.setSuccess(true);
            response.setMessage("Permission assigned to group successfully");
            response.setData(updatedGroup);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            logger.error("IllegalArgumentException occurred while setting permission for group: {}", ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("Failed to assign permission to group: " + ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            logger.error("Error occurred while setting permission for group: {}", ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("An error occurred while assigning permission to group: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deletePermissionByGroupIdAndPermissionId/{groupId}")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> deletePermissionsByGroupId(@PathVariable UUID groupId, @RequestBody PermissionIdRequest permissionId) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            logger.info("Deleting permissions from group with ID: {}", groupId);
            IPermissionService.deletePermissionByGroupIdAndPermissionId(groupId, permissionId.getPermissionId());
            List<PermissionGroup> remainingPermissions = IPermissionService.getAllPermissionsByGroupId(groupId);
            response.setSuccess(true);
            response.setMessage("Permissions deleted from group successfully");
            response.setData(remainingPermissions);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error occurred while deleting permissions from group with ID {}: {}", groupId, ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("An error occurred while deleting permissions from group: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getAllPermissionByGroupId/{groupId}")
    public ResponseEntity<ApiResponse<List<PermissionGroup>>> getAllPermissionsByGroupId(@PathVariable UUID groupId) {
        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
        try {
            logger.info("Retrieving all permissions by group ID: {}", groupId);
            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByGroupId(groupId);
            response.setSuccess(true);
            response.setMessage("Permissions retrieved successfully");
            response.setData(permissions);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error occurred while retrieving permissions by group ID {}: {}", groupId, ex.getMessage(), ex);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve permissions by group ID: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    public PermissionGroup getPermissionGroup(UUID permissionId) {
        Permission permission = IPermissionService.getPermissionById(permissionId);
        List<PermissionLevelGroup> accessLevels = IPermissionService.getPermissionLevel(permission.getPermissionId());

        List<PermissionLevelGroupResponse> levelGroupResponses = accessLevels.stream()
                .map(levelGroup -> PermissionLevelGroupResponse.builder()
                        .accessLevel(levelGroup.getAccessLevel())
                        .accessLevelValue(levelGroup.isAccessLevelValue())
                        .build())
                .collect(Collectors.toList());

        return PermissionGroup.builder()
                .permissionId(permission.getPermissionId())
                .permissionName(permission.getPermissionName())
                .permissionType(permission.getPermissionType())
                .levelGroups(levelGroupResponses)
                .build();
    }



//    @DeleteMapping("/deletePermissionByGroupIdAndPermissionId/{groupId}")
//    public ResponseEntity<ApiResponse<List<PermissionGroup>>> deletePermissionsByGroupId(@PathVariable UUID groupId, @RequestBody PermissionIdRequest permissionId) {
//        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
//        try {
//            logger.info("Deleting permissions from group with ID: {}", groupId);
//            IPermissionService.deletePermissionByGroupIdAndPermissionId(groupId, permissionId.getPermissionId());
//            List<PermissionGroup> remainingPermissions = IPermissionService.getAllPermissionsByGroupId(groupId);
//            response.setSuccess(true);
//            response.setMessage("Permissions deleted from group successfully");
//            response.setData(remainingPermissions);
//            return ResponseEntity.ok(response);
//        } catch (Exception ex) {
//            logger.error("Error occurred while deleting permissions from group with ID {}: {}", groupId, ex.getMessage(), ex);
//            response.setSuccess(false);
//            response.setMessage("An error occurred while deleting permissions from group: " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @DeleteMapping("/deletePermissionByRoleIdAndPermissionId/{roleId}")
//    public ResponseEntity<ApiResponse<List<PermissionGroup>>> deletePermissionByRoleIdAndPermissionId(@PathVariable UUID roleId,@RequestBody PermissionIdRequest permissionId) {
//        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
//        try {
//            logger.info("Deleting permissions from role with ID: {}", roleId);
//            IPermissionService.deletePermissionByRoleIdAndPermissionId(roleId, permissionId.getPermissionId());
//            List<PermissionGroup> remainingPermissions = IPermissionService.getAllPermissionsByRoleId(roleId);
//            response.setSuccess(true);
//            response.setMessage("Permissions deleted from role successfully");
//            response.setData(remainingPermissions);
//            return ResponseEntity.ok(response);
//        } catch (Exception ex) {
//            logger.error("Error occurred while deleting permissions from role with ID {}: {}", roleId, ex.getMessage(), ex);
//            response.setSuccess(false);
//            response.setMessage("An error occurred while deleting permissions from role: " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @DeleteMapping("/deletePermissionByUserIdAndPermissionId/{userId}")
//    public ResponseEntity<ApiResponse<List<PermissionGroup>>> deletePermissionByUserIdAndPermissionId(@PathVariable UUID userId,@RequestBody PermissionIdRequest permissionId) {
//        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
//        try {
//            logger.info("Deleting permission from user with ID: {}", userId);
//            IPermissionService.deletePermissionByUserIdAndPermissionId(userId, permissionId.getPermissionId());
//            List<PermissionGroup> remainingPermissions = IPermissionService.getAllPermissionsByUserId(userId);
//            response.setSuccess(true);
//            response.setMessage("Permission deleted from user successfully");
//            response.setData(remainingPermissions);
//            return ResponseEntity.ok(response);
//        } catch (Exception ex) {
//            logger.error("Error occurred while deleting permission from user with ID {}: {}", userId, ex.getMessage(), ex);
//            response.setSuccess(false);
//            response.setMessage("An error occurred while deleting permission from user: " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//
//    @GetMapping("/getAllPermissionByGroupId/{groupId}")
//    public ResponseEntity<ApiResponse<List<PermissionGroup>>> getAllPermissionsByGroupId(@PathVariable UUID groupId) {
//        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
//        try {
//            logger.info("Retrieving all permissions by group ID: {}", groupId);
//            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByGroupId(groupId);
//            response.setSuccess(true);
//            response.setMessage("Permissions retrieved successfully");
//            response.setData(permissions);
//            return ResponseEntity.ok(response);
//        } catch (Exception ex) {
//            logger.error("Error occurred while retrieving permissions by group ID {}: {}", groupId, ex.getMessage(), ex);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve permissions by group ID: " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @GetMapping("/getAllPermissionByRoleId/{roleId}")
//    public ResponseEntity<ApiResponse<List<PermissionGroup>>> getAllPermissionsByRoleId(@PathVariable UUID roleId) {
//        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
//        try {
//            if (roleId == null || roleId.toString().isEmpty()) {
//                response.setMessage("Role ID is required");
//                response.setSuccess(false);
//                return ResponseEntity.status(HttpStatus.CREATED).body(response);
//            }
//
//
//            if (!roleService.isRoleExists(roleId.toString())) {
//                response.setMessage("Role not found with ID: " + roleId);
//                response.setSuccess(false);
//                return ResponseEntity.status(HttpStatus.CREATED).body(response);
//            }
//
//            logger.info("Retrieving all permissions by role ID: {}", roleId);
//            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByRoleId(roleId);
//            response.setSuccess(true);
//            response.setMessage("Permissions retrieved successfully");
//            response.setData(permissions);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception ex) {
//            logger.error("Error occurred while retrieving permissions by role ID {}: {}", roleId, ex.getMessage(), ex);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve permissions by role ID: " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @GetMapping("/getAllPermissionByUserId/{userId}")
//    public ResponseEntity<ApiResponse<List<PermissionGroup>>> getAllPermissionsByUserId(@PathVariable UUID userId) {
//        ApiResponse<List<PermissionGroup>> response = new ApiResponse<>();
//        try {
//            logger.info("Retrieving all permissions by user ID: {}", userId);
//            List<PermissionGroup> permissions = IPermissionService.getAllPermissionsByUserId(userId);
//            response.setSuccess(true);
//            response.setMessage("Permissions retrieved successfully");
//            response.setData(permissions);
//            return ResponseEntity.ok(response);
//        } catch (Exception ex) {
//            logger.error("Error occurred while retrieving permissions by user ID {}: {}", userId, ex.getMessage(), ex);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve permissions by user ID: " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//
//    public PermissionGroup getPermissionGroup(UUID permissionId) {
//        Permission permission = IPermissionService.getPermissionById(permissionId);
//        List<PermissionLevelGroup> accessLevels = IPermissionService.getPermissionLevel(permission.getPermissionId());
//
//        List<PermissionLevelGroupResponse> levelGroupResponses = accessLevels.stream()
//                .map(levelGroup -> PermissionLevelGroupResponse.builder()
//                        .accessLevel(levelGroup.getAccessLevel())
//                        .accessLevelValue(levelGroup.isAccessLevelValue())
//                        .build())
//                .collect(Collectors.toList());
//
//        return PermissionGroup.builder()
//                .permissionId(permission.getPermissionId())
//                .permissionName(permission.getPermissionName())
//                .permissionType(permission.getPermissionType())
//                .levelGroups(levelGroupResponses)
//                .build();
//    }
}
