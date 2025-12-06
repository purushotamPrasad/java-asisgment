package com.qssence.backend.audittrailservice.controller;

import com.qssence.backend.audittrailservice.util.APIError;
import com.qssence.backend.audittrailservice.entity.RoleLog;
import com.qssence.backend.audittrailservice.util.SuccessStatus;
import com.qssence.backend.audittrailservice.exception.AuditTrailException;
import com.qssence.backend.audittrailservice.service.implementation.RoleLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roleLog")
public class RoleLogController {

    private static final Logger logger = LoggerFactory.getLogger(RoleLogController.class);
    @Autowired
    private RoleLogService roleLogService;

    @GetMapping("/getAllRolelogs")
    public ResponseEntity<?> getAllRoleLogs() {
        try {
            List<RoleLog> roleLogs = roleLogService.getAllRoleLogs();
            List<RoleLog> activeRoleLogs = roleLogs.stream()
                    .filter(roleLog -> roleLog.getStatus().equals("Active"))
                    .collect(Collectors.toList());

            if (!activeRoleLogs.isEmpty()) {
                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved all active role logs");
                successStatus.setData(activeRoleLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No active role logs found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (AuditTrailException e) {
            logger.error("An error occurred while fetching all role logs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getRoleLogsByRoleName/{name}")
    public ResponseEntity<?> getRoleLogsByRoleName(@PathVariable String name) {
        try {
            if (name == null || name.isEmpty()) {
                logger.warn("Role name is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Role name is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<RoleLog> roleLogs = roleLogService.getRoleLogsByName(name);
            if (!roleLogs.isEmpty()) {
                logger.info("Retrieved role logs for role name: " + name);
                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved role logs for role name: " + name);
                successStatus.setData(roleLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No role logs found for role name: " + name);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No role logs found for role name: " + name);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving role logs for role name: " + name, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving role logs for role name: " + name);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/getRoleLogsByDate/{date}")
    public ResponseEntity<?> getRoleLogsByDate(
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

            List<RoleLog> roleLogs = roleLogService.getRoleLogsByTimestamp(date);

            if (!roleLogs.isEmpty()) {
                SuccessStatus<List<RoleLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved role logs for date: " + date);
                successStatus.setData(roleLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No role logs found for date: " + date);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No role logs found for date: " + date);
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
            logger.error("Error occurred while retrieving role logs for date: " + dateString, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/getRoleLogsByTimestampRange/{startDate}/{endDate}")
    public ResponseEntity<?> getRoleLogsByTimestampRange(
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
            LocalDate start = null;
            LocalDate end = null;
            try {
                start = LocalDate.parse(startDate, formatter);
                end = LocalDate.parse(endDate, formatter);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format provided");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Invalid date format provided. Please use the format dd-MM-yyyy");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

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

            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
            List<RoleLog> roleLogs = roleLogService.getRoleLogsByTimestampRange(startDateTime, endDateTime);

            if (roleLogs.isEmpty()) {
                logger.warn("No logs found for the given date range");
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No logs found for the given date range");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            SuccessStatus<List<RoleLog>> successStatus = new SuccessStatus<>();
            successStatus.setSuccess(true);
            successStatus.setMessage("Retrieved role logs for the given date range");
            successStatus.setData(roleLogs);
            return ResponseEntity.ok(successStatus);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving role logs for timestamp range: " + e.getMessage(), e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving role logs for timestamp range");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getRoleLogsByStatus/{status}")
    public ResponseEntity<?> getRoleLogsByStatus(@PathVariable("status") String status) {
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

            List<RoleLog> roleLogs = roleLogService.getRoleLogsByStatus(status);
            if (!roleLogs.isEmpty()) {
                logger.info("Retrieved role logs for status: " + status);
                SuccessStatus<List<RoleLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved role logs for status: " + status);
                successStatus.setData(roleLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No role logs found for status: " + status);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No role logs found for status: " + status);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving role logs by status: " + status, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving role logs by status: " + status);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getRoleLogsByIpAddress/{ipAddress}")
    public ResponseEntity<?> getRoleLogsByIpAddress(@PathVariable("ipAddress") String ipAddress) {
        try {
            if (ipAddress == null || ipAddress.isEmpty()) {
                logger.warn("IP address is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("IP address is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<RoleLog> roleLogs = roleLogService.getRoleLogsByIpAddress(ipAddress);
            if (!roleLogs.isEmpty()) {
                logger.info("Retrieved role logs for IP address: " + ipAddress);
                SuccessStatus<List<RoleLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved role logs for IP address: " + ipAddress);
                successStatus.setData(roleLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No role logs found for IP address: " + ipAddress);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No role logs found for IP address: " + ipAddress);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving role logs by IP address: " + ipAddress, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving role logs by IP address: " + ipAddress);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/getRoleLogsByRoleId/{roleId}")
    public ResponseEntity<?> getRoleLogsByRoleId(@PathVariable String roleId) {
        if (StringUtils.isEmpty(roleId)) {
            APIError error = new APIError();
            error.setError_code(400);
            error.setError_name("BAD_REQUEST");
            error.setError_description("Role ID is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        roleId = roleId.trim();

        List<RoleLog> roleLogs = roleLogService.getRoleLogsByRoleId(roleId);

        if (roleLogs.isEmpty()) {
            APIError error = new APIError();
            error.setError_code(204);
            error.setError_name("NO_CONTENT");
            error.setError_description("No role logs found for the role.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
        }

        SuccessStatus successStatus = new SuccessStatus();
        successStatus.setSuccess(true);
        successStatus.setMessage("Role logs retrieved successfully.");
        successStatus.setData(roleLogs);
        return ResponseEntity.status(HttpStatus.OK).body(successStatus);
    }
}
