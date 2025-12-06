package com.qssence.backend.authservice.mapper;

import com.qssence.backend.authservice.dbo.Group;
import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dbo.UserRole;
import com.qssence.backend.authservice.dto.UserMasterDto;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Builder
public class UserMapper {

    public UserMasterDto toDto(UserMaster user) {
        if (user == null) return null;

        Set<Long> groupIds = user.getGroups() != null
                ? user.getGroups().stream().map(Group::getGroupsId).collect(Collectors.toSet())
                : null;

        return UserMasterDto.builder()
                .userId(user.getUserId())
                .userFirstName(user.getFirstName())
                .userMiddleName(user.getMiddleName())
                .userLastName(user.getLastName())
                .profileImageUrl(user.getProfileImageUrl())
                .userMobileNumber(user.getMobileNumber())
                .userEmailId(user.getEmailId())
                .userAddress(user.getAddress())
                .region(user.getRegion())
                .country(user.getCountry())
                .userPlantId(user.getPlant() != null ? user.getPlant().getId() : null)
                .userDepartmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .userSectionId(user.getSection() != null ? user.getSection().getId() : null)
                .groupIds(groupIds)
                .employeeId(user.getEmployeeId())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .timeZone(user.getTimeZone())
                .createdAt(user.getCreatedAt())
//                .password(user.getPassword())
                .location(user.getLocation())
                .designation(user.getDesignation())
                .build();
    }


}
