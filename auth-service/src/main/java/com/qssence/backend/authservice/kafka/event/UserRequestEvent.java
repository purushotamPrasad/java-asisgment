package com.qssence.backend.authservice.kafka.event;

import com.qssence.backend.authservice.dto.request.UserRequest;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestEvent {
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String username;
}
