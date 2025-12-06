package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.RoleGroupMapping;

import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleGroupMappingRepository extends JpaRepository<RoleGroupMapping, UUID> {
    RoleGroupMapping findByGroupIdAndRoleId(String groupId, String roleId);
}
