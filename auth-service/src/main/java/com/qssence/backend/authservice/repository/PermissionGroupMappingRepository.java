package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.PermissionGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionGroupMappingRepository
		extends JpaRepository<PermissionGroupMapping, org.hibernate.validator.constraints.UUID> {
	List<PermissionGroupMapping> findAllByGroupId(UUID groupId);

	void deletePermissionByGroupIdAndPermissionId(UUID groupId, UUID permissionId);
}
