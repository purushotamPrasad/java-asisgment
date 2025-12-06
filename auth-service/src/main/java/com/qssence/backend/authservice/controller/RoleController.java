package com.qssence.backend.authservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.authservice.dto.APIError;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.RoleIdRequest;
import com.qssence.backend.authservice.dto.request.RoleRequest;
import com.qssence.backend.authservice.kafka.event.RoleRequestEvent;
import com.qssence.backend.authservice.kafka.producer.RoleProducer;
import com.qssence.backend.authservice.service.implementation.RoleService;
import com.qssence.backend.authservice.service.implementation.UserRoleMappingService;
import com.qssence.backend.authservice.service.implementation.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    @Autowired
    UserService userService;
    
    @Autowired
    RoleService roleService;
    
    @Autowired
    RoleProducer roleProducer;
    
    @Autowired
    private UserRoleMappingService userRoleMappingService;
    
    @PostMapping("/createRole")
    public ResponseEntity<?> createRole(@RequestBody @Valid RoleRequest roleRequest, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return handleErrorResponse(buildValidationError(bindingResult), HttpStatus.BAD_REQUEST);
        }
        int response = roleService.createRole(roleRequest).getStatus();
        if (response == HttpStatus.CREATED.value()) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Role created successfully");
            apiResponse.setData(roleService.getAllRoles());
            RoleRequestEvent roleEvent = new RoleRequestEvent("Role Created", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), roleRequest.getRolesId(), roleRequest.getName());
            roleProducer.sendRoleEvent(roleEvent);
            return ResponseEntity.created(URI.create("/api/v1/role/createRole")).body(apiResponse);
        } else if (response == HttpStatus.CONFLICT.value()) {
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Creation Failed", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), roleRequest.getRolesId(), roleRequest.getName());
            roleProducer.sendRoleEvent(failureEvent);
            return handleErrorResponse(buildConflictError(), HttpStatus.CONFLICT);
        } else {
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Creation Failed", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), roleRequest.getRolesId(), roleRequest.getName());
            roleProducer.sendRoleEvent(failureEvent);
            return handleErrorResponse(buildBadRequestError(), HttpStatus.BAD_REQUEST);
        }
    }


    private ResponseEntity<APIError> handleErrorResponse(APIError error, HttpStatus status) {
        if (status == HttpStatus.CONFLICT) {
            error.setError_description("Role already exists.");
        } else {
            error.setError_description(Objects.requireNonNullElse(error.getError_description(), status.getReasonPhrase()));
        }
        return ResponseEntity.status(status).body(error);
    }
    private APIError buildValidationError(BindingResult bindingResult) {
        APIError error = new APIError();
        error.setError_code(4000);
        error.setError_name("VALIDATION_ERROR");

        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        try {
            String errorDescription = new ObjectMapper().writeValueAsString(errorMap);
            error.setError_description(errorDescription);
        } catch (JsonProcessingException e) {
            // Handle JSON processing exception
            e.printStackTrace();
        }

        return error;
    }

    private APIError buildConflictError() {
        APIError error = new APIError();
        error.setError_code(4009);
        error.setError_description("Role already exists.");
        error.setError_name("ALREADY_EXIST");
        return error;
    }
    private APIError buildBadRequestError() {
        APIError error = new APIError();
        error.setError_code(4000);
        error.setError_name("INVALID_REQUEST");
        error.setError_description("Invalid request");
        return error;
    }
    private APIError buildNotFoundError() {
        APIError error = new APIError();
        error.setError_code(4040);
        error.setError_name("NOT_FOUND");
        error.setError_description("Role Not Found");
        return error;
    }
    private APIError buildInternalServerError() {
        APIError error = new APIError();
        error.setError_code(500);
        error.setError_name("INTERNAL_SERVER_ERROR");
        error.setError_description("Internal server error occurred.");
        return error;
    }

    @GetMapping("/getAllRoles")
    public ResponseEntity<ApiResponse<List<RoleRepresentation>>> getAllRoles(HttpServletRequest request) {
        ApiResponse<List<RoleRepresentation>> response = new ApiResponse<>();
        try {
            logger.info("Retrieving all roles...");
            List<RoleRepresentation> roles = roleService.getAllRoles();
            logger.info("All roles retrieved successfully.");
            response.setSuccess(true);
            response.setMessage("All roles retrieved successfully.");
            response.setData(roles);
            RoleRequestEvent successEvent = new RoleRequestEvent("All Roles Retrieved", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, null);
            roleProducer.sendRoleEvent(successEvent);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all roles: " + e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve all roles: " + e.getMessage());
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Retrieval Failed", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, null);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getRolesByUserId/{userId}")
    public ResponseEntity<?> getRolesByUserId(@PathVariable String userId, HttpServletRequest request){
        ApiResponse<List<RoleRepresentation>> response = new ApiResponse<>();
        try{
            List<RoleRepresentation> rolesRepresentation = roleService.getRolesByUserId(userId);
            if(rolesRepresentation != null){
                response.setSuccess(true);
                response.setMessage("Role retrieved successfully.");
                response.setData(rolesRepresentation);
                RoleRequestEvent successEvent = new RoleRequestEvent("Roles Retrieved by User Id", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, null);
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            else{
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Roles Retrieval Failed by User Id", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, null);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }

        } catch (Exception e){
            logger.error("Error occurred while retrieving role by name: " + e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve role by name: " + e.getMessage());
            RoleRequestEvent failureEvent = new RoleRequestEvent("Roles Retrieval Failed by User Id", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, null);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getRoleByName/{name}")
    public ResponseEntity<?> getRoleByName(@PathVariable String name, HttpServletRequest request) {
        ApiResponse<RoleRepresentation> response = new ApiResponse<>();
        try {
            logger.info("Retrieving role by name: " + name);
            RoleRepresentation role = roleService.getRoleByName(name);
            if(role != null){
                logger.info("Role retrieved successfully.");
                response.setSuccess(true);
                response.setMessage("Role retrieved successfully.");
                response.setData(role);
                RoleRequestEvent successEvent = new RoleRequestEvent("Role Retrieved by Name", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, name);
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }else{
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Role Retrieval Failed by Name", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, name);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }

        } catch (Exception e) {
            logger.error("Error occurred while retrieving role by name: " + e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve role by name: " + e.getMessage());
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Retrieval Failed by Name", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, name);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/updateRoleByName/{name}")
    public ResponseEntity<?> updateRoleByName(@PathVariable String name, @RequestBody @Valid RoleRequest roleRequest, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(buildValidationError(bindingResult));
        }
        ApiResponse<RoleRepresentation> role = new ApiResponse<>();
        try {
            ResponseEntity<RoleRepresentation> response = roleService.updateRoleByName(name, roleRequest);
            if (response.getStatusCode() == HttpStatus.OK) {
                role.setSuccess(true);
                role.setMessage("Role updated successfully.");
                role.setData(response.getBody());
                RoleRequestEvent successEvent = new RoleRequestEvent("Role Updated by Name", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, name);
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.status(HttpStatus.OK).body(role);
            } else if (response.getStatusCode() == HttpStatus.CONFLICT) {
                RoleRequestEvent conflictEvent = new RoleRequestEvent("Role Update Failed by Name", "CONFLICT", LocalDateTime.now(), request.getRemoteAddr(), null, name);
                roleProducer.sendRoleEvent(conflictEvent);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(buildConflictError());
            }
            else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Role Update Failed by Name", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, name);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        }catch (Exception e) {
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Update Failed by Name", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, name);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
        }
    }

    @DeleteMapping("/deleteRoleByName/{name}")
    public ResponseEntity<?> deleteRoleByName(@PathVariable String name, HttpServletRequest request) {
        ApiResponse<List<RoleRepresentation>> response = new ApiResponse<>();
        try {
            logger.info("Deleting role by name: " + name);
            RoleRepresentation existingRole = roleService.getRoleByName(name);
            if (existingRole == null) {
                APIError error = new APIError();
                error.setError_code(4040);
                error.setError_name("NOT_FOUND");
                error.setError_description("Role with name " + name + " not found.");
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Role Deletion Failed by Name", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, name);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            roleService.deleteRoleByName(name);
            logger.info("Role deleted successfully.");

            List<RoleRepresentation> remainingRoles = roleService.getAllRoles();

            response.setSuccess(true);
            response.setMessage("Role deleted successfully.");
            response.setData(remainingRoles);
            RoleRequestEvent successEvent = new RoleRequestEvent("Role Deleted by Name", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, name);
            roleProducer.sendRoleEvent(successEvent);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error occurred while deleting role by name: " + e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to delete role by name: " + e.getMessage());
            response.setData(new ArrayList<>());
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Deletion Failed by Name", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, name);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getRoleById/{rolesId}")
    public ResponseEntity<?> getRoleById(@PathVariable UUID rolesId, HttpServletRequest request) {
        ApiResponse<RoleRepresentation> response = new ApiResponse<>();
        try {
            logger.info("Retrieving role by ID: " + rolesId);
            RoleRepresentation role = roleService.getRoleById(rolesId);
            if(role != null){
                logger.info("Role retrieved successfully.");
                response.setSuccess(true);
                response.setMessage("Role retrieved successfully.");
                response.setData(role);
                RoleRequestEvent successEvent = new RoleRequestEvent("Role Retrieved by Id", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            else{
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Role Retrieval Failed by Id", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), rolesId, null);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }

        } catch (Exception e) {
            logger.error("Error occurred while retrieving role by ID: " + e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve role by ID: " + e.getMessage());
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Retrieval Failed by Id", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), rolesId, null);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/updateRoleById/{rolesId}")
    public ResponseEntity<?> updateRoleById(@PathVariable UUID rolesId, @RequestBody @Valid RoleRequest roleRequest, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(buildValidationError(bindingResult));
        }
        ApiResponse<RoleRepresentation> role = new ApiResponse<>();
        try {
            ResponseEntity<RoleRepresentation> response = roleService.updateRoleById(rolesId, roleRequest);
            if (response.getStatusCode() == HttpStatus.OK) {
                role.setSuccess(true);
                role.setMessage("Role updated successfully.");
                role.setData(response.getBody());
                RoleRequestEvent successEvent = new RoleRequestEvent("Role Updated by Id", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.status(HttpStatus.OK).body(role);
            } else if (response.getStatusCode() == HttpStatus.CONFLICT) {
                RoleRequestEvent conflictEvent = new RoleRequestEvent("Role Update Failed by Id", "CONFLICT", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
                roleProducer.sendRoleEvent(conflictEvent);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(buildConflictError());
            }
            else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Role Update Failed by Id", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        }catch (Exception e) {
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Update Failed by Id", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
        }

    }

    @DeleteMapping("/deleteRoleById/{rolesId}")
    public ResponseEntity<?> deleteRoleById(@PathVariable UUID rolesId, HttpServletRequest request) {
        ApiResponse<List<RoleRepresentation>> response = new ApiResponse<>();
        try {
            logger.info("Deleting role by ID: " + rolesId);
            if (!roleService.roleExists(rolesId)) {
                APIError error = new APIError();
                error.setError_code(4040);
                error.setError_name("NOT_FOUND");
                error.setError_description("Role with ID " + rolesId + " not found.");
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Role Deletion Failed by Id", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            roleService.deleteRoleById(rolesId);
            logger.info("Role deleted successfully.");

            List<RoleRepresentation> remainingRoles = roleService.getAllRoles();

            response.setSuccess(true);
            response.setMessage("Role deleted successfully.");
            response.setData(remainingRoles);
            RoleRequestEvent successEvent = new RoleRequestEvent("Role Deleted by Id", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
            roleProducer.sendRoleEvent(successEvent);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.error("Error occurred while deleting role by ID: " + e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to delete role by ID: " + e.getMessage());
            response.setData(new ArrayList<>());
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Deletion Failed by Id", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(),  rolesId, null);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getAllGroupsByRoleId/{roleId}")
    public ResponseEntity<?> getAllGroupsByRoleId(@PathVariable("roleId") String roleId, HttpServletRequest request) {
        ApiResponse<List<GroupRepresentation>> response = new ApiResponse<>();
        try {
            logger.info("Retrieving all groups for role with ID {}", roleId);
            List<GroupRepresentation> groups = roleService.getAllGroupsByRoleId(roleId);
            if(groups != null){
                logger.info("All groups for role retrieved successfully.");
                response.setSuccess(true);
                response.setMessage("All groups for role retrieved successfully.");
                response.setData(groups);
                RoleRequestEvent successEvent = new RoleRequestEvent("All Groups Retrieved for Role", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(),  null, null);
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            else{
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("All Groups Retrieval Failed for Role", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(),  null, null );
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }

        } catch (Exception e) {
            logger.error("Error occurred while retrieving all groups for role: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to retrieve all groups for role: " + e.getMessage());
            RoleRequestEvent failureEvent = new RoleRequestEvent("All Groups Retrieval Failed for Role", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, null );
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/assignGroupsToRole/{roleId}")
    public ResponseEntity<?> assignGroupsToRole(@PathVariable String roleId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
        ApiResponse<List> response = new ApiResponse<>();
        try {
            List<String> groupIds = requestBody.get("groupIds");
            if (groupIds != null) {
                for (String groupId : groupIds) {
                    logger.info("Assigning group with ID {} to role with ID {}", groupId, roleId);
                    roleService.assignGroupToRole(roleId, groupId);
                    logger.info("Group assigned to role successfully.");
                }
                RoleRequestEvent successEvent = new RoleRequestEvent("Groups Assigned to Role", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, null);
                roleProducer.sendRoleEvent(successEvent);
            } else {
                response.setSuccess(false);
                response.setMessage("Group IDs not found in request body");
                RoleRequestEvent badRequestEvent = new RoleRequestEvent("Groups Assignment Failed to Role", "BAD_REQUEST", LocalDateTime.now(), request.getRemoteAddr(), null, null);
                roleProducer.sendRoleEvent(badRequestEvent);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            response.setSuccess(true);
            response.setMessage("Groups assigned to role successfully.");
            List<GroupRepresentation> groups = roleService.getAllGroupsByRoleId(roleId);
            response.setData(groups);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error occurred while assigning groups to role: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("Failed to assign groups to role: " + e.getMessage());
            RoleRequestEvent successEvent = new RoleRequestEvent("Groups Assigned to Role", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, null);
            roleProducer.sendRoleEvent(successEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deleteRoleByRoleIdAndUserId/{userId}")
    public ResponseEntity<?> deleteRoleByRoleIdAndUserId(@PathVariable String userId, @RequestBody RoleIdRequest roleIdRequest, HttpServletRequest request) {
        try {
            roleService.deleteRoleByRoleIdAndUserId(roleIdRequest.getRoleId(), userId);
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Role deleted for user successfully");
            RoleRequestEvent successEvent = new RoleRequestEvent("Role Deleted for User", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, userId);
            roleProducer.sendRoleEvent(successEvent);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("DELETE_FAILED");
            error.setError_description("Failed to delete role for user");
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Deletion Failed for User", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, userId);
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/deleteRoleByGroupIdAndRoleIds/{groupId}")
    public ResponseEntity<?> deleteRoleFromGroups(@PathVariable String groupId, @RequestBody List<String> roleIds, HttpServletRequest request) {
        try {
            for (String roleId : roleIds) {
                roleService.deleteRoleFromGroup(roleId, groupId);
            }
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Role deleted from groups successfully");
            RoleRequestEvent successEvent = new RoleRequestEvent("Role Deleted from Groups", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, String.join(",", roleIds));
            roleProducer.sendRoleEvent(successEvent);
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("DELETE_FAILED");
            error.setError_description("Failed to delete role from groups");
            RoleRequestEvent failureEvent = new RoleRequestEvent("Role Deletion Failed from Groups", "FAILURE", LocalDateTime.now(), request.getRemoteAddr(), null, String.join(",", roleIds));
            roleProducer.sendRoleEvent(failureEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/removeUsersFromRoleByRoleId/{roleId}")
    public ResponseEntity<?> removeUsersFromRoleByRoleId(@PathVariable String roleId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
        try {
            List<String> userIds = requestBody.get("userIds");
            boolean removedSuccessfully = roleService.removeUsersFromRoleByRoleId(roleId, userIds);
            if (removedSuccessfully) {
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Users removed from the role successfully");
//                apiResponse.setData(roleService.getRoleUsers(roleId));
                RoleRequestEvent successEvent = new RoleRequestEvent("Users Removed from Role", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, String.join(",", userIds));
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.ok().body(apiResponse);
            } else {
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Users Removal from Role Failed", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, null);
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
        } catch (IllegalArgumentException e) {
            RoleRequestEvent notFoundEvent = new RoleRequestEvent("Users Removal from Role Failed", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null,  null);
            roleProducer.sendRoleEvent(notFoundEvent);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBadRequestError());
        } catch (Exception e) {
            RoleRequestEvent badRequestEvent = new RoleRequestEvent("Users Removal from Role Failed", "BAD_REQUEST", LocalDateTime.now(), request.getRemoteAddr(), null, null);
            roleProducer.sendRoleEvent(badRequestEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
        }
    }

    @DeleteMapping("/removeRolesFromUserByUserId/{userId}")
    public ResponseEntity<?> removeRolesFromUserByUserId(@PathVariable String userId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
        try {
            List<String> roleIds = requestBody.get("roleIds");
            boolean removedSuccessfully = roleService.removeRolesFromUserByUserId(userId, roleIds);
            if (removedSuccessfully) {
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Roles removed from the user successfully");
                apiResponse.setData(roleService.getRolesByUserId(userId));
                RoleRequestEvent successEvent = new RoleRequestEvent("Roles Removed from User", "SUCCESS", LocalDateTime.now(), request.getRemoteAddr(), null, String.join(",", roleIds));
                roleProducer.sendRoleEvent(successEvent);
                return ResponseEntity.ok().body(apiResponse);
            } else {
                RoleRequestEvent notFoundEvent = new RoleRequestEvent("Roles Removal from User Failed", "NOT_FOUND", LocalDateTime.now(), request.getRemoteAddr(), null, String.join(",", roleIds));
                roleProducer.sendRoleEvent(notFoundEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
        } catch (IllegalArgumentException e) {
            RoleRequestEvent badRequestEvent = new RoleRequestEvent("Roles Removal from User Failed", "BAD_REQUEST", LocalDateTime.now(), request.getRemoteAddr(), null, null);
            roleProducer.sendRoleEvent(badRequestEvent);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildBadRequestError());
        } catch (Exception e) {
            RoleRequestEvent internalServerErrorEvent = new RoleRequestEvent("Roles Removal from User Failed", "INTERNAL_SERVER_ERROR", LocalDateTime.now(), request.getRemoteAddr(), null, null);
            roleProducer.sendRoleEvent(internalServerErrorEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildInternalServerError());
        }
    }

}

