package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dbo.PasswordResetToken;
import com.qssence.backend.authservice.dto.responce.ResetData;
import com.qssence.backend.authservice.repository.PasswordResetTokenRepository;
import com.qssence.backend.authservice.service.MailServices.SendMail;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final Keycloak keycloak;
    private final SendMail sendMail;

    private static final int EXPIRATION_MINUTES = 30;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    private String generateShortToken() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString().toUpperCase();
    }

    // Step 1: create reset token
    public ResetData createPasswordResetToken(String email) {
        //UsersResource usersResource = keycloak.realm("QssenceRealm").users();
        UsersResource usersResource = keycloak.realm("authrealm").users();
        List<UserRepresentation> users = usersResource.search(email, true);

        if (users.isEmpty()) {
            throw new RuntimeException("No account found with this email.");
        }

        String token = generateShortToken();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email.toLowerCase());
        resetToken.setExpiryDate(expiry);

        tokenRepository.save(resetToken);

        String resetLink = "http://46.28.44.11:3001/reset-password?token=" + token + "&email=" + email;

        return new ResetData(token, resetLink);
    }

    // Step 2: validate and reset
    public String resetPassword(String token, String email, String newPassword) {
        Optional<PasswordResetToken> optional =
                tokenRepository.findByTokenAndEmail(token.toUpperCase(), email.toLowerCase());

        if (optional.isEmpty()) {
            return "Invalid reset token or email!";
        }

        PasswordResetToken resetToken = optional.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.deleteByToken(token);
            return "Reset token has expired!";
        }

        try {
            UsersResource usersResource = keycloak.realm("QssenceRealm").users();
            List<UserRepresentation> users = usersResource.search(resetToken.getEmail(), true);

            if (users.isEmpty()) {
                return "No account found with this email!";
            }

            UserRepresentation user = users.get(0);

            CredentialRepresentation newCred = new CredentialRepresentation();
            newCred.setType(CredentialRepresentation.PASSWORD);
            newCred.setValue(newPassword);
            newCred.setTemporary(false);

            usersResource.get(user.getId()).resetPassword(newCred);

            tokenRepository.deleteByToken(token);

            // ✅ Mail user
            sendMail.sendPasswordResetSuccessEmail(email, newPassword);

            return "Password reset successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Internal server error while resetting password.";
        }
    }
}
