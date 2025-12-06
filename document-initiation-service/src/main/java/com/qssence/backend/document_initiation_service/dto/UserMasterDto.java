package com.qssence.backend.document_initiation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.time.ZoneId;
import java.util.Date;

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
    private Long userMobileNumber;
    private String userEmailId;
    private String userAddress;
    private String region;
    private String country;
    private Long userPlantId;
    private Long userDepartmentId;
    private Long userSectionId;
    private Set<Long> groupIds;
    private Set<Long> roleIds;
    private String employeeId;
    private String status;
    private ZoneId timeZone;
    private Date createdAt;
    private String password;
    private String location;
    private String designation;
} 