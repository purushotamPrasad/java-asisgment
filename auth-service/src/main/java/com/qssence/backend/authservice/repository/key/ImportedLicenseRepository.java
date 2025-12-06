package com.qssence.backend.authservice.repository.key;

import com.qssence.backend.authservice.dbo.Key.ImportedLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportedLicenseRepository extends JpaRepository<ImportedLicense, Long> {

    Optional<ImportedLicense> findByLicenseId(Long licenseId);
    // ✅ Active license fetch karne ke liye
    Optional<ImportedLicense> findFirstByActiveTrue();
}

