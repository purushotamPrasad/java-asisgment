package com.qssence.backend.authservice.dto.responce;

import com.qssence.backend.authservice.dto.UserMasterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponseDto {

    private Long groupsId;
    private String name;
    private String description;

    // List of user IDs in this group
    private Set<Long> userIds;
    private Set<UserMasterDto> users; // ✅ This must exist
    
    // Enhanced: Role information
    private Set<Long> roleIds;
    private Set<String> roleNames;
    
    // Enhanced: Permission information
    private Set<Long> permissionIds;
    private Set<String> permissionNames;
    
    // Enhanced: Group metadata
    private String createdBy;
    private String status;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private String updatedBy;
    
    // Enhanced: Group statistics
    private int userCount;
    private int roleCount;
    private int permissionCount;

}

