package com.qssence.backend.authservice.dbo;

import com.qssence.backend.authservice.dto.RoleGroupMappingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_role_mapping")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@IdClass(RoleGroupMappingId.class)
public class RoleGroupMapping {
    @Id
    private String groupId;

    @Id
    private String roleId;
}
