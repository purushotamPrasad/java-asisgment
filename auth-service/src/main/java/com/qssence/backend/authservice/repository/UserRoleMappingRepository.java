package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.UserRoleMapping;

import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleMappingRepository  extends JpaRepository<UserRoleMapping, UUID> {



}
