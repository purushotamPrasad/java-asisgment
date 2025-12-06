package com.qssence.backend.authservice.kafka.event;

import com.qssence.backend.authservice.dto.request.RoleIdRequest;
import com.qssence.backend.authservice.dto.request.RoleRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestEvent {
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private String ipAddress;
    private UUID rolesId;
    private String name;
}
