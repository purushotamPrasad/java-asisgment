package com.qssence.backend.audittrailservice.repository;

import com.qssence.backend.audittrailservice.entity.UserLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserLogRepository extends MongoRepository<UserLog, UUID> {
    List<UserLog> findByUsernameContaining(String username);
    List<UserLog> findByTimestamp(LocalDate date);
    List<UserLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<UserLog> findByStatus(String status);

    List<UserLog> findByIpAddress(String ipAddress);
    List<UserLog> findByUserId(String userId);
}


