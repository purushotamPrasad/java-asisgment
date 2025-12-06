package com.qssence.backend.audittrailservice.service.implementation;

import com.qssence.backend.audittrailservice.entity.RoleLog;
import com.qssence.backend.audittrailservice.exception.AuditTrailException;
import com.qssence.backend.audittrailservice.repository.RoleLogRepository;
import com.qssence.backend.audittrailservice.service.IRoleLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleLogService implements IRoleLogService {
    private static final Logger logger = LoggerFactory.getLogger(RoleLogService.class);
    @Autowired
    private RoleLogRepository roleLogRepository;

    @Override
    public List<RoleLog> getAllRoleLogs() {
        try {
            List<RoleLog> allRoleLogs = roleLogRepository.findAll();
            if (allRoleLogs.isEmpty()) {
                logger.error("No role logs found");
                throw new AuditTrailException("No role logs found");
            }

            List<RoleLog> activeRoleLogs = allRoleLogs.stream()
                    .filter(roleLog -> roleLog.getStatus().equals("Active"))
                    .sorted(Comparator.comparing(RoleLog::getTimestamp))
                    .collect(Collectors.toList());

            logger.info("Retrieved all active role logs");
            return activeRoleLogs;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching all role logs: " + e.getMessage();
            logger.error(errorMessage);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<RoleLog> getRoleLogsByName(String name) {
        try {
            List<RoleLog> roleLogsByName = roleLogRepository.findByName(name);

            if (roleLogsByName.isEmpty()) {
                logger.warn("No role logs found for name: " + name);
                throw new AuditTrailException("No role logs found for name: " + name);
            }

            roleLogsByName = roleLogsByName.stream()
                    .filter(roleLog -> roleLog.getName().equals(name))
                    .collect(Collectors.toList());

            roleLogsByName.sort(Comparator.comparing(RoleLog::getTimestamp));

            logger.info("Retrieved role logs for name: " + name);
            return roleLogsByName;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching role logs by name: " + name;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<RoleLog> getRoleLogsByTimestamp(LocalDate date) {
        try {
            List<RoleLog> allRoleLogs = roleLogRepository.findAll();

            List<RoleLog> roleLogsByDate = allRoleLogs.stream()
                    .filter(roleLog -> roleLog.getTimestamp().toLocalDate().equals(date))
                    .collect(Collectors.toList());

            if (roleLogsByDate.isEmpty()) {
                logger.warn("No role logs found for date: " + date);
                throw new AuditTrailException("No role logs found for date: " + date);
            }

            logger.info("Retrieved role logs for date: " + date);
            return roleLogsByDate;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching role logs for date: " + date;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<RoleLog> getRoleLogsByTimestampRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<RoleLog> allRoleLogs = roleLogRepository.findAll();

            List<RoleLog> roleLogsInRange = allRoleLogs.stream()
                    .filter(roleLog -> roleLog.getTimestamp().isAfter(start) && roleLog.getTimestamp().isBefore(end))
                    .collect(Collectors.toList());

            roleLogsInRange.sort(Comparator.comparing(RoleLog::getTimestamp));

            if (roleLogsInRange.isEmpty()) {
                logger.warn("No role logs found for the given timestamp range");
                throw new AuditTrailException("No role logs found for the given timestamp range");
            }

            logger.info("Retrieved and sorted role logs for the given timestamp range");
            return roleLogsInRange;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching and sorting role logs for timestamp range: " + e.getMessage();
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<RoleLog> getRoleLogsByStatus(String status) {
        try {
            List<RoleLog> allRoleLogs = roleLogRepository.findAll();

            List<RoleLog> roleLogsByStatus = allRoleLogs.stream()
                    .filter(roleLog -> roleLog.getStatus().equalsIgnoreCase(status))
                    .sorted(Comparator.comparing(RoleLog::getTimestamp))
                    .collect(Collectors.toList());

            if (roleLogsByStatus.isEmpty()) {
                logger.warn("No role logs found for status: " + status);
                throw new AuditTrailException("No role logs found for status: " + status);
            }

            logger.info("Retrieved role logs for status: " + status);
            return roleLogsByStatus;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching role logs by status: " + status;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<RoleLog> getRoleLogsByIpAddress(String ipAddress) {
        try {
            List<RoleLog> allRoleLogs = roleLogRepository.findAll();

            List<RoleLog> roleLogsByIpAddress = allRoleLogs.stream()
                    .filter(roleLog -> roleLog.getIpAddress().equals(ipAddress))
                    .collect(Collectors.toList());

            if (roleLogsByIpAddress.isEmpty()) {
                logger.warn("No role logs found for IP address: " + ipAddress);
                throw new AuditTrailException("No role logs found for IP address: " + ipAddress);
            }

            logger.info("Retrieved role logs for IP address: " + ipAddress);
            return roleLogsByIpAddress;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching role logs by IP address: " + ipAddress;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<RoleLog> getRoleLogsByRoleId(String roleId) {
        try {
            List<RoleLog> roleLogsByRoleId = roleLogRepository.findByRoleId(roleId);

            if (roleLogsByRoleId.isEmpty()) {
                logger.warn("No role logs found for role ID: " + roleId);
                throw new AuditTrailException("No role logs found for role ID: " + roleId);
            }

            roleLogsByRoleId.sort(Comparator.comparing(RoleLog::getTimestamp));

            logger.info("Retrieved role logs for role ID: " + roleId);
            return roleLogsByRoleId;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching role logs by role ID: " + roleId;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }
}

