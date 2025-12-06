package com.qssence.backend.authservice.repository.MailRepository;

import com.qssence.backend.authservice.entity.SmtpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmtpConfigRepository extends JpaRepository<SmtpConfig,Long> {

    Optional<SmtpConfig> findByProviderName(String providerName);
}
