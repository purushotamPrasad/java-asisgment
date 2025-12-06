package com.qssence.backend.subscription_panel.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserLicenseRequestDto {
    @NotNull(message = "Company ID is required")
    private String companyId;

    private LocalDate purchaseDate;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Purchase cost is required")
    @Min(value = 0, message = "Purchase cost must be positive")
    private Long purchaseCost;

    @NotNull(message = "Total user access is required")
    @Min(value = 1, message = "Total user access must be at least 1")
    private Long totalUserAccess;

    @NotNull(message = "Admin account allowed is required")
    @Min(value = 0, message = "Admin account must be at least 0")
    private Long adminAccountAllowed;

    @NotNull(message = "User account allowed is required")
    @Min(value = 0, message = "User account must be at least 0")
    private Long userAccountAllowed;

    private String description;
}
