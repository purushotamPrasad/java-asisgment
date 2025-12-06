package com.qssence.backend.audittrailservice.service;

import com.qssence.backend.audittrailservice.entity.UserLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IUserLogService {
    List<UserLog> getAllUserLogs();
    List<UserLog> getUserLogsByUsername(String username);
    List<UserLog> getUserLogsByTimestamp(LocalDate date);
    List<UserLog> getUserLogsByTimestampRange(LocalDateTime start, LocalDateTime end);
    List<UserLog> searchUserLogsByIpAddress(String ipAddress);
    List<UserLog> searchUserLogsByStatus(String status);
    List<UserLog> getUserLogsByUserId(String userId);
}
