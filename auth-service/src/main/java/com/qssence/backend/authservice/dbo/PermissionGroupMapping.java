package com.qssence.backend.authservice.dbo;

import com.qssence.backend.authservice.dto.request.PermissionGroupMappingRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(PermissionGroupMappingRequest.class)
@Table(name = "PermissionGroupMapping")
public class PermissionGroupMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID permissionId;
    @Id
    private UUID groupId;
    
}
