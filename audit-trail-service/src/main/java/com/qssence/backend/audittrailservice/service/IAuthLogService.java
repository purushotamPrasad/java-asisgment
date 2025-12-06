package com.qssence.backend.audittrailservice.service;

import com.qssence.backend.audittrailservice.entity.AuthLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IAuthLogService {
    List<AuthLog> getAllLoginEvents();
    List<AuthLog> getLoginLogsByUsername(String username);
    List<AuthLog> getLoginLogsByTimestamp(LocalDate date);
    List<AuthLog> getLoginLogsByTimestampRange(LocalDateTime start, LocalDateTime end);
    List<AuthLog> getLoginLogsByIpAddress(String ipAddress);
    List<AuthLog> getLoginLogsByStatus(String status);
    List<AuthLog> getLoginLogsByUserId(String userId);
    List<AuthLog> getFailedLoginAttempts();
    List<AuthLog> getSuccessfulLoginAttempts();
    List<AuthLog> getLogoutEvents();
}

