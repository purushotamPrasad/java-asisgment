package com.qssence.backend.subscription_panel.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserLicenseResponseDto {

    private Long licenseId;
    private String licenseKey;  // 🔹 New field
    private String companyId;
    private String companyName;
    private String companyPrefix;
    private String location;
    private String email;
    private String password;

    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String description;

    private boolean active;

    private Long purchaseCost;
    private Long totalUserAccess;
    private Long adminAccountAllowed;
    private Long userAccountAllowed;
    private boolean expired;  // ✅ NEW FIELD

    private List<PlanResponse> plans;  // Assigned plans with features
//    private boolean active = true; // Default to true when created


}
