package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dbo.Group;
import com.qssence.backend.authservice.dbo.UserRole;
import com.qssence.backend.authservice.dbo.Permission.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionConflictResolver {

}

    /**
     * Resolve permission conflicts for a user
     * Priority: Role permissions > Group permissions > Individual permissions
     */
//    public Map<String, String> resolvePermissionConflicts(UserMaster user) {
//        Map<String, String> resolvedPermissions = new HashMap<>();

//        // Get all permissions from different sources
//        Set<Permission> rolePermissions = getUserRolePermissions(user);
//        Set<Permission> groupPermissions = getUserGroupPermissions(user);

        // Resolve conflicts based on priority
//        for (Permission permission : rolePermissions) {
//            resolvedPermissions.put(permission.getPermissionName(), "ROLE_BASED");
//        }
//
//        for (Permission permission : groupPermissions) {
//            if (!resolvedPermissions.containsKey(permission.getPermissionName())) {
//                resolvedPermissions.put(permission.getPermissionName(), "GROUP_BASED");
//            }
//        }
//
//        return resolvedPermissions;
//    }

        /**
         * Check for permission conflicts between groups
         */
//    public List<String> checkGroupPermissionConflicts(Set<Group> groups) {
//        List<String> conflicts = new ArrayList<>();
//
//        for (Group group1 : groups) {
//            for (Group group2 : groups) {
//                if (!group1.equals(group2)) {
//                    // Check for conflicting permissions
//                    Set<String> group1Permissions = getGroupPermissions(group1);
//                    Set<String> group2Permissions = getGroupPermissions(group2);
//
//                    Set<String> intersection = group1Permissions.stream()
//                            .filter(group2Permissions::contains)
//                            .collect(Collectors.toSet());
//
//                    if (!intersection.isEmpty()) {
//                        conflicts.add(String.format("Conflict between groups %s and %s for permissions: %s",
//                                group1.getName(), group2.getName(), intersection));
//                    }
//                }
//            }
//        }
//
//        return conflicts;
//    }

        /**
         * Get effective permissions for a user
         */
//    public Set<Permission> getEffectivePermissions(UserMaster user) {
//        Set<Permission> effectivePermissions = new HashSet<>();
//
//        // Add role-based permissions
//        effectivePermissions.addAll(getUserRolePermissions(user));
//
//        // Add group-based permissions (only if not already present from roles)
//        Set<Permission> groupPermissions = getUserGroupPermissions(user);
//        for (Permission permission : groupPermissions) {
//            if (effectivePermissions.stream().noneMatch(p -> p.getPermissionName().equals(permission.getPermissionName()))) {
//                effectivePermissions.add(permission);
//            }
//        }
//
//        return effectivePermissions;
//    }

//    private Set<Permission> getUserRolePermissions(UserMaster user) {
//        return user.getRole().stream()
//                .flatMap(role -> role.getPermissions().stream())
//                .collect(Collectors.toSet());
//    }
//
//    private Set<Permission> getUserGroupPermissions(UserMaster user) {
//        return user.getGroups().stream()
//                .flatMap(group -> getGroupPermissions(group).stream())
//                .map(permissionName -> new Permission.Permission()) // Create permission object
//                .collect(Collectors.toSet());
//    }

//    private Set<String> getGroupPermissions(Group group) {
//        // Implementation to get group permissions
//        return new HashSet<>();
//    }


