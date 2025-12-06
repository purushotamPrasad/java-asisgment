package com.qssence.backend.authservice.dto;

import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import com.qssence.backend.authservice.dbo.UserMaster;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMasterDto {

    private Long userId;
    private String userFirstName;
    private String userMiddleName;
    private String userLastName;
    private String profileImageUrl;
    private String userMobileNumber;   // ✅ Long → String
    private String userEmailId;
    private String userAddress;
    private String region;
    private String country;
    private Long userPlantId;
    private Long userDepartmentId;
    private Long userSectionId;
    private Set<Long> groupIds;  // Set of group IDs (foreign keys)
    private Set<Long> roleIds;   // Set of role IDs (foreign keys)
    private String employeeId;
    private String status;
    private ZoneId timeZone;
    private Date createdAt;   // ✅ or LocalDateTime/OffsetDateTime
    private String location;
    private String designation;
    private String keycloakUserId;  // ✅ renamed for consistency
}
