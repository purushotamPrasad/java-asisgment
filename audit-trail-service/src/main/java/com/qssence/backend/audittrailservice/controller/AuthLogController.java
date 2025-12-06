package com.qssence.backend.audittrailservice.controller;

import com.qssence.backend.audittrailservice.entity.AuthLog;
import com.qssence.backend.audittrailservice.util.APIError;
import com.qssence.backend.audittrailservice.exception.AuditTrailException;
import com.qssence.backend.audittrailservice.service.implementation.AuthLogService;
import com.qssence.backend.audittrailservice.util.SuccessStatus;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/authLog")
public class AuthLogController {

    private static final Logger logger = LoggerFactory.getLogger(AuthLogController.class);

    @Autowired
    private AuthLogService authLogService;

    @GetMapping("/getAllLoginLogs")
    public ResponseEntity<?> getAllAuthLogs() {
        try {
            List<AuthLog> authLogs = authLogService.getAllLoginEvents();
            List<AuthLog> activeAuthLogs = authLogs.stream()
                    .filter(authLog -> authLog.getStatus().equals("Active"))
                    .collect(Collectors.toList());

            if (!activeAuthLogs.isEmpty()) {
                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved all active login logs");
                successStatus.setData(activeAuthLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No active login events found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (AuditTrailException e) {
            logger.error("An error occurred while fetching all login events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getLoginLogsByUsername/{username}")
    public ResponseEntity<?> getLoginLogsByUsername(@PathVariable String username) {
        try {
            if (username == null || username.isEmpty()) {
                logger.warn("Username is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Username is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (!username.matches("^[a-zA-Z0-9_@]+$")) {
                logger.warn("Invalid username format: " + username);
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Username should be alphanumeric with optional underscore and @ symbol");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<AuthLog> authLogs = authLogService.getLoginLogsByUsername(username);
            if (!authLogs.isEmpty()) {
                logger.info("Retrieved login logs for username: " + username);
                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved login logs for username: " + username);
                successStatus.setData(authLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No login logs found for username: " + username);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No login logs found for username: " + username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving login logs for username: " + username, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving login logs for username: " + username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getLoginLogsByDate/{date}")
    public ResponseEntity<?> getLoginLogsByDate(
            @PathVariable("date") String dateString) {
        try {
            if (dateString == null) {
                logger.warn("Date is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Date is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (!dateString.matches("\\d{2}-\\d{2}-\\d{4}")) {
                logger.warn("Invalid date format: " + dateString);
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Invalid date format. Please provide date in 'dd-MM-yyyy' format.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(dateString, formatter);

            LocalDate currentDate = LocalDate.now();
            if (date.isAfter(currentDate)) {
                logger.warn("Date entered is in the future: " + date);
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Date cannot be in the future");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<AuthLog> authLogs = authLogService.getLoginLogsByTimestamp(date);

            if (!authLogs.isEmpty()) {
                SuccessStatus<List<AuthLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved login logs for date: " + date);
                successStatus.setData(authLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No login logs found for date: " + date);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No login logs found for date: " + date);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format: " + e.getMessage());
            APIError error = new APIError();
            error.setError_code(400);
            error.setError_name("BAD_REQUEST");
            error.setError_description("Invalid date format. Please provide date in 'dd-MM-yyyy' format.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving login logs for date: " + dateString, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving login logs for date: " + dateString);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getLoginLogsByTimestampRange/{startDate}/{endDate}")
    public ResponseEntity<?> getAuthLogsByTimestampRange(
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate) {
        try {
            if (startDate == null || endDate == null) {
                logger.warn("Start date or end date is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Both start date and end date are required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate.parse(startDate, formatter);
                LocalDate.parse(endDate, formatter);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format provided");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Invalid date format provided. Please use the format dd-MM-yyyy");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            LocalDate currentDate = LocalDate.now();
            if (start.isAfter(currentDate) || end.isAfter(currentDate)) {
                logger.warn("Start date or end date cannot be in the future");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Start date or end date cannot be in the future");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (start.isAfter(end)) {
                logger.warn("Start date cannot be after end date");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Start date cannot be after end date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<AuthLog> authLogs = authLogService.getLoginLogsByTimestampRange(start.atStartOfDay(), end.plusDays(1).atStartOfDay().minusSeconds(1));
            if (authLogs.isEmpty()) {
                logger.warn("No logs found for the given date range");
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No logs found for the given date range");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            SuccessStatus<List<AuthLog>> successStatus = new SuccessStatus<>();
            successStatus.setSuccess(true);
            successStatus.setMessage("Retrieved login logs for the date range");
            successStatus.setData(authLogs);
            return ResponseEntity.ok(successStatus);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving login logs for timestamp range: " + e.getMessage(), e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving login logs for timestamp range");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getLoginLogsByIpAddress/{ipAddress}")
    public ResponseEntity<?> getAuthLogsByIpAddress(@PathVariable("ipAddress") String ipAddress) {
        try {
            if (ipAddress == null || ipAddress.isEmpty()) {
                logger.warn("IP address is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("IP address is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<AuthLog> authLogs = authLogService.getLoginLogsByIpAddress(ipAddress);
            if (!authLogs.isEmpty()) {
                logger.info("Retrieved login logs for IP address: " + ipAddress);
                SuccessStatus<List<AuthLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved login logs for IP address: " + ipAddress);
                successStatus.setData(authLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No login logs found for IP address: " + ipAddress);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No login logs found for IP address: " + ipAddress);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving login logs by IP address: " + ipAddress, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving login logs by IP address: " + ipAddress);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getLoginLogsByStatus/{status}")
    public ResponseEntity<?> getAuthLogsByStatus(@PathVariable("status") String status) {
        try {
            if (status == null || status.isEmpty()) {
                logger.warn("Status is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Status is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (!status.equalsIgnoreCase("success") && !status.equalsIgnoreCase("failure")) {
                logger.warn("Invalid status: " + status);
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Invalid status. Status should be either 'success' or 'failure'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<AuthLog> authLogs = authLogService.getLoginLogsByStatus(status);
            if (!authLogs.isEmpty()) {
                logger.info("Retrieved login logs for status: " + status);
                SuccessStatus<List<AuthLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved login logs for status: " + status);
                successStatus.setData(authLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No login logs found for status: " + status);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No login logs found for status: " + status);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving login logs by status: " + status, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving login logs by status: " + status);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getLoginLogsByUserId/{userId}")
    public ResponseEntity<?> getLoginLogsByUserId(@PathVariable String userId) {
        if (StringUtils.isEmpty(userId)) {
            APIError error = new APIError();
            error.setError_code(400);
            error.setError_name("BAD_REQUEST");
            error.setError_description("User ID is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        userId = userId.trim();

        List<AuthLog> authLogs = authLogService.getLoginLogsByUserId(userId);

        if (authLogs.isEmpty()) {
            APIError error = new APIError();
            error.setError_code(204);
            error.setError_name("NO_CONTENT");
            error.setError_description("No login logs found for the user.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
        }

        SuccessStatus successStatus = new SuccessStatus();
        successStatus.setSuccess(true);
        successStatus.setMessage("Login logs retrieved successfully.");
        successStatus.setData(authLogs);
        return ResponseEntity.status(HttpStatus.OK).body(successStatus);
    }

    @GetMapping("/getFailedLoginAttempts")
    public ResponseEntity<?> getFailedLoginAttempts() {
        try {
            List<AuthLog> authLogs = authLogService.getAllLoginEvents();

            List<AuthLog> failedLoginAttempts = authLogs.stream()
                    .filter(authLog -> authLog.getStatus().equals("Failed"))
                    .collect(Collectors.toList());

            if (failedLoginAttempts.isEmpty()) {
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No failed login attempts found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            SuccessStatus successStatus = new SuccessStatus();
            successStatus.setSuccess(true);
            successStatus.setMessage("Failed login attempts retrieved successfully.");
            successStatus.setData(failedLoginAttempts);
            return ResponseEntity.ok().body(successStatus);
        } catch (AuditTrailException e) {
            logger.error("An error occurred while fetching failed login attempts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

        @GetMapping("/getSuccessfulLoginAttempts")
        public ResponseEntity<?> getSuccessfulLoginAttempts() {
            try {
                List<AuthLog> authLogs = authLogService.getAllLoginEvents();

                List<AuthLog> successfulLoginAttempts = authLogs.stream()
                        .filter(authLog -> authLog.getStatus().equals("Success"))
                        .collect(Collectors.toList());

                if (successfulLoginAttempts.isEmpty()) {
                    APIError error = new APIError();
                    error.setError_code(404);
                    error.setError_name("NOT_FOUND");
                    error.setError_description("No successful login attempts found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                }

                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Successful login attempts retrieved successfully.");
                successStatus.setData(successfulLoginAttempts);
                return ResponseEntity.ok().body(successStatus);
            } catch (AuditTrailException e) {
                logger.error("An error occurred while fetching successful login attempts", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        @GetMapping("/getLogoutEvents")
        public ResponseEntity<?> getLogoutEvents() {
            try {
                List<AuthLog> authLogs = authLogService.getAllLoginEvents();

                List<AuthLog> logoutEvents = authLogs.stream()
                        .filter(authLog -> "Inactive".equals(authLog.getStatus()))
                        .collect(Collectors.toList());

                if (logoutEvents.isEmpty()) {
                    APIError error = new APIError();
                    error.setError_code(404);
                    error.setError_name("NOT_FOUND");
                    error.setError_description("No logout events found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                }

                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Logout events retrieved successfully.");
                successStatus.setData(logoutEvents);
                return ResponseEntity.ok().body(successStatus);
            } catch (AuditTrailException e) {
                logger.error("An error occurred while fetching logout events", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
}
