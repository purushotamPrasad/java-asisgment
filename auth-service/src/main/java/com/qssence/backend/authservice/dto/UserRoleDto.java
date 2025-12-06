package com.qssence.backend.authservice.dto;

import java.util.Set;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleDto {

	private Long userRoleId;
	private String userRoleName;
	private String description;
	private String status; // Status field for ACTIVE/INACTIVE
//	private Set<Long> permissionIds;

	// List of user IDs in this group
	private Set<Long> userIds;

}
