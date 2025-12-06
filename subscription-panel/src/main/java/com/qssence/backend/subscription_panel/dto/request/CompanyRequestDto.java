package com.qssence.backend.subscription_panel.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDto {

    @NotBlank(message = "Company name is required")
    private String companyName;
    private String companyPrefix;
    private String location;
    private String region;
    private String country;
    private String timezone;
    private String phoneNo;

    @NotBlank(message = "License number is required")
    private String licenseNo;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email ID is required")
    private String companyEmailId;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    private String status;

}
