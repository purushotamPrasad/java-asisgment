package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.PermissionUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionUserMappingRepository
		extends JpaRepository<PermissionUserMapping, org.hibernate.validator.constraints.UUID> {
	List<PermissionUserMapping> findAllByUserId(UUID userId);

	void deleteByUserIdAndPermissionId(UUID userId, UUID permissionId);
}
