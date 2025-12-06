package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.Permission.PermissionLevelGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionLevelGroupRepository
		extends JpaRepository<PermissionLevelGroup, org.hibernate.validator.constraints.UUID> {
	List<PermissionLevelGroup> findByPermissionId(UUID permissionId);
}
