package com.qssence.backend.authservice.dbo;

import com.qssence.backend.authservice.dto.UserRoleMappingDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_role_mapping")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@IdClass(UserRoleMappingDto.class)
public class UserRoleMapping {
    @Id
    private String roleId;

    @Id
    private String userId;
}
