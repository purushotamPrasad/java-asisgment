package com.qssence.backend.authservice.dbo;
import com.qssence.backend.authservice.dto.request.PermissionUserMappingRequest;
import jakarta.persistence.Entity;
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
@IdClass(PermissionUserMappingRequest.class)
@Table(name = "PermissionUserMapping")
public class PermissionUserMapping {
    @Id
    private UUID userId;
    @Id
    private UUID permissionId;
}
