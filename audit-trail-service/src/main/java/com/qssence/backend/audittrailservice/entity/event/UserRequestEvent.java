package com.qssence.backend.audittrailservice.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestEvent {
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String username;
}
