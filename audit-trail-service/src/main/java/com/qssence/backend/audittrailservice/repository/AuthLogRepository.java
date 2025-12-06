package com.qssence.backend.audittrailservice.repository;

import com.qssence.backend.audittrailservice.entity.AuthLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuthLogRepository extends MongoRepository<AuthLog, UUID> {

    List<AuthLog> findByUsername(String username);
    List<AuthLog> findByTimestamp(LocalDate date);

    List<AuthLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AuthLog> findByIpAddress(String ipAddress);

    List<AuthLog> findByStatus(String status);
    List<AuthLog> findByUserId(String userId);

}
