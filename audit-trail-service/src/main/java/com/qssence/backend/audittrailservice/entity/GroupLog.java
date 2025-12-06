package com.qssence.backend.audittrailservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "group_logs")
public class GroupLog {
    @Id
    private UUID logId;
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String groupId;
    private String name;
}
