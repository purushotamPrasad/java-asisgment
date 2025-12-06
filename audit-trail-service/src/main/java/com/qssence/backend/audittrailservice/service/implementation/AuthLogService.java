package com.qssence.backend.audittrailservice.service.implementation;

import com.qssence.backend.audittrailservice.entity.AuthLog;
import com.qssence.backend.audittrailservice.exception.AuditTrailException;
import com.qssence.backend.audittrailservice.repository.AuthLogRepository;
import com.qssence.backend.audittrailservice.service.IAuthLogService;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthLogService implements IAuthLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuthLogService.class);

    @Autowired
    private AuthLogRepository authLogRepository;

    @Override
    public List<AuthLog> getAllLoginEvents() {
        try {
            List<AuthLog> allAuthLogs = authLogRepository.findAll();
            if (allAuthLogs.isEmpty()) {
                logger.error("No login events found");
                throw new AuditTrailException("No login events found");
            }

            List<AuthLog> activeAuthLogs = allAuthLogs.stream()
                    .filter(authLog -> authLog.getStatus().equals("Active"))
                    .sorted(Comparator.comparing(AuthLog::getTimestamp))
                    .collect(Collectors.toList());

            logger.info("Retrieved all active login events");
            return activeAuthLogs;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching all login events: " + e.getMessage();
            logger.error(errorMessage);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<AuthLog> getLoginLogsByUsername(String username) {
        try {
            List<AuthLog> authLogsByUsername = authLogRepository.findByUsername(username);

            authLogsByUsername = authLogsByUsername.stream()
                    .filter(log -> log.getUsername() != null && !log.getUsername().isEmpty())
                    .collect(Collectors.toList());

            if (authLogsByUsername.isEmpty()) {
                logger.warn("No valid login events found for username: {}", username);
                throw new AuditTrailException("No valid login events found for username: " + username);
            }

            Collections.sort(authLogsByUsername, Comparator.comparing(AuthLog::getTimestamp));

            logger.info("Retrieved and filtered login logs for username: {}", username);
            return authLogsByUsername;
        } catch (Exception e) {
            logger.error("An error occurred while fetching login events by username: {}", username, e);
            throw new AuditTrailException("Failed to fetch login events by username");
        }
    }

    @Override
    public List<AuthLog> getLoginLogsByTimestamp(LocalDate date) {
        try {
            List<AuthLog> allLoginEvents = authLogRepository.findAll();

            List<AuthLog> loginLogsByDate = allLoginEvents.stream()
                    .filter(authLog -> authLog.getTimestamp().toLocalDate().equals(date))
                    .collect(Collectors.toList());

            if (loginLogsByDate.isEmpty()) {
                logger.warn("No login logs found for date: " + date);
                throw new AuditTrailException("No login logs found for date: " + date);
            }

            logger.info("Retrieved login logs for date: " + date);
            return loginLogsByDate;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching login logs for date: " + date;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<AuthLog> getLoginLogsByTimestampRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<AuthLog> allLoginLogs = authLogRepository.findAll();

            List<AuthLog> loginLogsInRange = allLoginLogs.stream()
                    .filter(authLog -> authLog.getTimestamp().isAfter(start) && authLog.getTimestamp().isBefore(end))
                    .collect(Collectors.toList());

            loginLogsInRange.sort(Comparator.comparing(AuthLog::getTimestamp));

            if (loginLogsInRange.isEmpty()) {
                logger.warn("No login logs found for the given timestamp range");
                throw new AuditTrailException("No login logs found for the given timestamp range");
            }

            logger.info("Retrieved and sorted login logs for the given timestamp range");
            return loginLogsInRange;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching and sorting login logs for timestamp range: " + e.getMessage();
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<AuthLog> getLoginLogsByIpAddress(String ipAddress) {
        try {
            List<AuthLog> allLoginLogs = authLogRepository.findAll();

            List<AuthLog> loginLogsByIpAddress = allLoginLogs.stream()
                    .filter(authLog -> authLog.getIpAddress().equals(ipAddress))
                    .collect(Collectors.toList());

            if (loginLogsByIpAddress.isEmpty()) {
                logger.warn("No login logs found for IP address: " + ipAddress);
                throw new AuditTrailException("No login logs found for IP address: " + ipAddress);
            }

            logger.info("Retrieved login logs for IP address: " + ipAddress);
            return loginLogsByIpAddress;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching login logs by IP address: " + ipAddress;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<AuthLog> getLoginLogsByStatus(String status) {
        try {
            List<AuthLog> allAuthLogs = authLogRepository.findAll();

            List<AuthLog> loginLogsByStatus = allAuthLogs.stream()
                    .filter(authLog -> authLog.getStatus().equalsIgnoreCase(status))
                    .sorted(Comparator.comparing(AuthLog::getTimestamp))
                    .collect(Collectors.toList());

            if (loginLogsByStatus.isEmpty()) {
                logger.warn("No login logs found for status: " + status);
                throw new AuditTrailException("No login logs found for status: " + status);
            }

            logger.info("Retrieved login logs for status: " + status);
            return loginLogsByStatus;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching login logs by status: " + status;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<AuthLog> getLoginLogsByUserId(String userId) {
        try {
            List<AuthLog> authLogsByUserId = authLogRepository.findByUserId(userId);

            if (authLogsByUserId.isEmpty()) {
                logger.warn("No login logs found for user ID: " + userId);
                throw new AuditTrailException("No login logs found for user ID: " + userId);
            }

            authLogsByUserId.sort(Comparator.comparing(AuthLog::getTimestamp));

            logger.info("Retrieved login logs for user ID: " + userId);
            return authLogsByUserId;
        } catch (Exception e) {
            String errorMessage = "Error occurred while fetching login logs by user ID: " + userId;
            logger.error(errorMessage, e);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<AuthLog> getFailedLoginAttempts() throws AuditTrailException {
        try {
            List<AuthLog> failedLogs = authLogRepository.findByStatus("Failed");

            if (failedLogs.isEmpty()) {
                logger.error("No failed login attempts found");
                throw new AuditTrailException("No failed login attempts found");
            }

            List<AuthLog> sortedFailedLogs = failedLogs.stream()
                    .filter(authLog -> authLog.getStatus().equals("Failed"))
                    .sorted(Comparator.comparing(AuthLog::getTimestamp))
                    .collect(Collectors.toList());

            logger.info("Retrieved all failed login attempts");
            return sortedFailedLogs;
        } catch (Exception e) {
            String errorMessage = "Error occurred while retrieving failed login attempts: " + e.getMessage();
            logger.error(errorMessage);
            throw new AuditTrailException(errorMessage);
        }
    }

    @Override
    public List<AuthLog> getSuccessfulLoginAttempts() throws AuditTrailException {
        try {
            List<AuthLog> successLogs = authLogRepository.findByStatus("Success");

            if (successLogs.isEmpty()) {
                logger.error("No successful login attempts found");
                throw new AuditTrailException("No successful login attempts found");
            }

            List<AuthLog> activeSuccessLogs = successLogs.stream()
                    .filter(authLog -> authLog.getStatus().equals("Success"))
                    .sorted(Comparator.comparing(AuthLog::getTimestamp))
                    .collect(Collectors.toList());

            logger.info("Retrieved all successful login attempts");
            return activeSuccessLogs;
        } catch (Exception e) {
            String errorMessage = "Error occurred while retrieving successful login attempts: " + e.getMessage();
            logger.error(errorMessage);
            throw new AuditTrailException(errorMessage);
        }
    }


    @Override
    public List<AuthLog> getLogoutEvents() throws AuditTrailException {
        try {
            List<AuthLog> logoutEvents = authLogRepository.findByStatus("Logout");

            if (logoutEvents.isEmpty()) {
                logger.error("No logout events found");
                throw new AuditTrailException("No logout events found");
            }

            List<AuthLog> activeLogoutEvents = logoutEvents.stream()
                    .filter(authLog -> authLog.getStatus().equals("Logout"))
                    .sorted(Comparator.comparing(AuthLog::getTimestamp))
                    .collect(Collectors.toList());

            logger.info("Retrieved all logout events");
            return activeLogoutEvents;
        } catch (Exception e) {
            String errorMessage = "Error occurred while retrieving logout events: " + e.getMessage();
            logger.error(errorMessage);
            throw new AuditTrailException(errorMessage);
        }
    }

}