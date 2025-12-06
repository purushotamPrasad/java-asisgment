package com.qssence.backend.audittrailservice.service.implementation;

import com.qssence.backend.audittrailservice.entity.GroupLog;
import com.qssence.backend.audittrailservice.entity.RoleLog;
import com.qssence.backend.audittrailservice.exception.AuditTrailException;
import com.qssence.backend.audittrailservice.repository.GroupLogRepository;
import com.qssence.backend.audittrailservice.service.IGroupLogService;
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
public class GroupLogService implements IGroupLogService {

    private static final Logger logger = LoggerFactory.getLogger(GroupLogService.class);

    @Autowired
    private GroupLogRepository groupLogRepository;

    @Override
    public List<GroupLog> getAllGroupLogs() {
        try {
            List<GroupLog> allGroupLogs = groupLogRepository.findAll();
            if (allGroupLogs.isEmpty()) {
                logger.error("No group logs found");
                throw new AuditTrailException("No group logs found");
            }

            List<GroupLog> activeGroupLogs = allGroupLogs.stream()
                    .filter(groupLog -> groupLog.getStatus().equals("Active"))
                    .sorted(Comparator.comparing(GroupLog::getTimestamp))
                    .collect(Collectors.toList());

            logger.info("Retrieved all active group logs");
            return activeGroupLogs;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching all group logs: " + e.getMessage();
            logger.error(errorMessage);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<GroupLog> getGroupLogsByName(String name) {
        try {
            List<GroupLog> groupLogs = groupLogRepository.findByName(name);

            groupLogs = groupLogs.stream()
                    .filter(log -> log.getName() != null && !log.getName().isEmpty())
                    .collect(Collectors.toList());

            if (groupLogs.isEmpty()) {
                logger.warn("No valid group logs found for name: {}", name);
                throw new AuditTrailException("No valid group logs found for name: " + name);
            }

            groupLogs.sort(Comparator.comparing(GroupLog::getTimestamp));

            logger.info("Retrieved and filtered group logs for name: {}", name);
            return groupLogs;
        } catch (Exception e) {
            logger.error("An error occurred while fetching group logs by name: {}", name, e);
            throw new AuditTrailException("Failed to fetch group logs by name");
        }
    }


    @Override
    public List<GroupLog> getGroupLogsByTimestamp(LocalDate date) {
        try {
            List<GroupLog> allGroupLogs = groupLogRepository.findAll();

            List<GroupLog> groupLogsByDate = allGroupLogs.stream()
                    .filter(groupLog -> groupLog.getTimestamp().toLocalDate().equals(date))
                    .collect(Collectors.toList());

            if (groupLogsByDate.isEmpty()) {
                logger.warn("No group logs found for date: " + date);
                throw new AuditTrailException("No group logs found for date: " + date);
            }

            logger.info("Retrieved group logs for date: " + date);
            return groupLogsByDate;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching group logs for date: " + date;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<GroupLog> getGroupLogsByTimestampRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<GroupLog> allGroupLogs = groupLogRepository.findAll();

            List<GroupLog> groupLogsInRange = allGroupLogs.stream()
                    .filter(groupLog -> groupLog.getTimestamp().isAfter(start) && groupLog.getTimestamp().isBefore(end))
                    .collect(Collectors.toList());

            groupLogsInRange.sort(Comparator.comparing(GroupLog::getTimestamp));

            if (groupLogsInRange.isEmpty()) {
                logger.warn("No group logs found for the given timestamp range");
                throw new AuditTrailException("No group logs found for the given timestamp range");
            }

            logger.info("Retrieved and sorted group logs for the given timestamp range");
            return groupLogsInRange;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching and sorting group logs for timestamp range: " + e.getMessage();
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<GroupLog> getGroupLogsByStatus(String status) {
        try {
            List<GroupLog> allGroupLogs = groupLogRepository.findAll();

            List<GroupLog> groupLogsByStatus = allGroupLogs.stream()
                    .filter(groupLog -> groupLog.getStatus().equalsIgnoreCase(status))
                    .sorted(Comparator.comparing(GroupLog::getTimestamp))
                    .collect(Collectors.toList());

            if (groupLogsByStatus.isEmpty()) {
                logger.warn("No group logs found for status: " + status);
                throw new AuditTrailException("No group logs found for status: " + status);
            }

            logger.info("Retrieved group logs for status: " + status);
            return groupLogsByStatus;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching group logs by status: " + status;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<GroupLog> getGroupLogsByIpAddress(String ipAddress) {
        try {
            List<GroupLog> allGroupLogs = groupLogRepository.findAll();

            List<GroupLog> groupLogsByIpAddress = allGroupLogs.stream()
                    .filter(groupLog -> groupLog.getIpAddress().equals(ipAddress))
                    .collect(Collectors.toList());

            if (groupLogsByIpAddress.isEmpty()) {
                logger.warn("No group logs found for IP address: " + ipAddress);
                throw new AuditTrailException("No group logs found for IP address: " + ipAddress);
            }

            logger.info("Retrieved group logs for IP address: " + ipAddress);
            return groupLogsByIpAddress;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching group logs by IP address: " + ipAddress;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<GroupLog> getGroupLogsByGroupId(String groupId) {
        try {
            List<GroupLog> groupLogsByGroupId = groupLogRepository.findByGroupId(groupId);

            if (groupLogsByGroupId.isEmpty()) {
                logger.warn("No group logs found for group ID: " + groupId);
                throw new AuditTrailException("No group logs found for group ID: " + groupId);
            }

            groupLogsByGroupId.sort(Comparator.comparing(GroupLog::getTimestamp));

            logger.info("Retrieved group logs for group ID: " + groupId);
            return groupLogsByGroupId;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching group logs by group ID: " + groupId;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }
}