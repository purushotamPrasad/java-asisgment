package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dbo.*;
import com.qssence.backend.authservice.dbo.Permission.Permission;
import com.qssence.backend.authservice.dbo.Permission.PermissionLevelGroup;
import com.qssence.backend.authservice.dto.request.AddPermissionsForRoleRequest;
import com.qssence.backend.authservice.dto.request.AddPermissionsForUserRequest;
import com.qssence.backend.authservice.dto.responce.Permission.PermissionGroup;
import com.qssence.backend.authservice.dto.responce.Permission.PermissionLevelGroupResponse;
import com.qssence.backend.authservice.dto.responce.Permission.PermissionRoleMappingResponse;
import com.qssence.backend.authservice.enums.PermissionAccessLevel;
import com.qssence.backend.authservice.enums.PermissionAccessLevelType;
import com.qssence.backend.authservice.repository.*;

import com.qssence.backend.authservice.service.IPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class PermissionService implements IPermissionService {
    @Override
    public Permission createPermission(Permission permission) {
        try {
            logger.info("Creating permission...");
            return permissionRepository.save(permission);
        } catch (Exception e) {
            logger.error("Error occurred while creating permission: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void createPermissionLevelGroup(PermissionLevelGroup permissionLevelGroup) {
        try {
            logger.info("Creating permission...");
            permissionLevelGroupRepository.save(permissionLevelGroup);
        } catch (Exception e) {
            logger.error("Error occurred while creating permission: {}", e.getMessage(), e);
        }
    }

    @Override
    public void updatePermissionLevels(UUID permissionId, PermissionAccessLevel levels) {
        try {
            logger.info("Updating permission levels...");
            List<PermissionLevelGroup> existingLevels = permissionLevelGroupRepository.findByPermissionId(permissionId);

            updatePermissionLevel(existingLevels, permissionId, PermissionAccessLevelType.CAN_CREATE, levels.isCanCreate());
            updatePermissionLevel(existingLevels, permissionId, PermissionAccessLevelType.CAN_READ, levels.isCanRead());
            updatePermissionLevel(existingLevels, permissionId, PermissionAccessLevelType.CAN_UPDATE, levels.isCanUpdate());
            updatePermissionLevel(existingLevels, permissionId, PermissionAccessLevelType.CAN_DELETE, levels.isCanDelete());

            logger.info("Permission levels updated successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while updating permission levels: {}", e.getMessage(), e);
        }
    }

    private void updatePermissionLevel(List<PermissionLevelGroup> levels, UUID permissionId, PermissionAccessLevelType levelType, boolean levelValue) {
        Optional<PermissionLevelGroup> existingLevel = levels.stream()
                .filter(level -> level.getAccessLevel() == levelType)
                .findFirst();

        if (existingLevel.isPresent()) {
            PermissionLevelGroup permissionLevelGroup = existingLevel.get();
            permissionLevelGroup.setAccessLevelValue(levelValue);
            permissionLevelGroupRepository.save(permissionLevelGroup);
        } else {
            PermissionLevelGroup newPermissionLevelGroup = PermissionLevelGroup.builder()
                    .permissionId(permissionId)
                    .accessLevel(levelType)
                    .accessLevelValue(levelValue)
                    .build();
            permissionLevelGroupRepository.save(newPermissionLevelGroup);
        }
    }


    @Override
    public boolean isPermissionExist(UUID permissionId) {
        try {
            logger.info("Checking if permission exists...");
            return permissionRepository.existsById(permissionId);
        } catch (Exception e) {
            logger.error("Error occurred while checking permission existence: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<PermissionLevelGroup> getPermissionLevel(UUID permissionId) {
        return permissionLevelGroupRepository.findByPermissionId(permissionId);
    }


    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);


    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PermissionLevelGroupRepository permissionLevelGroupRepository;


    @Autowired
    private PermissionUserMappingRepository permissionUserMappingRepository;
    @Autowired
    private PermissionGroupMappingRepository permissionGroupMappingRepository;
    @Autowired
    private PermissionRoleMappingRepository permissionRoleMappingRepository;


    @Override
    public Permission findPermissionByName(String name) {
        return permissionRepository.findByPermissionName(name);
    }
    @Override
    public Permission getPermissionById(UUID permissionId) {
        try {
            logger.info("Retrieving permission by ID...");
            return permissionRepository.findById(permissionId).orElse(null);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving permission by ID: {}", e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public List<Permission> getAllPermissions() {
        try {
            logger.info("Retrieving all permissions sorted by name...");
            List<Permission> allPermissions = permissionRepository.findAll();
            return allPermissions.stream()
                    .sorted(Comparator.comparing(Permission::getPermissionName))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all permissions sorted by name: {}", e.getMessage(), e);
        }
        return null;
    }


    @Override
    public Permission updatePermission(UUID permissionId, Permission updatedPermission) {
        try {
            logger.info("Updating permission...");
            Permission existingPermission = permissionRepository.findById(permissionId).orElse(null);
            if (existingPermission != null) {
                updatedPermission.setPermissionId(permissionId);
                return permissionRepository.save(updatedPermission);
            }
            return null;
        } catch (Exception e) {
            logger.error("Error occurred while updating permission: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean deletePermission(UUID permissionId) {
        try {
            logger.info("Deleting permission...");
            if (permissionRepository.existsById(permissionId)) {
                permissionRepository.deleteById(permissionId);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error occurred while deleting permission: {}", e.getMessage(), e);
        }
        return false;
    }


    @Override
    public PermissionGroupMapping setPermissionForGroup(PermissionGroupMapping permissionGroupMapping) {
        try {
            logger.info("Setting permission for group...");
            if (permissionGroupMapping.getGroupId() == null || permissionGroupMapping.getPermissionId() == null) {
                throw new IllegalArgumentException("Group ID and Permission ID are required");
            }
            permissionGroupMappingRepository.save(permissionGroupMapping);
        } catch (Exception e) {
            logger.error("Error occurred while setting permission for group: {}", e.getMessage(), e);
            throw e;
        }
        return permissionGroupMapping;
    }

    @Override
    public List<PermissionGroup> getAllPermissionsByGroupId(UUID groupId) {

        List<PermissionGroupMapping> mappings = permissionGroupMappingRepository.findAllByGroupId(groupId);
        List<PermissionGroup> result = new ArrayList<>();

        for (PermissionGroupMapping mapping : mappings) {
            Optional<Permission> permissionOptional = permissionRepository.findById(mapping.getPermissionId());
            if (permissionOptional.isPresent()) {
                Permission permission = permissionOptional.get();
                PermissionGroup mappingResponse = new PermissionGroup();
                mappingResponse.setPermissionId(permission.getPermissionId());
                mappingResponse.setPermissionName(permission.getPermissionName());
                mappingResponse.setPermissionType(permission.getPermissionType());
                List<PermissionLevelGroup> permissionLevels = permissionLevelGroupRepository.findByPermissionId(permission.getPermissionId());
                List<PermissionLevelGroupResponse> levelGroupResponses = permissionLevels.stream()
                        .map(levelGroup -> PermissionLevelGroupResponse.builder()
                                .accessLevel(levelGroup.getAccessLevel())
                                .accessLevelValue(levelGroup.isAccessLevelValue())
                                .build())
                        .collect(Collectors.toList());

                mappingResponse.setLevelGroups(levelGroupResponses);
                result.add(mappingResponse);
            }
        }

        return result;
    }

    @Override
    @Transactional
    public void deletePermissionByGroupIdAndPermissionId(UUID groupId, UUID permissionId) {
        try {
            permissionGroupMappingRepository.deletePermissionByGroupIdAndPermissionId(groupId, permissionId);
        } catch (Exception e) {
            logger.error("An error occurred while deleting permissions by group ID: {}", e.getMessage(), e);
        }
    }




    @Override
    public List<PermissionRoleMapping> setPermissionsForRole(AddPermissionsForRoleRequest permissionRoleMapping) {
        try {
            logger.info("Setting permissions for role...");
            if (permissionRoleMapping.getPermissionIds().isEmpty()) {
                throw new IllegalArgumentException("Permission IDs are required");
            }

            List<Permission> permissions = permissionRepository.findAllById(permissionRoleMapping.getPermissionIds());

            List<PermissionRoleMapping> mappings = permissions.stream()
                    .map(permission -> PermissionRoleMapping.builder()
                            .roleId(permissionRoleMapping.getRoleId())
                            .permissionId(permission.getPermissionId())
                            .build())
                    .collect(Collectors.toList());
            List<PermissionRoleMapping> savedMappings = permissionRoleMappingRepository.saveAll(mappings);
            logger.info("Permissions set for role successfully"+mappings);
            return savedMappings;
        } catch (Exception e) {
            logger.error("Error occurred while setting permission for role: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean removePermissionsFromRole(AddPermissionsForRoleRequest permissionRoleMapping) {
        try {
            List<Permission> permissions = permissionRepository.findAllById(permissionRoleMapping.getPermissionIds());
            List<PermissionRoleMapping> mappings = permissions.stream()
                    .map(permission -> PermissionRoleMapping.builder()
                            .roleId(permissionRoleMapping.getRoleId())
                            .permissionId(permission.getPermissionId())
                            .build())
                    .collect(Collectors.toList());
            permissionRoleMappingRepository.deleteAll(mappings);
            logger.info("Permissions removed for role successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error occurred while setting permission for role: {}", e.getMessage(), e);
            throw e;
        }
    }



    @Override
    public List<PermissionUserMapping> setPermissionsForUser(AddPermissionsForUserRequest addPermissionsForUserRequest) {
        UUID userId = addPermissionsForUserRequest.getUserId();
        List<UUID> permissionGroupRequests = addPermissionsForUserRequest.getPermissionIds();
        List<PermissionUserMapping> permissionUserMappings = new ArrayList<>();

        for (UUID permissionId : permissionGroupRequests) {
            PermissionUserMapping permissionUserMapping = new PermissionUserMapping();
            permissionUserMapping.setUserId(userId);
            permissionUserMapping.setPermissionId(permissionId);
            permissionUserMappings.add(permissionUserMapping);
        }

        List<PermissionUserMapping> savedMappings = permissionUserMappingRepository.saveAll(permissionUserMappings);
        return savedMappings;
    }



    @Override
    public List<PermissionGroup> getAllPermissionsByUserId(UUID userId) {
        List<PermissionUserMapping> mappings = permissionUserMappingRepository.findAllByUserId(userId);
        List<PermissionGroup> result = new ArrayList<>();

        for (PermissionUserMapping mapping : mappings) {
            Optional<Permission> permissionOptional = permissionRepository.findById(mapping.getPermissionId());
            if (permissionOptional.isPresent()) {
                Permission permission = permissionOptional.get();
                PermissionGroup mappingResponse = new PermissionGroup();
                mappingResponse.setPermissionId(permission.getPermissionId());
                mappingResponse.setPermissionName(permission.getPermissionName());
                mappingResponse.setPermissionType(permission.getPermissionType());
                List<PermissionLevelGroup> permissionLevels = permissionLevelGroupRepository.findByPermissionId(permission.getPermissionId());
                List<PermissionLevelGroupResponse> levelGroupResponses = permissionLevels.stream()
                        .map(levelGroup -> PermissionLevelGroupResponse.builder()
                                .accessLevel(levelGroup.getAccessLevel())
                                .accessLevelValue(levelGroup.isAccessLevelValue())
                                .build())
                        .collect(Collectors.toList());

                mappingResponse.setLevelGroups(levelGroupResponses);
                result.add(mappingResponse);
            }
        }

        return result;
    }

    @Override
    public boolean removePermissionsFromUser(AddPermissionsForUserRequest addPermissionsForUserRequest) {
        try {
            List<Permission> permissions = permissionRepository.findAllById(addPermissionsForUserRequest.getPermissionIds());
            List<PermissionUserMapping> mappings = permissions.stream()
                    .map(permission -> PermissionUserMapping.builder()
                            .userId(addPermissionsForUserRequest.getUserId())
                            .permissionId(permission.getPermissionId())
                            .build())
                    .collect(Collectors.toList());
            permissionUserMappingRepository.deleteAll(mappings);
            logger.info("Permissions removed for role successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error occurred while setting permission for role: {}", e.getMessage(), e);
            throw e;
        }
    }







    @Override
    @Transactional
    public void deletePermissionByRoleIdAndPermissionId(UUID roleId, UUID permissionId) {
        try {
            permissionRoleMappingRepository.deletePermissionByRoleIdAndPermissionId(roleId, permissionId);
        } catch (Exception e) {
            logger.error("An error occurred while deleting permissions by role ID: {}", e.getMessage(), e);
        }

    }



    @Override
    @Transactional
    public void deletePermissionByUserIdAndPermissionId(UUID userId, UUID permissionId) {
        try {
            permissionUserMappingRepository.deleteByUserIdAndPermissionId(userId, permissionId);
        } catch (Exception e) {
            logger.error("An error occurred while deleting permission by user ID and permission ID: {}", e.getMessage(), e);
        }
    }


    @Override
    public List<PermissionGroup> getAllPermissionsByRoleId(UUID roleId) {
        List<PermissionRoleMapping> mappings = permissionRoleMappingRepository.findAllByRoleId(roleId);
        List<PermissionGroup> result = new ArrayList<>();

        for (PermissionRoleMapping mapping : mappings) {
            Optional<Permission> permissionOptional = permissionRepository.findById(mapping.getPermissionId());
            if (permissionOptional.isPresent()) {
                Permission permission = permissionOptional.get();
                PermissionGroup mappingResponse = new PermissionGroup();
                mappingResponse.setPermissionId(permission.getPermissionId());
                mappingResponse.setPermissionName(permission.getPermissionName());
                mappingResponse.setPermissionType(permission.getPermissionType());
                List<PermissionLevelGroup> permissionLevels = permissionLevelGroupRepository.findByPermissionId(permission.getPermissionId());
                List<PermissionLevelGroupResponse> levelGroupResponses = permissionLevels.stream()
                        .map(levelGroup -> PermissionLevelGroupResponse.builder()
                                .accessLevel(levelGroup.getAccessLevel())
                                .accessLevelValue(levelGroup.isAccessLevelValue())
                                .build())
                        .collect(Collectors.toList());

                mappingResponse.setLevelGroups(levelGroupResponses);
                result.add(mappingResponse);
            }
        }

        return result;
    }

}