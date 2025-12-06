package com.qssence.backend.authservice.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_permission_mappings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupPermissionMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "permission_id")
//    private Permission.Permission permission;
    
    @Enumerated(EnumType.STRING)
    private PermissionAccessLevel accessLevel;
    
    private String grantedBy;
    private java.time.LocalDateTime grantedAt;
    private java.time.LocalDateTime expiresAt;
    
    public enum PermissionAccessLevel {
        READ,
        WRITE,
        DELETE,
        ADMIN
    }
}
