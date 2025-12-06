package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.Permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Permission findByPermissionName(String permissionName);
}
