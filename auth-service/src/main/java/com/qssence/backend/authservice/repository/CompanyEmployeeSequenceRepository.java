package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.CompanyEmployeeSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyEmployeeSequenceRepository extends JpaRepository<CompanyEmployeeSequence, String> {
}
