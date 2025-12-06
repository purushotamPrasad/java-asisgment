package com.qssence.backend.authservice.service.Key;

import com.qssence.backend.authservice.dbo.Key.Feature;
import com.qssence.backend.authservice.dbo.Key.ImportedLicense;
import com.qssence.backend.authservice.dbo.Key.Plan;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.Key.FeatureDto;
import com.qssence.backend.authservice.dto.Key.ImportedLicenseDto;
import com.qssence.backend.authservice.dto.Key.PlanDto;
import com.qssence.backend.authservice.repository.key.FeatureRepository;
import com.qssence.backend.authservice.repository.key.ImportedLicenseRepository;
import com.qssence.backend.authservice.repository.key.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicenseImportService {

    private final ImportedLicenseRepository licenseRepository;
    private final PlanRepository planRepository;
    private final FeatureRepository featureRepository;
    private final RestTemplate restTemplate;

    public String importLicense(String encodedLicenseKey) {
        // ... (fetch and decode license as before)

      //String url = "http://localhost:8085/api/v2/licenses/decode/" + encodedLicenseKey;
        String url = "http://46.28.44.11:8085/api/v2/licenses/decode/" + encodedLicenseKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse<ImportedLicenseDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<ApiResponse<ImportedLicenseDto>>() {}
        );

        ImportedLicenseDto dto = response.getBody() != null ? response.getBody().getData() : null;
        if (dto == null) throw new RuntimeException("License decode failed or returned null");
        if (dto.getPlans() == null) throw new RuntimeException("License plans are missing from the decoded license");

        ImportedLicense license = licenseRepository.findByLicenseId(dto.getLicenseId()).orElse(new ImportedLicense());
        // ✅ Basic details
        license.setLicenseId(dto.getLicenseId());
        license.setLicenseKey(dto.getLicenseKey());       // 🔹 save the key
        license.setCompanyId(dto.getCompanyId());
        license.setCompanyName(dto.getCompanyName());
        license.setCompanyPrefix(dto.getCompanyPrefix());
        license.setLocation(dto.getLocation());
        license.setEmail(dto.getEmail());
        license.setPurchaseDate(dto.getPurchaseDate());
        license.setExpiryDate(dto.getExpiryDate());
        license.setDescription(dto.getDescription());
        license.setPurchaseCost(dto.getPurchaseCost());
        license.setTotalUserAccess(dto.getTotalUserAccess());
        license.setAdminAccountAllowed(dto.getAdminAccountAllowed());
        license.setUserAccountAllowed(dto.getUserAccountAllowed());
        license.setActive(dto.isActive());

        // ✅ auto compute expired
        boolean expired = dto.getExpiryDate() != null && dto.getExpiryDate().isBefore(LocalDate.now());
        license.setExpired(expired);

        // ✅ timestamps
        if (license.getImportedAt() == null) {
            license.setImportedAt(LocalDateTime.now());
        }
        license.setLastVerifiedAt(LocalDateTime.now());

        // ✅ Plans & Features
        List<Plan> planEntities = dto.getPlans().stream().map(planDto -> {
            Plan plan = planRepository.findById(planDto.getPlanId()).orElse(new Plan());
            plan.setPlanId(planDto.getPlanId());
            plan.setName(planDto.getName());
            plan.setDescription(planDto.getDescription());
            plan.setImportedLicense(license);

            List<Feature> featureEntities = planDto.getFeatures().stream().map(featureDto -> {
                Feature feature = featureRepository.findById(featureDto.getFeaturesId()).orElse(new Feature());
                feature.setFeaturesId(featureDto.getFeaturesId());
                feature.setName(featureDto.getName());
                return feature;
            }).collect(Collectors.toList());

            plan.setFeatures(featureEntities);
            return plan;
        }).collect(Collectors.toList());

        license.setPlans(planEntities);

        licenseRepository.save(license);
        return "License imported/updated successfully.";
    }

    /**
     * Get all imported licenses with expiry check
     */
    public List<ImportedLicenseDto> getAllImportedLicenses() {
        List<ImportedLicense> licenses = licenseRepository.findAll();

        return licenses.stream().map(license -> {
            List<PlanDto> planDtos = license.getPlans().stream().map(plan -> {
                List<FeatureDto> featureDtos = plan.getFeatures().stream().map(feature ->
                        FeatureDto.builder()
                                .featuresId(feature.getFeaturesId())
                                .name(feature.getName())
                                .build()
                ).collect(Collectors.toList());

                return PlanDto.builder()
                        .planId(plan.getPlanId())
                        .name(plan.getName())
                        .description(plan.getDescription())
                        .features(featureDtos)
                        .build();
            }).collect(Collectors.toList());

            // ✅ check expiry on the fly also
            boolean expired = license.getExpiryDate() != null && license.getExpiryDate().isBefore(LocalDate.now());

            return ImportedLicenseDto.builder()
                    .licenseId(license.getLicenseId())
                    .licenseKey(license.getLicenseKey())
                    .companyId(license.getCompanyId())
                    .companyName(license.getCompanyName())
                    .companyPrefix(license.getCompanyPrefix())
                    .location(license.getLocation())
                    .email(license.getEmail())
                    .purchaseDate(license.getPurchaseDate())
                    .expiryDate(license.getExpiryDate())
                    .description(license.getDescription())
                    .active(license.isActive())
                    .expired(expired)   // ✅ show expired flag
                    .purchaseCost(license.getPurchaseCost())
                    .totalUserAccess(license.getTotalUserAccess())
                    .adminAccountAllowed(license.getAdminAccountAllowed())
                    .userAccountAllowed(license.getUserAccountAllowed())
                    .importedAt(license.getImportedAt())
                    .lastVerifiedAt(license.getLastVerifiedAt())
                    .plans(planDtos)
                    .build();
        }).collect(Collectors.toList());
    }
}
