package com.qssence.backend.audittrailservice.service.implementation;

import com.qssence.backend.audittrailservice.entity.UserLog;
import com.qssence.backend.audittrailservice.exception.AuditTrailException;
import com.qssence.backend.audittrailservice.repository.UserLogRepository;
import com.qssence.backend.audittrailservice.service.IUserLogService;
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
public class UserLogService implements IUserLogService {

    private static final Logger logger = LoggerFactory.getLogger(UserLogService.class);
    @Autowired
    private UserLogRepository userLogRepository;
    @Override
    public List<UserLog> getAllUserLogs() {
        try {
            List<UserLog> allUserLogs = userLogRepository.findAll();
            if (allUserLogs.isEmpty()) {
                logger.error("No user logs found");
                throw new AuditTrailException("No user logs found");
            }

            List<UserLog> activeUserLogs = allUserLogs.stream()
                    .filter(userLog -> userLog.getStatus().equals("Active"))
                    .sorted(Comparator.comparing(UserLog::getTimestamp))
                    .collect(Collectors.toList());

            logger.info("Retrieved all active user logs");
            return activeUserLogs;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching all user logs: " + e.getMessage();
            logger.error(errorMessage);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<UserLog> getUserLogsByUsername(String username) {
        try {
            List<UserLog> userLogsByUsername = userLogRepository.findByUsernameContaining(username);

            userLogsByUsername = userLogsByUsername.stream()
                    .filter(log -> log.getUsername() != null && !log.getUsername().isEmpty())
                    .collect(Collectors.toList());

            if (userLogsByUsername.isEmpty()) {
                logger.warn("No valid user logs found for username: {}", username);
                throw new AuditTrailException("No valid user logs found for username: " + username);
            }

            userLogsByUsername.sort(Comparator.comparing(UserLog::getTimestamp));

            logger.info("Retrieved and filtered user logs for username: {}", username);
            return userLogsByUsername;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching user logs by username: " + username;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }



    @Override
    public List<UserLog> getUserLogsByTimestamp(LocalDate date) {
        try {
            List<UserLog> allUserLogs = userLogRepository.findAll();

            List<UserLog> userLogsByDate = allUserLogs.stream()
                    .filter(userLog -> userLog.getTimestamp().toLocalDate().equals(date))
                    .collect(Collectors.toList());

            if (userLogsByDate.isEmpty()) {
                logger.warn("No user logs found for date: " + date);
                throw new AuditTrailException("No user logs found for date: " + date);
            }

            logger.info("Retrieved user logs for date: " + date);
            return userLogsByDate;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching user logs for date: " + date;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<UserLog> getUserLogsByTimestampRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<UserLog> allUserLogs = userLogRepository.findAll();

            List<UserLog> userLogsInRange = allUserLogs.stream()
                    .filter(userLog -> userLog.getTimestamp().isAfter(start) && userLog.getTimestamp().isBefore(end))
                    .collect(Collectors.toList());

            userLogsInRange.sort(Comparator.comparing(UserLog::getTimestamp));

            if (userLogsInRange.isEmpty()) {
                logger.warn("No user logs found for the given timestamp range");
                throw new AuditTrailException("No user logs found for the given timestamp range");
            }

            logger.info("Retrieved and sorted user logs for the given timestamp range");
            return userLogsInRange;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching and sorting user logs for timestamp range: " + e.getMessage();
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<UserLog> searchUserLogsByIpAddress(String ipAddress) {
        try {
            List<UserLog> allUserLogs = userLogRepository.findAll();

            List<UserLog> userLogsByIpAddress = allUserLogs.stream()
                    .filter(userLog -> userLog.getIpAddress().equals(ipAddress))
                    .collect(Collectors.toList());

            if (userLogsByIpAddress.isEmpty()) {
                logger.warn("No user logs found for IP address: " + ipAddress);
                throw new AuditTrailException("No user logs found for IP address: " + ipAddress);
            }

            logger.info("Retrieved user logs for IP address: " + ipAddress);
            return userLogsByIpAddress;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching user logs by IP address: " + ipAddress;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<UserLog> searchUserLogsByStatus(String status) {
        try {
            List<UserLog> allUserLogs = userLogRepository.findAll();

            List<UserLog> userLogsByStatus = allUserLogs.stream()
                    .filter(userLog -> userLog.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());

            if (userLogsByStatus.isEmpty()) {
                logger.warn("No user logs found for status: " + status);
                throw new AuditTrailException("No user logs found for status: " + status);
            }

            logger.info("Retrieved user logs for status: " + status);
            return userLogsByStatus;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching user logs by status: " + status;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<UserLog> getUserLogsByUserId(String userId) {
        try {
            List<UserLog> userLogsByUserId = userLogRepository.findByUserId(userId);

            if (userLogsByUserId.isEmpty()) {
                logger.warn("No user logs found for user ID: " + userId);
                throw new AuditTrailException("No user logs found for user ID: " + userId);
            }

            userLogsByUserId.sort(Comparator.comparing(UserLog::getTimestamp));

            logger.info("Retrieved user logs for user ID: " + userId);
            return userLogsByUserId;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching user logs by user ID: " + userId;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }

}
