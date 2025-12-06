package com.qssence.backend.subscription_panel.repository;

import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.UserLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLicenseRepository extends JpaRepository<UserLicense, Long> {
    UserLicense findByCompany(Company company);
}
