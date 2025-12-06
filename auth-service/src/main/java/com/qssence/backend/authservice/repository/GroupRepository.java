package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Set<Group> findByNameIn(Set<String> groupNames);
    Optional<Group> findGroupByName(String name);

}
