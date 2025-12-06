package com.qssence.backend.authservice.dto.Key;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ImportedLicenseDto {
    private Long licenseId;
    private String licenseKey;

    private String companyId;
    private String companyName;
    private String companyPrefix;
    private String location;
    private String email;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String description;

    private boolean active;
    private boolean expired;          // ✅ show expiry status in response

    private Long purchaseCost;
    private Long totalUserAccess;
    private Long adminAccountAllowed;
    private Long userAccountAllowed;

    private LocalDateTime importedAt;    // ✅ when license imported in admin-panel
    private LocalDateTime lastVerifiedAt; // ✅ when last validated with subscription-panel

    private List<PlanDto> plans;         // ✅ all plans with features
}
