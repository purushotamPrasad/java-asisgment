package com.qssence.backend.authservice.repository;

import com.qssence.backend.authservice.dbo.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndEmail(String token, String email);  // ✅ token + email verify
    void deleteByToken(String token);
}
