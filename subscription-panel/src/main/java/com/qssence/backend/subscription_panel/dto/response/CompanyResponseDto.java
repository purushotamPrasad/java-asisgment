package com.qssence.backend.subscription_panel.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyResponseDto {

    private String companyId;
    private String companyName;
    private String companyPrefix;
    private String location;
    private String region;
    private String country;
    private String timezone;
    private String phoneNo;
    private String licenseNo;
    private String companyEmailId;
    private LocalDateTime createdAt;
    private String password;
    private String status;

}
