//package com.qssence.backend.authservice.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.qssence.backend.authservice.dto.APIError;
//import com.qssence.backend.authservice.dto.ApiResponse;
//import com.qssence.backend.authservice.dto.request.GroupIdRequest;
//import com.qssence.backend.authservice.dto.request.GroupsRequest;
//import com.qssence.backend.authservice.kafka.event.GroupRequestEvent;
//import com.qssence.backend.authservice.kafka.producer.GroupProducer;
//import com.qssence.backend.authservice.service.IGroupsService;
//import com.qssence.backend.authservice.service.implementation.RoleGroupMappingService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import jakarta.ws.rs.NotFoundException;
//import org.keycloak.representations.idm.GroupRepresentation;
//import org.keycloak.representations.idm.RoleRepresentation;
//import org.keycloak.representations.idm.UserRepresentation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//import java.util.Objects;
//import java.net.URI;
//
//
//@RestController
//@RequestMapping("/api/v1/group")
//public class GroupsController {
//
//    private static final Logger logger = LoggerFactory.getLogger(GroupsController.class);
//
//    @Autowired
//    IGroupsService groupsService;
//    @Autowired
//    private RoleGroupMappingService roleGroupMappingService;
//    @Autowired
//    private GroupProducer groupProducer;
//
//    @PostMapping("/createGroup")
//    @ResponseStatus(HttpStatus.CREATED)
//
//    public ResponseEntity<?> createGroup(@RequestBody @Valid GroupsRequest groupsRequest, BindingResult bindingResult, HttpServletRequest request) {
//        if (bindingResult.hasErrors()) {
//            return handleErrorResponse(buildValidationError(bindingResult), HttpStatus.BAD_REQUEST);
//        }
//        int response = groupsService.createGroup(groupsRequest).getStatus();
//        if (response == HttpStatus.CREATED.value()) {
//            ApiResponse apiResponse = new ApiResponse();
//            apiResponse.setSuccess(true);
//            apiResponse.setMessage("Group created successfully");
//            apiResponse.setData(groupsService.getAllGroups());
//            GroupRequestEvent successEvent = new GroupRequestEvent("Group Created", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), groupsRequest.getGroupsId(),groupsRequest.getName());
//            groupProducer.sendGroupEvent(successEvent);
//            return ResponseEntity.created(URI.create("/api/v1/group/createGroup")).body(apiResponse);
//        } else if (response == HttpStatus.CONFLICT.value()) {
//            GroupRequestEvent conflictEvent = new GroupRequestEvent("Group Creation Failed - Conflict", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), groupsRequest.getGroupsId(),groupsRequest.getName());
//            groupProducer.sendGroupEvent(conflictEvent);
//            return handleErrorResponse(buildConflictError(), HttpStatus.CONFLICT);
//        } else {
//            GroupRequestEvent badRequestEvent = new GroupRequestEvent("Group Creation Failed - Bad Request", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), groupsRequest.getGroupsId(),groupsRequest.getName());
//            groupProducer.sendGroupEvent(badRequestEvent);
//            return handleErrorResponse(buildBadRequestError(), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    private ResponseEntity<APIError> handleErrorResponse(APIError error, HttpStatus status) {
//        if (status == HttpStatus.CONFLICT) {
//            error.setError_description("Group already exists.");
//        } else {
//            error.setError_description(Objects.requireNonNullElse(error.getError_description(), status.getReasonPhrase()));
//        }
//        return ResponseEntity.status(status).body(error);
//    }
//    private APIError buildValidationError(BindingResult bindingResult) {
//        APIError error = new APIError();
//        error.setError_code(4000);
//        error.setError_name("VALIDATION_ERROR");
//
//        Map<String, String> errorMap = new HashMap<>();
//        bindingResult.getFieldErrors().forEach(fieldError -> {
//            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
//        });
//
//        try {
//            String errorDescription = new ObjectMapper().writeValueAsString(errorMap);
//            error.setError_description(errorDescription);
//        } catch (JsonProcessingException e) {
//            // Handle JSON processing exception
//            e.printStackTrace();
//        }
//
//        return error;
//    }
//
//    private APIError buildConflictError() {
//        APIError error = new APIError();
//        error.setError_code(4009);
//        error.setError_description("Group already exists.");
//        error.setError_name("ALREADY_EXIST");
//        return error;
//    }
//
//    private APIError buildBadRequestError() {
//        APIError error = new APIError();
//        error.setError_code(4000);
//        error.setError_name("INVALID_REQUEST");
//        error.setError_description("Invalid request");
//        return error;
//    }
//    private APIError buildNotFoundError() {
//        APIError error = new APIError();
//        error.setError_code(4040);
//        error.setError_name("NOT_FOUND");
//        error.setError_description("Group Not Found");
//        return error;
//    }
//    private APIError buildInternalServerError() {
//        APIError error = new APIError();
//        error.setError_code(500);
//        error.setError_name("INTERNAL_SERVER_ERROR");
//        error.setError_description("Internal server error occurred.");
//        return error;
//    }
//
//    @GetMapping("/getAllGroups")
//    public ResponseEntity<ApiResponse<List<GroupRepresentation>>> getAllGroups(HttpServletRequest request) {
//        ApiResponse<List<GroupRepresentation>> response = new ApiResponse<>();
//        try {
//            logger.info("Retrieving all groups...");
//            List<GroupRepresentation> groups = groupsService.getAllGroups();
//            logger.info("All groups retrieved successfully.");
//            response.setSuccess(true);
//            response.setMessage("All groups retrieved successfully.");
//            response.setData(groups);
//            GroupRequestEvent successEvent = new GroupRequestEvent("All Groups Retrieved", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), UUID.randomUUID(), "");
//            groupProducer.sendGroupEvent(successEvent);
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        } catch (Exception e) {
//            logger.error("Error occurred while retrieving all groups: " + e.getMessage(), e);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve all groups: " + e.getMessage());
//            GroupRequestEvent errorEvent = new GroupRequestEvent("Error Retrieving All Groups", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), UUID.randomUUID(), "");
//            groupProducer.sendGroupEvent(errorEvent);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @GetMapping("/getGroupById/{groupId}")
//    public ResponseEntity<?> getGroupsById(@PathVariable("groupId") UUID groupsId, HttpServletRequest request) {
//        ApiResponse<GroupRepresentation> response = new ApiResponse<>();
//        try {
//            logger.info("Retrieving group by ID: " + groupsId);
//            GroupRepresentation group = groupsService.getGroupById(groupsId);
//            if(group != null){
//                logger.info("Group retrieved successfully.");
//                response.setSuccess(true);
//                response.setMessage("Group retrieved successfully.");
//                response.setData(group);
//                GroupRequestEvent successEvent = new GroupRequestEvent("Group Retrieved by ID", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), groupsId, null);
//                groupProducer.sendGroupEvent(successEvent);
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            }
//            else{
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent("Group Not Found by ID", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), groupsId, null);
//                groupProducer.sendGroupEvent(notFoundEvent);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//
//        }
//        catch (NotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//        }
//        catch (Exception e) {
//            logger.error("Error occurred while retrieving group by ID: " + e.getMessage(), e);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve group by ID: " + e.getMessage());
//            GroupRequestEvent errorEvent = new GroupRequestEvent("Error Retrieving Group by ID", "ERROR", LocalDateTime.now(), request.getRemoteAddr(), groupsId,null);
//            groupProducer.sendGroupEvent(errorEvent);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @GetMapping("/getGroupByUserId/{userId}")
//    public ResponseEntity<?> getGroupsByUserId(@PathVariable String userId, HttpServletRequest request) {
//        ApiResponse<List<GroupRepresentation>> response = new ApiResponse<>();
//        List<GroupRepresentation> groupRepresentation = groupsService.getGroupsByUserId(userId);
//        if (groupRepresentation.isEmpty()) {
//            response.setSuccess(false);
//            response.setMessage("No groups found for user with ID: " + userId);
//            GroupRequestEvent notFoundEvent = new GroupRequestEvent("No Groups Found by User ID", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, null );
//            groupProducer.sendGroupEvent(notFoundEvent);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//        } else {
//            response.setSuccess(true);
//            response.setMessage("Groups retrieved successfully.");
//            response.setData(groupRepresentation);
//            GroupRequestEvent successEvent = new GroupRequestEvent("Groups Retrieved by User ID", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, null);
//            groupProducer.sendGroupEvent(successEvent);
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        }
//    }
//
//
//    @PostMapping("/addUsersToGroupByGroupId/{groupId}")
//    public ResponseEntity<?> addUsersToGroupByGroupId(@PathVariable String groupId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
//        try {
//            List<String> userIds = requestBody.get("userIds");
//            List<UserRepresentation> group = groupsService.addUsersToGroupByGroupId(groupId, userIds);
//            if(group != null){
//                UUID uuid = UUID.fromString(groupId);
//                ApiResponse<List<UserRepresentation>> response = new ApiResponse<>();
//                List<UserRepresentation> groupRepresentation = groupsService.getAllGroupMembersByGroupId(uuid);
//                response.setSuccess(true);
//                response.setData(groupRepresentation);
//                response.setMessage("Users added to the group successfully");
//                GroupRequestEvent successEvent = new GroupRequestEvent("Users Added to Group by Group ID", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null,null);
//                groupProducer.sendGroupEvent(successEvent);
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            }else{
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent("Group Not Found by Group ID", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null,null);
//                groupProducer.sendGroupEvent(notFoundEvent);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//
//        }
//        catch (NotFoundException e) {
//            GroupRequestEvent notFoundEvent = new GroupRequestEvent("Group Not Found by Group ID", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, null);
//            groupProducer.sendGroupEvent(notFoundEvent);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//        }catch(IllegalArgumentException e) {
//            GroupRequestEvent badRequestEvent = new GroupRequestEvent("Invalid Argument", "BAD_REQUEST", LocalDateTime.now(), request.getRemoteAddr(), null, null);
//            groupProducer.sendGroupEvent(badRequestEvent);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBadRequestError());
//        } catch (Exception e) {
//            GroupRequestEvent errorEvent = new GroupRequestEvent("Error Adding Users to Group by Group ID", "ERROR", LocalDateTime.now(), request.getRemoteAddr(), null,null);
//            groupProducer.sendGroupEvent(errorEvent);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
//        }
//    }
//
//    @PostMapping("/addGroupsToUserByUserId/{userId}")
//    public ResponseEntity<?> addGroupsToUserByUserId(@PathVariable String userId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
//        try {
//            List<String> groupIds = requestBody.get("groupIds");
//            List<GroupRepresentation> userGroups = groupsService.addGroupsToUserByUserId(userId, groupIds);
//            if (userGroups != null) {
//                GroupRequestEvent event = new GroupRequestEvent(
//                        "User added to groups",
//                        "SUCCESS",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        "GroupAddition for User: " + userId
//                );
//                groupProducer.sendGroupEvent(event);
//                ApiResponse<List<GroupRepresentation>> response = new ApiResponse<>();
//                response.setSuccess(true);
//                response.setData(userGroups);
//                response.setMessage("User added to the groups successfully");
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            } else {
//                GroupRequestEvent groupEvent = new GroupRequestEvent(
//                        "User or groups not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        "GroupAddition for User: " + userId
//                );
//                groupProducer.sendGroupEvent(groupEvent);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//        } catch (NotFoundException e) {
//            GroupRequestEvent groupEvent = new GroupRequestEvent(
//                    "User or groups not found",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "GroupAddition for User: " + userId
//            );
//            groupProducer.sendGroupEvent(groupEvent);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//        } catch (IllegalArgumentException e) {
//            GroupRequestEvent groupEvent = new GroupRequestEvent(
//                    "Bad request",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "GroupAddition for User: " + userId
//            );
//            groupProducer.sendGroupEvent(groupEvent);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBadRequestError());
//        } catch (Exception e) {
//            GroupRequestEvent groupEvent = new GroupRequestEvent(
//                    "Internal server error",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "GroupAddition for User: " + userId
//            );
//            groupProducer.sendGroupEvent(groupEvent);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
//        }
//    }
//
//    @DeleteMapping("/removeUsersFromGroupByGroupId/{groupId}")
//    public ResponseEntity<?> removeUsersFromGroupByGroupId(@PathVariable String groupId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
//        try {
//            List<String> userIds = requestBody.get("userIds");
//            boolean removedSuccessfully = groupsService.removeUsersFromGroupByGroupId(groupId, userIds);
//            if (removedSuccessfully) {
//                UUID uuid = UUID.fromString(groupId);
//                ApiResponse<List<UserRepresentation>> response = new ApiResponse<>();
//                List<UserRepresentation> groupRepresentation = groupsService.getAllGroupMembersByGroupId(uuid);
//                GroupRequestEvent successEvent = new GroupRequestEvent(
//                        "Users removed from group",
//                        "SUCCESS",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        uuid,
//                        "GroupRemoval for Group: " + groupId
//                );
//                groupProducer.sendGroupEvent(successEvent);
//
//                response.setSuccess(true);
//                response.setData(groupRepresentation);
//                response.setMessage("Users removed from the group successfully");
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            } else {
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "Users or group not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        "GroupRemoval for Group: " + groupId
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//        } catch (NotFoundException e) {
//            GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                    "Users or group not found",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "GroupRemoval for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(notFoundEvent);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//        } catch (IllegalArgumentException e) {
//            GroupRequestEvent badRequestEvent = new GroupRequestEvent(
//                    "Bad request",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "GroupRemoval for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(badRequestEvent);
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBadRequestError());
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Internal server error",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "GroupRemoval for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
//        }
//    }
//
//    @DeleteMapping("/removeGroupsFromUserByUserId/{userId}")
//    public ResponseEntity<?> removeGroupsFromUserByUserId(@PathVariable String userId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
//        try {
//            List<String> groupIds = requestBody.get("groupIds");
//            boolean removedSuccessfully = groupsService.removeGroupsFromUserByUserId(userId, groupIds);
//            if (removedSuccessfully) {
//                ApiResponse<List<GroupRepresentation>> response = new ApiResponse<>();
//                List<GroupRepresentation> userGroups = groupsService.getGroupsByUserId(userId);
//                GroupRequestEvent successEvent = new GroupRequestEvent(
//                        "Groups removed from user",
//                        "SUCCESS",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        "Removed user from group: " + userId
//                );
//                groupProducer.sendGroupEvent(successEvent);
//                response.setSuccess(true);
//                response.setData(userGroups);
//                response.setMessage("Groups removed from the user successfully");
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            } else {
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "User or groups not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        "Removed user from group: " + userId
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//        } catch (NotFoundException e) {
//            GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                    "User or groups not found",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "Removed user from group: " + userId
//            );
//            groupProducer.sendGroupEvent(notFoundEvent);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//        } catch (IllegalArgumentException e) {
//            GroupRequestEvent badRequestEvent = new GroupRequestEvent(
//                    "Bad request",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "Removed user from group: " + userId
//            );
//            groupProducer.sendGroupEvent(badRequestEvent);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBadRequestError());
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Internal server error",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "Removed user from group: " + userId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
//        }
//    }
//    @GetMapping("/getGroupByName/{name}")
//    public ResponseEntity<?> getGroupsByName(@PathVariable("name") String name, HttpServletRequest request) {
//        ApiResponse<GroupRepresentation> response = new ApiResponse<>();
//        try {
//            logger.info("Retrieving group by name: " + name);
//            GroupRepresentation group = groupsService.getGroupsByName(name);
//            if(group != null){
//                logger.info("Group retrieved successfully.");
//                response.setSuccess(true);
//                response.setMessage("Group retrieved successfully.");
//                response.setData(group);
//                GroupRequestEvent successEvent = new GroupRequestEvent(
//                        "Group retrieved successfully",
//                        "SUCCESS",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        name
//                );
//                groupProducer.sendGroupEvent(successEvent);
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            }
//            else{
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "Group not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        name
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//        } catch (Exception e) {
//            logger.error("Error occurred while retrieving group by name: " + e.getMessage(), e);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve group by name: " + e.getMessage());
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Internal server error",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    name
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @DeleteMapping("/deleteGroupById/{groupId}")
//    public ResponseEntity<?> deleteGroupsById(@PathVariable("groupId") UUID groupsId, HttpServletRequest request) {
//        ApiResponse<List<GroupRepresentation>> response = new ApiResponse<>();
//        try {
//            logger.info("Deleting group by ID: " + groupsId);
//            GroupRepresentation existingGroup = groupsService.getGroupById(groupsId);
//            if (existingGroup == null) {
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "Group not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        groupsId,
//                        "GroupDeletion for Group: " + groupsId
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//
//                APIError error = new APIError();
//                error.setError_code(4040);
//                error.setError_name("NOT_FOUND");
//                error.setError_description("Group not found.");
//
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//            }
//            groupsService.deleteGroupsById(groupsId);
//            logger.info("Group deleted successfully.");
//
//            List<GroupRepresentation> remainingGroups = groupsService.getAllGroups();
//            GroupRequestEvent successEvent = new GroupRequestEvent(
//                    "Group deleted successfully",
//                    "SUCCESS",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    groupsId,
//                    "GroupDeletion for Group: " + groupsId
//            );
//            groupProducer.sendGroupEvent(successEvent);
//
//            response.setSuccess(true);
//            response.setMessage("Group deleted successfully.");
//            response.setData(remainingGroups);
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        } catch (Exception e) {
//            logger.error("Error occurred while deleting group by ID: " + e.getMessage(), e);
//            response.setSuccess(false);
//            response.setMessage("Failed to delete group by ID: " + e.getMessage());
//            response.setData(new ArrayList<>());
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Internal server error",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    groupsId,
//                    "GroupDeletion for Group: " + groupsId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @DeleteMapping("/deleteGroupByName/{name}")
//    public ResponseEntity<?> deleteGroupsByName(@PathVariable("name") String name, HttpServletRequest request) {
//        ApiResponse<List<GroupRepresentation>> response = new ApiResponse<>();
//        try {
//            GroupRepresentation existingGroup = groupsService.getGroupsByName(name);
//            if (existingGroup == null) {
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "Group not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        name
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//                APIError error = new APIError();
//                error.setError_code(4040);
//                error.setError_name("NOT_FOUND");
//                error.setError_description("Group with name " + name + " not found.");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//            }
//
//            boolean isDeleted = groupsService.deleteGroupsByName(name);
//            if (isDeleted) {
//                logger.info("Group with name {} deleted successfully.", name);
//                GroupRequestEvent successEvent = new GroupRequestEvent(
//                        "Group deleted successfully",
//                        "SUCCESS",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        name
//                );
//                groupProducer.sendGroupEvent(successEvent);
//                List<GroupRepresentation> remainingGroups = groupsService.getAllGroups();
//                response.setSuccess(true);
//                response.setMessage("Group with name " + name + " deleted successfully.");
//                response.setData(remainingGroups);
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            } else {
//                GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                        "Failed to delete group",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        name
//                );
//                groupProducer.sendGroupEvent(internalErrorEvent);
//                APIError error = new APIError();
//                error.setError_code(5000);
//                error.setError_name("INTERNAL_SERVER_ERROR");
//                error.setError_description("Failed to delete group with name: " + name);
//
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//            }
//        } catch (Exception e) {
//            logger.error("Error occurred while deleting group by name: {}", e.getMessage(), e);
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to delete group",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    name
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//            APIError error = new APIError();
//            error.setError_code(5000);
//            error.setError_name("INTERNAL_SERVER_ERROR");
//            error.setError_description("Failed to delete group by name: " + e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    @PutMapping("/updateGroupById/{groupId}")
//    public ResponseEntity<?> updateGroupsById(@PathVariable("groupId") UUID groupId,
//                                              @RequestBody @Valid GroupsRequest groupsRequest, BindingResult bindingResult, HttpServletRequest request) {
//        if (bindingResult.hasErrors()) {
//            return handleErrorResponse(buildValidationError(bindingResult), HttpStatus.BAD_REQUEST);
//        }
//        try {
//            GroupRepresentation existingGroup = groupsService.getGroupById(groupId);
//            if (existingGroup == null) {
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "Group not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        groupId,
//                        "GroupUpdate for Group: " + groupId
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//                APIError error = new APIError();
//                error.setError_code(4040);
//                error.setError_name("NOT_FOUND");
//                error.setError_description("Group not found.");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//            }
//            GroupRepresentation groupWithSameName = groupsService.getGroupsByName(groupsRequest.getName());
//            if (groupWithSameName != null && !groupWithSameName.getId().equals(existingGroup.getId())) {
//                GroupRequestEvent conflictEvent = new GroupRequestEvent(
//                        "Group with the same name already exists",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        groupId,
//                        "GroupUpdate for Group: " + groupId
//                );
//                groupProducer.sendGroupEvent(conflictEvent);
//                APIError error = new APIError();
//                error.setError_code(4090);
//                error.setError_name("ALREADY_EXIST");
//                error.setError_description("Group with the same name already exists.");
//                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
//            }
//            GroupRepresentation updatedGroup = groupsService.updateGroupsById(groupId, groupsRequest);
//            GroupRequestEvent successEvent = new GroupRequestEvent(
//                    "Group updated successfully",
//                    "SUCCESS",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    groupId,
//                    "GroupUpdate for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(successEvent);
//            ApiResponse<GroupRepresentation> response = new ApiResponse<>();
//            response.setSuccess(true);
//            response.setMessage("Group updated successfully.");
//            response.setData(updatedGroup);
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to update group",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    "GroupUpdate for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//            APIError error = new APIError();
//            error.setError_code(5000);
//            error.setError_name("INTERNAL_SERVER_ERROR");
//            error.setError_description("Failed to update group: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    @PutMapping("/updateGroupByName/{name}")
//    public ResponseEntity<?> updateGroupByName(@PathVariable("name") String name,
//                                               @RequestBody @Valid GroupsRequest groupsRequest, BindingResult bindingResult, HttpServletRequest request) {
//        if (bindingResult.hasErrors()) {
//            return handleErrorResponse(buildValidationError(bindingResult), HttpStatus.BAD_REQUEST);
//        }
//        try {
//            GroupRepresentation existingGroup = groupsService.getGroupsByName(name);
//            GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                    "Group not found",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    name
//            );
//            groupProducer.sendGroupEvent(notFoundEvent);
//
//            if (existingGroup == null) {
//                APIError error = new APIError();
//                error.setError_code(4040);
//                error.setError_name("NOT_FOUND");
//                error.setError_description("Group not found.");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//            }
//
//            GroupRepresentation groupWithSameName = groupsService.getGroupsByName(groupsRequest.getName());
//            if (groupWithSameName != null && !groupWithSameName.getId().equals(existingGroup.getId())) {
//                GroupRequestEvent conflictEvent = new GroupRequestEvent(
//                        "Group with the same name already exists",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        UUID.randomUUID(),
//                        name
//                );
//                groupProducer.sendGroupEvent(conflictEvent);
//                APIError error = new APIError();
//                error.setError_code(4090);
//                error.setError_name("ALREADY_EXIST");
//                error.setError_description("Group with the same name already exists.");
//                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
//            }
//
//            GroupRepresentation updatedGroup = groupsService.updateGroupsByName(name, groupsRequest);
//            GroupRequestEvent successEvent = new GroupRequestEvent(
//                    "Group updated successfully",
//                    "SUCCESS",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    name
//            );
//            groupProducer.sendGroupEvent(successEvent);
//            ApiResponse<GroupRepresentation> response = new ApiResponse<>();
//            response.setSuccess(true);
//            response.setMessage("Group updated successfully.");
//            response.setData(updatedGroup);
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to update group",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.randomUUID(),
//                    name
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//            APIError error = new APIError();
//            error.setError_code(5000);
//            error.setError_name("INTERNAL_SERVER_ERROR");
//            error.setError_description("Failed to update group: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    @PostMapping("/assignRolesToGroup/{groupId}")
//    public ResponseEntity<?> assignRolesToGroup(@PathVariable String groupId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
//        List<String> roleIds = requestBody.get("roleIds");
//        if (roleIds == null || roleIds.isEmpty()) {
//            return ResponseEntity.badRequest().body("No role IDs provided.");
//        }
//        try {
//            for (String roleId : roleIds) {
//                roleGroupMappingService.assignRoleToGroup(groupId, roleId);
//            }
//
//            GroupRequestEvent successEvent = new GroupRequestEvent(
//                    "Roles assigned successfully to Group",
//                    "SUCCESS",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.fromString(groupId),
//                    "RoleAssignment for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(successEvent);
//
//            UUID groupUUID = UUID.fromString(groupId);
//            List<RoleRepresentation> roles = groupsService.getAllRolesByGroupId(groupUUID);
//            ApiResponse<List<RoleRepresentation>> response = new ApiResponse<>();
//            response.setSuccess(true);
//            response.setMessage("Roles assigned successfully to Group.");
//            response.setData(roles);
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to assign roles to Group",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.fromString(groupId),
//                    "RoleAssignment for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to assign roles to Group.");
//        }
//    }
//
//    @DeleteMapping("/removeRolesFromGroup/{groupId}")
//    public ResponseEntity<?> removeRolesFromGroup(@PathVariable String groupId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
//        List<String> roleIds = requestBody.get("roleIds");
//        if (roleIds == null || roleIds.isEmpty()) {
//            return ResponseEntity.badRequest().body("No role IDs provided.");
//        }
//        try {
//            for (String roleId : roleIds) {
//                roleGroupMappingService.removeRoleFromGroup(groupId, roleId);
//            }
//
//            GroupRequestEvent successEvent = new GroupRequestEvent(
//                    "Roles removed successfully from Group",
//                    "SUCCESS",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.fromString(groupId),
//                    "RoleRemovalFromGroup for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(successEvent);
//
//            UUID groupUUID = UUID.fromString(groupId);
//            List<RoleRepresentation> roles = groupsService.getAllRolesByGroupId(groupUUID);
//            ApiResponse<List<RoleRepresentation>> response = new ApiResponse<>();
//            response.setSuccess(true);
//            response.setMessage("Roles removed successfully from Group.");
//            response.setData(roles);
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to remove roles from Group",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    UUID.fromString(groupId),
//                    "RoleRemovalFromGroup for Group: " + groupId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove roles from Group.");
//        }
//    }
//
//    @GetMapping("/getallgroupmembersbygroupid/{groupId}")
//    public ResponseEntity<?> getAllGroupMembersByGroupsId(@PathVariable("groupId") UUID groupsId, HttpServletRequest request) {
//        ApiResponse<List<UserRepresentation>> response = new ApiResponse<>();
//        try {
//            logger.info("Retrieving all group members by groups ID: {}", groupsId);
//            List<UserRepresentation> groupMembers = groupsService.getAllGroupMembersByGroupId(groupsId);
//            if(groupMembers != null){
//                logger.info("All group members retrieved successfully.");
//                GroupRequestEvent successEvent = new GroupRequestEvent(
//                        "All group members retrieved successfully",
//                        "SUCCESS",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        groupsId,
//                        "GroupMembersRetrieval for Group: " + groupsId
//                );
//                groupProducer.sendGroupEvent(successEvent);
//                response.setSuccess(true);
//                response.setMessage("All group members retrieved successfully.");
//                response.setData(groupMembers);
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            }else{
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "Group members not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        groupsId,
//                        "GroupMembersRetrieval for Group: " + groupsId
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to retrieve all group members by group ID",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    groupsId,
//                    "GroupMembersRetrieval for Group: " + groupsId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//            logger.error("Error occurred while retrieving all group members by group ID: {}", e.getMessage(), e);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve all group members by group ID: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @GetMapping("/getAllRolesByGroupId/{groupId}")
//    public ResponseEntity<?> getAllRolesByGroupId(@PathVariable("groupId") UUID groupsId, HttpServletRequest request) {
//        ApiResponse<List<RoleRepresentation>> response = new ApiResponse<>();
//        try {
//            logger.info("Retrieving all roles for group with ID {}", groupsId);
//            List<RoleRepresentation> roles = groupsService.getAllRolesByGroupId(groupsId);
//            if(roles != null){
//                logger.info("All roles for group retrieved successfully.");
//                GroupRequestEvent successEvent = new GroupRequestEvent(
//                        "All roles for group retrieved successfully",
//                        "SUCCESS",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        groupsId,
//                        "RoleRetrieval for Group: " + groupsId
//                );
//                groupProducer.sendGroupEvent(successEvent);
//
//                response.setSuccess(true);
//                response.setMessage("All roles for group retrieved successfully.");
//                response.setData(roles);
//                return ResponseEntity.status(HttpStatus.OK).body(response);
//            }
//            else{
//                GroupRequestEvent notFoundEvent = new GroupRequestEvent(
//                        "Roles for group not found",
//                        "FAILURE",
//                        LocalDateTime.now(),
//                        request.getRemoteAddr(),
//                        groupsId,
//                        "RoleRetrieval for Group: " + groupsId
//                );
//                groupProducer.sendGroupEvent(notFoundEvent);
//
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
//            }
//
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to retrieve all roles for group",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    groupsId,
//                    "RoleRetrieval for Group: " + groupsId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//            logger.error("Error occurred while retrieving all roles for group: {}", e.getMessage(), e);
//            response.setSuccess(false);
//            response.setMessage("Failed to retrieve all roles for group: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    @DeleteMapping("/deleteUserFromGroupByUserId/{userId}")
//    public ResponseEntity<?> deleteUserFromGroupByUserId(@PathVariable String userId, @RequestBody GroupIdRequest groupIdRequest, HttpServletRequest request) {
//        try {
//            groupsService.deleteUserFromGroupByUserId(userId, groupIdRequest.getGroupId());
//            GroupRequestEvent successEvent = new GroupRequestEvent(
//                    "User deleted from group successfully",
//                    "SUCCESS",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    null,
//                    "UserDeletionFromGroup for User: " + userId
//            );
//            groupProducer.sendGroupEvent(successEvent);
//            ApiResponse apiResponse = new ApiResponse();
//            apiResponse.setSuccess(true);
//            apiResponse.setMessage("User deleted from group successfully");
//            return ResponseEntity.ok().body(apiResponse);
//        } catch (Exception e) {
//            GroupRequestEvent internalErrorEvent = new GroupRequestEvent(
//                    "Failed to delete user from group",
//                    "FAILURE",
//                    LocalDateTime.now(),
//                    request.getRemoteAddr(),
//                    null,
//                    "UserDeletionFromGroup for User: " + userId
//            );
//            groupProducer.sendGroupEvent(internalErrorEvent);
//
//            APIError error = new APIError();
//            error.setError_code(5000);
//            error.setError_name("DELETE_FAILED");
//            error.setError_description("Failed to delete user from group");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//}
//
