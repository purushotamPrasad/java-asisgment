package com.qssence.backend.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qssence.backend.authservice.dbo.UserRole;

import java.util.Optional;

@Repository
public interface UserRoleRepository  extends JpaRepository<UserRole, Long>{

    UserRole findByUserRoleName(String roleName);


}
