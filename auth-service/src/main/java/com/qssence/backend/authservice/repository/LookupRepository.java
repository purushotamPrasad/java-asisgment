package com.qssence.backend.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qssence.backend.authservice.dbo.Lookup;

@Repository
public interface LookupRepository extends JpaRepository<Lookup, Long>{

}
