package com.qssence.backend.subscription_panel.service.impl;

import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.UserLicense;
import com.qssence.backend.subscription_panel.dbo.plans.CompanyPlanFeature;
import com.qssence.backend.subscription_panel.dto.request.UserLicenseRequestDto;
import com.qssence.backend.subscription_panel.dto.response.FeatureResponse;
import com.qssence.backend.subscription_panel.dto.response.PlanResponse;
import com.qssence.backend.subscription_panel.dto.response.UserLicenseResponseDto;
import com.qssence.backend.subscription_panel.repository.CompanyRepository;
import com.qssence.backend.subscription_panel.repository.UserLicenseRepository;
import com.qssence.backend.subscription_panel.repository.plansRepo.CompanyPlanFeatureRepository;
import com.qssence.backend.subscription_panel.service.MailService;
import com.qssence.backend.subscription_panel.service.UserLicenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLicenseServiceImpl implements UserLicenseService {

    private final UserLicenseRepository userLicenseRepository;
    private final CompanyRepository companyRepository;
    private final CompanyPlanFeatureRepository companyPlanFeatureRepository;
    private final MailService mailService;

    /**
     * Create a new User License for a company
     */
    @Override
    public UserLicenseResponseDto createUserLicense(UserLicenseRequestDto request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (userLicenseRepository.findByCompany(company) != null) {
            throw new IllegalArgumentException("A user license already exists for this company.");
        }

        if (request.getAdminAccountAllowed() + request.getUserAccountAllowed() > request.getTotalUserAccess()) {
            throw new IllegalArgumentException("Admin and User accounts cannot exceed total user access limit");
        }

        LocalDate purchaseDate = request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDate.now();
        LocalDate expiryDate = request.getExpiryDate();

        UserLicense userLicense = UserLicense.builder()
                .company(company)
                .purchaseDate(purchaseDate)
                .expiryDate(expiryDate)
                .purchaseCost(request.getPurchaseCost())
                .totalUserAccess(request.getTotalUserAccess())
                .adminAccountAllowed(request.getAdminAccountAllowed())
                .userAccountAllowed(request.getUserAccountAllowed())
                .description(request.getDescription())
                .active(true)
                .build();

        userLicense = userLicenseRepository.save(userLicense);

        // 🔹 Generate License Key
        String licenseKey = generateLicenseKey(userLicense);

        // 🔹 Send Email
        String subject = "Your License Has Been Created!";
        String emailBody = "<h3>Dear " + company.getCompanyName() + ",</h3>"
                + "<p>Your license has been successfully created.</p>"
                + "<p><b>License Key:</b> " + licenseKey + "</p>"
                + "<p><b>Purchase Date:</b> " + purchaseDate + "</p>"
                + "<p><b>Expiry Date:</b> " + expiryDate + "</p>"
                + "<p><b>Total User Access:</b> " + request.getTotalUserAccess() + "</p>"
                + "<p><b>Login ID:</b> " + company.getCompanyEmailId() + "</p>"
                + "<p><b>Password:</b> " + company.getPassword() + "</p>"  // ✅ PASSWORD ADD
                + "<p>Thank you for choosing our service!</p>";
        mailService.sendEmail(company.getCompanyEmailId(), subject, emailBody);

        return mapToResponse(userLicense);
    }

    /**
     * Generate Base64 Encoded License Key
     */
    private String generateLicenseKey(UserLicense license) {
        String rawKey = license.getLicenseId() + ":" + license.getCompany().getCompanyId();
        // Use URL-safe Base64 to avoid issues with '+' and '/' in URLs/emails; omit padding for brevity
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get all licenses (active/inactive/expired)
     */
    @Override
    public List<UserLicenseResponseDto> getAllUserLicenses() {
        return userLicenseRepository.findAll().stream()
                .map(this::mapToResponse) // ✅ All licenses will be shown with status
                .collect(Collectors.toList());
    }

    /**
     * Get license by ID
     */
    @Override
    public UserLicenseResponseDto getUserLicenseById(Long id) {
        UserLicense userLicense = userLicenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User License not found"));
        return mapToResponse(userLicense);
    }

    /**
     * Convert Entity -> DTO with plan + feature mapping
     */
    private UserLicenseResponseDto mapToResponse(UserLicense license) {
        List<CompanyPlanFeature> planFeatures = companyPlanFeatureRepository.findByCompany(license.getCompany());
        Map<Long, PlanResponse.PlanResponseBuilder> planMap = new HashMap<>();

        for (CompanyPlanFeature pf : planFeatures) {
            Long planId = pf.getPlan().getPlanId();

            PlanResponse.PlanResponseBuilder planBuilder = planMap.computeIfAbsent(planId, id -> PlanResponse.builder()
                    .planId(planId)
                    .name(pf.getPlan().getName())
                    .description(pf.getPlan().getDescription())
                    .features(new ArrayList<>())
            );

            List<FeatureResponse> featureList = planBuilder.build().getFeatures();
            if (featureList == null) {
                featureList = new ArrayList<>();
            }
            featureList.add(FeatureResponse.builder()
                    .featuresId(pf.getFeature().getFeaturesId())
                    .name(pf.getFeature().getName())
                    .build());

            planBuilder.features(featureList);
        }

        boolean isExpired = license.getExpiryDate() != null && license.getExpiryDate().isBefore(LocalDate.now());

        return UserLicenseResponseDto.builder()
                .licenseId(license.getLicenseId())
                .licenseKey(generateLicenseKey(license))
                .companyId(license.getCompany().getCompanyId())
                .companyName(license.getCompany().getCompanyName())
                .companyPrefix(license.getCompany().getCompanyPrefix())
                .location(license.getCompany().getLocation())
                .email(license.getCompany().getCompanyEmailId())
                .password(license.getCompany().getPassword())
                .purchaseCost(license.getPurchaseCost())
                .totalUserAccess(license.getTotalUserAccess())
                .adminAccountAllowed(license.getAdminAccountAllowed())
                .userAccountAllowed(license.getUserAccountAllowed())
                .purchaseDate(license.getPurchaseDate())
                .expiryDate(license.getExpiryDate())
                .description(license.getDescription())
                .plans(planMap.values().stream().map(PlanResponse.PlanResponseBuilder::build).collect(Collectors.toList()))
                .active(license.isActive())
                .expired(isExpired) // ✅ Show expired status
                .build();
    }

    /**
     * Decode and validate license key
     */
//    @Override
//    public UserLicenseResponseDto decodeLicenseKey(String encodedKey) {
//        try {
//            byte[] decodedBytes = Base64.getDecoder().decode(encodedKey);
//            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
//
//            String[] parts = decodedString.split(":");
//            if (parts.length < 1) {
//                throw new IllegalArgumentException("Invalid License Key Format");
//            }
//
//            Long licenseId = Long.parseLong(parts[0]);
//            UserLicense userLicense = userLicenseRepository.findById(licenseId)
//                    .orElseThrow(() -> new RuntimeException("User License not found"));
//
//            if (!userLicense.isActive()) {
//                throw new IllegalArgumentException("Your key is disabled, please insert your new generated key.");
//            }
//            if (userLicense.getExpiryDate().isBefore(LocalDate.now())) {
//                throw new IllegalArgumentException("Your key has expired, please renew.");
//            }
//
//            return mapToResponse(userLicense);
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @Override
    public UserLicenseResponseDto decodeLicenseKey(String encodedKey) {
        try {
            if (encodedKey == null || encodedKey.trim().isEmpty()) {
                throw new IllegalArgumentException("License key is missing or empty");
            }

            // Normalize incoming key: trim, remove line-breaks, convert spaces restored from '+' in query params
            encodedKey = encodedKey.trim();
            // Remove all whitespace/newlines
            encodedKey = encodedKey.replaceAll("\\s+", "");
            // Some proxies turn '+' into space before it reaches us; if any spaces remain, convert back
            encodedKey = encodedKey.replace(' ', '+');
            // Add padding if missing (Base64 length must be multiple of 4)
            int mod = encodedKey.length() % 4;
            if (mod > 0) {
                encodedKey = encodedKey + "====".substring(mod);
            }
            log.info("Encoded license key received: {}", encodedKey);

            byte[] decodedBytes;
            try {
                // First try URL-safe decoding
                decodedBytes = Base64.getUrlDecoder().decode(encodedKey);
            } catch (IllegalArgumentException urlEx) {
                // Fallback to standard decoder if key wasn't URL-safe encoded
                decodedBytes = Base64.getDecoder().decode(encodedKey);
            }
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            log.info("Decoded license string: {}", decodedString);

            String[] parts = decodedString.split(":");
            if (parts.length < 1) {
                throw new IllegalArgumentException("Invalid license key format");
            }

            Long licenseId = Long.parseLong(parts[0]);
            log.info("Decoded license ID: {}", licenseId);

            UserLicense userLicense = userLicenseRepository.findById(licenseId)
                    .orElseThrow(() -> new RuntimeException("License not found in database"));

            if (!userLicense.isActive()) {
                throw new IllegalArgumentException("Your license key is disabled. Please generate a new key.");
            }

            if (userLicense.getExpiryDate() != null && userLicense.getExpiryDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Your license key has expired. Please renew your license.");
            }

            log.info("License validation successful for company: {}", userLicense.getCompany().getCompanyName());

            return mapToResponse(userLicense);

        } catch (IllegalArgumentException e) {
            log.error("License decode validation failed: {}", e.getMessage());
            // Re-throw as IllegalArgumentException so controller returns 400 with this exact message
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error while decoding license", e);
            throw new RuntimeException("Error importing license: License decode failed or returned null");
        }
    }


    /**
     * Update license details (upgrade/renew)
     */
    @Override
    public UserLicenseResponseDto updateUserLicense(Long licenseId, UserLicenseRequestDto request) {
        UserLicense license = userLicenseRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("User License not found"));

        license.setExpiryDate(request.getExpiryDate());
        license.setPurchaseCost(request.getPurchaseCost());
        license.setTotalUserAccess(request.getTotalUserAccess());
        license.setAdminAccountAllowed(request.getAdminAccountAllowed());
        license.setUserAccountAllowed(request.getUserAccountAllowed());
        license.setDescription(request.getDescription());
        license.setPurchaseDate(LocalDate.now());
        license.setActive(true); // reactivate on update

        userLicenseRepository.save(license);

        // Generate new key
        String newLicenseKey = generateLicenseKey(license);

        // Send email
        String subject = "Your License Has Been Updated!";
        String emailBody = "<h3>Dear " + license.getCompany().getCompanyName() + ",</h3>"
                + "<p>Your license key has been <b>updated</b>.</p>"
                + "<p><b>New License Key:</b> " + newLicenseKey + "</p>"
                + "<p><b>Expiry Date:</b> " + license.getExpiryDate() + "</p>"
                + "<p><b>Total User Access:</b> " + license.getTotalUserAccess() + "</p>"
                + "<p>Thank you for choosing our service!</p>";
        mailService.sendEmail(license.getCompany().getCompanyEmailId(), subject, emailBody);

        return mapToResponse(license);
    }

    /**
     * Delete license (soft delete by marking inactive)
     */
    public void deleteUserLicense(Long licenseId) {
        UserLicense license = userLicenseRepository.findById(licenseId)
                .orElseThrow(() -> new RuntimeException("User License not found"));
        license.setActive(false); // soft delete
        userLicenseRepository.save(license);
    }
}
