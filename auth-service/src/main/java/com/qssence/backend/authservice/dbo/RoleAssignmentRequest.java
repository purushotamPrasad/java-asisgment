package com.qssence.backend.authservice.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "role_assignment_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleAssignmentRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMaster user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private UserRole role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    
    private String requestedBy;
    private String approvedBy;
    private String rejectionReason;
    
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime expiresAt;
    
    private String justification;
    private String businessCase;
    
    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        EXPIRED
    }
}
