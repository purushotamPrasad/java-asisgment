package com.qssence.backend.audittrailservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.qssence.backend.audittrailservice.entity.RoleLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoleLogRepository extends MongoRepository<RoleLog, UUID> {
    List<RoleLog> findByName(String name);
    List<RoleLog> findByTimestamp(LocalDate date);
    List<RoleLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<RoleLog> findByStatus(String status);

    List<RoleLog> findByIpAddress(String ipAddress);
    List<RoleLog> findByRoleId(String roleId);
}
