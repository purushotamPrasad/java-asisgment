package com.qssence.backend.subscription_panel.repository;

import com.qssence.backend.subscription_panel.dbo.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findByCompanyEmailId(String companyEmailId);// check if an email already exists
}
