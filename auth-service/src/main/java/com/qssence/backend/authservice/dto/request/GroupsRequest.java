package com.qssence.backend.authservice.dto.request;

import com.qssence.backend.authservice.dto.UserMasterDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupsRequest {
    @NotBlank(message = "Group name is required")
    @Size(max = 60, message = "Group name must be at most 60 characters long")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Group name must contain only alphabets and spaces")
    private String name;
    private String description;
    private List<Long> userIds;
    private Set<UserMasterDto> users;
    
    // Enhanced: Role assignment to groups
    private List<Long> roleIds;
    
//    // Enhanced: Permission assignment to groups
//    private List<Long> permissionIds;
    
    // Enhanced: Group metadata
    private String createdBy;
    private String status;
}


