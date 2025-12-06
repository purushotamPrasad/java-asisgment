package com.qssence.backend.audittrailservice.controller;

import com.qssence.backend.audittrailservice.util.APIError;
import com.qssence.backend.audittrailservice.entity.GroupLog;
import com.qssence.backend.audittrailservice.util.SuccessStatus;
import com.qssence.backend.audittrailservice.exception.AuditTrailException;
import com.qssence.backend.audittrailservice.service.implementation.GroupLogService;
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
@RequestMapping("/api/v1/groupLog")
public class GroupLogController {

    private static final Logger logger = LoggerFactory.getLogger(GroupLogController.class);
    @Autowired
    private GroupLogService groupLogService;

    @GetMapping("/getAllGrouplogs")
    public ResponseEntity<?> getAllGroupLogs() {
        try {
            List<GroupLog> groupLogs = groupLogService.getAllGroupLogs();
            List<GroupLog> activeGroupLogs = groupLogs.stream()
                    .filter(groupLog -> groupLog.getStatus().equals("Active"))
                    .collect(Collectors.toList());

            if (!activeGroupLogs.isEmpty()) {
                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved all active group logs");
                successStatus.setData(activeGroupLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No active group logs found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (AuditTrailException e) {
            logger.error("An error occurred while fetching all group logs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getGroupLogsByGroupName/{name}")
    public ResponseEntity<?> getGroupLogsByGroupName(@PathVariable String name) {
        try {
            if (name == null || name.isEmpty()) {
                logger.warn("Group name is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Group name is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<GroupLog> groupLogs = groupLogService.getGroupLogsByName(name);
            if (!groupLogs.isEmpty()) {
                logger.info("Retrieved group logs for group name: " + name);
                SuccessStatus successStatus = new SuccessStatus();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved group logs for group name: " + name);
                successStatus.setData(groupLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No group logs found for group name: " + name);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No group logs found for group name: " + name);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving group logs for group name: " + name, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving group logs for group name: " + name);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/getGroupLogsByDate/{date}")
    public ResponseEntity<?> getGroupLogsByDate(
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

            List<GroupLog> groupLogs = groupLogService.getGroupLogsByTimestamp(date);

            if (!groupLogs.isEmpty()) {
                SuccessStatus<List<GroupLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved group logs for date: " + date);
                successStatus.setData(groupLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No group logs found for date: " + date);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No group logs found for date: " + date);
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
            logger.error("Error occurred while retrieving group logs for date: " + dateString, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getGroupLogsByTimestampRange/{startDate}/{endDate}")
    public ResponseEntity<?> getGroupLogsByTimestampRange(
            @PathVariable("startDate") String startDateString,
            @PathVariable("endDate") String endDateString) {
        try {
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (DateTimeParseException e) {
                logger.warn("Invalid start date format or value: " + startDateString);
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Invalid start date format or value. Please provide the date in dd-MM-yyyy format");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            LocalDate endDate;
            try {
                endDate = LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (DateTimeParseException e) {
                logger.warn("Invalid end date format or value: " + endDateString);
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Invalid end date format or value. Please provide the date in dd-MM-yyyy format");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (startDate == null || endDate == null) {
                logger.warn("Start date or end date is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Both start date and end date are required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            LocalDate currentDate = LocalDate.now();
            if (startDate.isAfter(currentDate) || endDate.isAfter(currentDate)) {
                logger.warn("Start date or end date cannot be in the future");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Start date or end date cannot be in the future");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (startDate.isAfter(endDate)) {
                logger.warn("Start date cannot be after end date");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("Start date cannot be after end date");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            List<GroupLog> groupLogs = groupLogService.getGroupLogsByTimestampRange(startDateTime, endDateTime);

            if (groupLogs.isEmpty()) {
                logger.warn("No logs found for the given date range");
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No logs found for the given date range");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            SuccessStatus<List<GroupLog>> successStatus = new SuccessStatus<>();
            successStatus.setSuccess(true);
            successStatus.setMessage("Retrieved group logs for the given date range");
            successStatus.setData(groupLogs);
            return ResponseEntity.ok(successStatus);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving group logs for timestamp range: " + e.getMessage(), e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving group logs for timestamp range");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getGroupLogsByStatus/{status}")
    public ResponseEntity<?> getGroupLogsByStatus(@PathVariable("status") String status) {
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

            List<GroupLog> groupLogs = groupLogService.getGroupLogsByStatus(status);
            if (!groupLogs.isEmpty()) {
                logger.info("Retrieved group logs for status: " + status);
                SuccessStatus<List<GroupLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved group logs for status: " + status);
                successStatus.setData(groupLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No group logs found for status: " + status);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No group logs found for status: " + status);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving group logs by status: " + status, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving group logs by status: " + status);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getGroupLogsByIpAddress/{ipAddress}")
    public ResponseEntity<?> getGroupLogsByIpAddress(@PathVariable("ipAddress") String ipAddress) {
        try {
            if (ipAddress == null || ipAddress.isEmpty()) {
                logger.warn("IP address is missing");
                APIError error = new APIError();
                error.setError_code(400);
                error.setError_name("BAD_REQUEST");
                error.setError_description("IP address is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            List<GroupLog> groupLogs = groupLogService.getGroupLogsByIpAddress(ipAddress);
            if (!groupLogs.isEmpty()) {
                logger.info("Retrieved group logs for IP address: " + ipAddress);
                SuccessStatus<List<GroupLog>> successStatus = new SuccessStatus<>();
                successStatus.setSuccess(true);
                successStatus.setMessage("Retrieved group logs for IP address: " + ipAddress);
                successStatus.setData(groupLogs);
                return ResponseEntity.ok().body(successStatus);
            } else {
                logger.warn("No group logs found for IP address: " + ipAddress);
                APIError error = new APIError();
                error.setError_code(404);
                error.setError_name("NOT_FOUND");
                error.setError_description("No group logs found for IP address: " + ipAddress);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving group logs by IP address: " + ipAddress, e);
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Error occurred while retrieving group logs by IP address: " + ipAddress);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getGroupLogsByGroupId/{groupId}")
    public ResponseEntity<?> getGroupLogsByGroupId(@PathVariable String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            APIError error = new APIError();
            error.setError_code(400);
            error.setError_name("BAD_REQUEST");
            error.setError_description("Group ID is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        groupId = groupId.trim();

        List<GroupLog> groupLogs = groupLogService.getGroupLogsByGroupId(groupId);

        if (groupLogs.isEmpty()) {
            APIError error = new APIError();
            error.setError_code(204);
            error.setError_name("NO_CONTENT");
            error.setError_description("No group logs found for the group.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
        }

        SuccessStatus successStatus = new SuccessStatus();
        successStatus.setSuccess(true);
        successStatus.setMessage("Group logs retrieved successfully.");
        successStatus.setData(groupLogs);
        return ResponseEntity.status(HttpStatus.OK).body(successStatus);
    }

}
