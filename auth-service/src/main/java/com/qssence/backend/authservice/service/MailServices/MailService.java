package com.qssence.backend.authservice.service.MailServices;

import com.qssence.backend.authservice.entity.SmtpConfig;
import com.qssence.backend.authservice.repository.MailRepository.SmtpConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MailService {

    @Autowired
    MailConfiguration mailConfigurationService;

    @Autowired
    private SmtpConfigRepository smtpConfigRepository;

    public SmtpConfig SaveMailConfig(SmtpConfig mailconfig) {
        try {
            return smtpConfigRepository.save(mailconfig);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save email configuration: " + e.getMessage());
        }
    }

    public List<SmtpConfig> ShowAllConfig() {
        List<SmtpConfig> findAll = (List<SmtpConfig>) smtpConfigRepository.findAll();
        return findAll;
    }

    public boolean deleteEmailConfig(Long id) {
        if (smtpConfigRepository.existsById(id)) {
            smtpConfigRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Update an existing SMTP configuration by ID
    public SmtpConfig updateMailConfig(Long id, SmtpConfig updatedConfig) {
        Optional<SmtpConfig> existingConfigOptional = smtpConfigRepository.findById(id);

        if (existingConfigOptional.isPresent()) {
            SmtpConfig existingConfig = existingConfigOptional.get();

            // Update the fields with new values
            existingConfig.setMailProtocol(updatedConfig.getMailProtocol());
            existingConfig.setMailHost(updatedConfig.getMailHost());
            existingConfig.setMailPort(updatedConfig.getMailPort());
            existingConfig.setMailUsername(updatedConfig.getMailUsername());
            existingConfig.setMailPassword(updatedConfig.getMailPassword());
            existingConfig.setMailProtocol(updatedConfig.getMailProtocol());

            return smtpConfigRepository.save(existingConfig);
        } else {
            throw new RuntimeException("SMTP Configuration with ID " + id + " not found");
        }
    }

    public SmtpConfig getActiveMailConfig() {
        return smtpConfigRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No SMTP configuration found in the database."));
    }
}
