package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.Permission.Permission;
import com.qssence.backend.authservice.dbo.PermissionRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRoleMappingRepository extends JpaRepository<PermissionRoleMapping, org.hibernate.validator.constraints.UUID> {
    List<PermissionRoleMapping> findAllByRoleId(UUID roleId);
    void deletePermissionByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    default List<PermissionRoleMapping> findByPermissionIn(List<Permission> permissions) {
        return null;
    }
}
