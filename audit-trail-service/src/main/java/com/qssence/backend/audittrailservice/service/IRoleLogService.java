package com.qssence.backend.audittrailservice.service;

import com.qssence.backend.audittrailservice.entity.RoleLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IRoleLogService {
    List<RoleLog> getAllRoleLogs();
    List<RoleLog> getRoleLogsByName(String name);
    List<RoleLog> getRoleLogsByTimestamp(LocalDate date);
    List<RoleLog> getRoleLogsByTimestampRange(LocalDateTime start, LocalDateTime end);
    List<RoleLog> getRoleLogsByStatus(String status);
    List<RoleLog> getRoleLogsByIpAddress(String ipAddress);
    List<RoleLog> getRoleLogsByRoleId(String roleId);
}
