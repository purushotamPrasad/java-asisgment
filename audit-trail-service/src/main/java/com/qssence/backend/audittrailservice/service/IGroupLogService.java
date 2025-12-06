package com.qssence.backend.audittrailservice.service;

import com.qssence.backend.audittrailservice.entity.GroupLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IGroupLogService {
    List<GroupLog> getAllGroupLogs();
    List<GroupLog> getGroupLogsByName(String name);
    List<GroupLog> getGroupLogsByTimestamp(LocalDate date);
    List<GroupLog> getGroupLogsByTimestampRange(LocalDateTime start, LocalDateTime end);
    List<GroupLog> getGroupLogsByStatus(String status);
    List<GroupLog> getGroupLogsByIpAddress(String ipAddress);
    List<GroupLog> getGroupLogsByGroupId(String groupId);

}
