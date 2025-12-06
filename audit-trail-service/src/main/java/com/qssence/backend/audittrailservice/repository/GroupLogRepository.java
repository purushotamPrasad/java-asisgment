package com.qssence.backend.audittrailservice.repository;

import com.qssence.backend.audittrailservice.entity.GroupLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GroupLogRepository extends MongoRepository<GroupLog, UUID> {
    List<GroupLog> findByName(String name);
    List<GroupLog> findByTimestamp(LocalDate date);
    List<GroupLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<GroupLog> findByStatus(String status);

    List<GroupLog> findByIpAddress(String ipAddress);
    List<GroupLog> findByGroupId(String groupId);
}
