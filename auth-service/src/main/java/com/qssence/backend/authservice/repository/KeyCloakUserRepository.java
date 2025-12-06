package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.KeyCloakUser;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyCloakUserRepository extends CrudRepository<KeyCloakUser, String> {
	
}
