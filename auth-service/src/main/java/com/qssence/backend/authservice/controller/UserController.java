package com.qssence.backend.authservice.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.authservice.dbo.User;
import com.qssence.backend.authservice.dto.APIError;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.GroupIdRequest;
import com.qssence.backend.authservice.dto.request.RoleIdRequest;
import com.qssence.backend.authservice.dto.request.UserIdDTO;
import com.qssence.backend.authservice.dto.request.UserRequest;
import com.qssence.backend.authservice.dto.responce.UserResponse;
import com.qssence.backend.authservice.dto.responce.User.UserDetailsResponse;
import com.qssence.backend.authservice.kafka.event.UserRequestEvent;
import com.qssence.backend.authservice.kafka.producer.UserProducer;
import com.qssence.backend.authservice.service.implementation.RoleService;
import com.qssence.backend.authservice.service.implementation.UserRoleMappingService;
import com.qssence.backend.authservice.service.implementation.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserProducer userProducer;

    @Autowired
    RoleService roleService;

    @Autowired
    private UserRoleMappingService userRoleMappingService;
    
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserRequest userRequest, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return  handleErrorResponse(buildValidationError(bindingResult), HttpStatus.BAD_REQUEST);
        }
        int response = userService.createUser(userRequest).getStatus();
        if (response == HttpStatus.CREATED.value()) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("User created successfully");
            apiResponse.setData(userService.getAllUsers());
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User created successfully",
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userRequest.getUsername()
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.created(URI.create("/api/v1/user/createUser")).body(apiResponse);
        } else if (response == HttpStatus.CONFLICT.value()) {
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User creation failed: Conflict",
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userRequest.getUsername()
            );
            userProducer.sendMessage(userEvent);
            return handleErrorResponse(buildConflictError(), HttpStatus.CONFLICT);
        } else {
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User creation failed: Bad request",
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userRequest.getUsername()
            );
            userProducer.sendMessage(userEvent);
            return handleErrorResponse(buildBadRequestError(), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<APIError> handleErrorResponse(APIError error, HttpStatus status) {
        error.setError_description(Objects.requireNonNullElse(error.getError_description(), status.getReasonPhrase()));
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
            e.printStackTrace();
        }

        return error;
    }

    private APIError buildConflictError() {
        APIError error = new APIError();
        error.setError_code(4009);
        error.setError_description("User already exists.");
        error.setError_name("ALREADY_EXIST");
        return error;
    }
    private APIError buildNotFoundError() {
        APIError error = new APIError();
        error.setError_code(4040);
        error.setError_name("NOT_FOUND");
        error.setError_description("User Not Found");
        return error;
    }
    private APIError buildBadRequestError() {
        APIError error = new APIError();
        error.setError_code(4000);
        error.setError_name("INVALID_REQUEST");
        error.setError_description("Invalid request");
        return error;
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        List<UserResponse> users = userService.getAllUsers();

        if(!users.isEmpty()) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("UserCreated Successfully");
            apiResponse.setData(users);
            UserRequestEvent userEvent = new UserRequestEvent(
                    "Users retrieved successfully",
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    ""
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.ok().body(apiResponse);
        } else {
            APIError error = new APIError();
            error.setError_code(2000);
            error.setError_name("INVALID_REQUEST");
            error.setError_description("Invalid request");
            UserRequestEvent userEvent = new UserRequestEvent(
                    "No users found",
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    ""
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.ok().body(error);
        }
    }

    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId, HttpServletRequest request) {
        ResponseEntity<?> response = userService.getUserById(userId);
        HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());

        UserRequestEvent userEvent;
        if (status == HttpStatus.NOT_FOUND) {
            userEvent = new UserRequestEvent(
                    "User not found with ID: " + userId,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
        } else {
            userEvent = new UserRequestEvent(
                    "User retrieved successfully with ID: " + userId,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);

            return response;
        }
    }

    @GetMapping("/getUserDetailsById/{userId}")
    public ResponseEntity<?> getUserDetailsById(@PathVariable UUID userId, HttpServletRequest request) {
        ApiResponse<UserDetailsResponse> response = new ApiResponse<>();
        try {
            if (!userService.isUserExists(userId.toString())) {
                response.setMessage("User with ID " + userId + " does not exist.");
                response.setSuccess(false);
                return ResponseEntity.ok(response);
            }

            UserDetailsResponse userDetails = userService.getUserDetailsById(userId);
            response.setSuccess(true);
            response.setData(userDetails);
            response.setMessage("User details retrieved successfully.");

            UserRequestEvent userEvent = new UserRequestEvent(
                    "User details retrieved successfully for ID: " + userId,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId.toString()
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            UserRequestEvent userEvent = new UserRequestEvent(
                    "Failed to retrieve user details for ID: " + userId + ". Error: " + e.getMessage(),
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId.toString()
            );
            userProducer.sendMessage(userEvent);

            response.setSuccess(false);
            response.setMessage("Failed to retrieve user details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username, HttpServletRequest request) {
        ResponseEntity<?> response = userService.getUserByUsername(username);
        HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());

        UserRequestEvent userEvent;
        if (status == HttpStatus.NOT_FOUND) {
            userEvent = new UserRequestEvent(
                    "User not found with username: " + username,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    username
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
        } else {
            userEvent = new UserRequestEvent(
                    "User retrieved successfully with username: " + username,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    username
            );
            userProducer.sendMessage(userEvent);

            return response;
        }
    }

    @PutMapping("updateUserById/{userId}")
    public ResponseEntity<?> updateUserById(@PathVariable String userId, @RequestBody @Valid UserRequest userRequest, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(buildValidationError(bindingResult));
        }
        if (userService.updateUserById(userId, userRequest).getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
        }
        ResponseEntity<UserRepresentation> response = userService.updateUserById(userId, userRequest);
        HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());
        ApiResponse<UserRepresentation> role = new ApiResponse<>();
        if (status == HttpStatus.NOT_FOUND) {
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User not found with ID: " + userId,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());

        } else if (status == HttpStatus.OK) {
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User updated successfully with ID: " + userId,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);

            role.setSuccess(true);
            role.setMessage("User updated successfully.");
            role.setData(response.getBody());
            return ResponseEntity.status(HttpStatus.OK).body(role);
        }

        return response;
    }

    @PutMapping("/updateUserByUsername/{username}")
    public ResponseEntity<?> updateUserByUsername(@PathVariable String username,
                                                  @RequestBody @Valid UserRequest userRequest,
                                                  BindingResult bindingResult,
                                                  HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(buildValidationError(bindingResult));
        }

        ResponseEntity<UserRepresentation> response = userService.updateUserByUsername(username, userRequest);
        HttpStatus status = HttpStatus.valueOf(response.getStatusCode().value());

        ApiResponse<UserRepresentation> role = new ApiResponse<>();
        UserRequestEvent userEvent;

        if (status == HttpStatus.NOT_FOUND) {
            userEvent = new UserRequestEvent(
                    "User not found with username: " + username,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    username
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildNotFoundError());
        } else if (status == HttpStatus.OK) {
            userEvent = new UserRequestEvent(
                    "User updated successfully with username: " + username,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    username
            );
            userProducer.sendMessage(userEvent);

            role.setSuccess(true);
            role.setMessage("User updated successfully.");
            role.setData(response.getBody());
            return ResponseEntity.status(HttpStatus.OK).body(role);
        }

        return response;
    }

    @DeleteMapping("deleteUserById/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable String userId, HttpServletRequest request) {
        ResponseEntity<?> responseEntity = userService.deleteUserById(userId);
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            List<UserResponse> remainingUsers = userService.getAllUsers();
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("User deleted successfully");
            apiResponse.setData(remainingUsers);
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User deleted successfully with ID: " + userId,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.ok().body(apiResponse);
        } else if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            APIError error = new APIError();
            error.setError_code(4040);
            error.setError_name("NOT_FOUND");
            error.setError_description("User not found");
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User not found with ID: " + userId,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else {
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Internal server error occurred");
            UserRequestEvent userEvent = new UserRequestEvent(
                    "Internal server error occurred for user with ID: " + userId,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("deleteUserByUsername/{username}")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username, HttpServletRequest request) {
        ResponseEntity<Void> responseEntity = userService.deleteUserByUsername(username);

        UserRequestEvent userEvent;
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            List<UserResponse> remainingUsers = userService.getAllUsers();

            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("User deleted successfully");
            apiResponse.setData(remainingUsers);
            userEvent = new UserRequestEvent(
                    "User deleted successfully with username: " + username,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    username
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.ok().body(apiResponse);
        } else if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            APIError error = new APIError();
            error.setError_code(4040);
            error.setError_name("NOT_FOUND");
            error.setError_description("User not found");
            userEvent = new UserRequestEvent(
                    "User not found with username: " + username,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    username
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else {
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("INTERNAL_SERVER_ERROR");
            error.setError_description("Internal server error occurred");
            userEvent = new UserRequestEvent(
                    "Internal server error occurred for user with username: " + username,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    username
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/getAllUsersByRoleId/{roleId}")
    public ResponseEntity<?> getAllUsersByRoleId(@PathVariable String roleId, HttpServletRequest request) {
        List<UserResponse> users = userService.getUsersByRoleId(roleId);

        if (!users.isEmpty()) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Users retrieved successfully");
            apiResponse.setData(users);
            UserRequestEvent userEvent = new UserRequestEvent(
                    "Users retrieved successfully for roleId: " + roleId,
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    roleId
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.ok().body(apiResponse);
        } else {
            APIError error = new APIError();
            error.setError_code(2000);
            error.setError_name("NO_USERS_FOUND");
            error.setError_description("No users found for the given role ID");


            UserRequestEvent userEvent = new UserRequestEvent(
                    "No users found for roleId: " + roleId,
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    roleId
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    @DeleteMapping("/deleteUserByGroupByAndUserId/{userId}")
    public ResponseEntity<?> deleteUserFromGroupByUserId(@PathVariable String userId, @RequestBody GroupIdRequest groupIdRequest, HttpServletRequest request) {
        try {
            userService.deleteUserByGroupAndUserId(userId, groupIdRequest.getGroupId());
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("User deleted from group successfully");
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User deleted from group successfully",
                    "SUCCESS",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);

            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("DELETE_FAILED");
            error.setError_description("Failed to delete user from group");
            UserRequestEvent userEvent = new UserRequestEvent(
                    "Failed to delete user from group: " + e.getMessage(),
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userId
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("addRoleToUserByUserId/{userId}")
    public ResponseEntity<?> assignRolesToUser(@PathVariable String userId, @RequestBody Map<String, List<String>> requestBody, HttpServletRequest request) {
        try {
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIError(2002, "MISSING_USER_ID", "User ID is required"));
            }
            if (!userService.isUserExists(userId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIError(2001, "USER_NOT_FOUND", "User not found with ID: " + userId));
            }
            List<String> roleIds = requestBody.get("roleIds");
            if (roleIds == null || roleIds.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIError(2003, "MISSING_ROLE_ID", "At least one Role ID is required"));
            }

            ApiResponse apiResponse = new ApiResponse();
            boolean rolesAssigned = false;
            for (String roleId : roleIds) {
                if (!roleService.isRoleExists(roleId)) {
                    continue;
                }
                if (roleService.assignRole(roleId, userId)) {
                    userRoleMappingService.assignRoleToUser(roleId, userId);
                    rolesAssigned = true;
                }
            }

            if (rolesAssigned) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Roles assigned successfully to user.");
                apiResponse.setData(roleService.getAssignedRoles(userId));
                UserRequestEvent userEvent = new UserRequestEvent(
                        "Roles assigned successfully to user with ID: " + userId,
                        "SUCCESS",
                        LocalDateTime.now(),
                        request.getRemoteAddr(),
                        userId
                );
                userProducer.sendMessage(userEvent);
                return ResponseEntity.ok().body(apiResponse);
            } else {
                UserRequestEvent userEvent = new UserRequestEvent(
                        "Failed to assign roles to user with ID: " + userId,
                        "FAILURE",
                        LocalDateTime.now(),
                        request.getRemoteAddr(),
                        userId
                );
                userProducer.sendMessage(userEvent);
                return ResponseEntity.ok().body(new APIError(2004, "ROLE_ASSIGNMENT_FAILED", "Roles could not be assigned to this user."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIError(500, "INTERNAL_SERVER_ERROR", "Internal Server Error"));
        }
    }

    @PostMapping("addUserToRoleByUserId/{userId}")
    public Object assignRoleToUser(@PathVariable String userId, @RequestBody RoleIdRequest roleIdRequest, HttpServletRequest request) {
        try {
            if (userId == null || userId.isEmpty()) {
                APIError error = new APIError();
                error.setError_code(2002);
                error.setError_name("MISSING_USER_ID");
                error.setError_description("User ID is required");
                return ResponseEntity.ok().body(error);
            }

            if (!userService.isUserExists(userId)) {
                APIError error = new APIError();
                error.setError_code(2001);
                error.setError_name("USER_NOT_FOUND");
                error.setError_description("User not found with ID: " + userId);
                return ResponseEntity.ok().body(error);
            }

            String roleId = roleIdRequest.getRoleId();
            if (roleId == null || roleId.isEmpty()) {
                APIError error = new APIError();
                error.setError_code(2003);
                error.setError_name("MISSING_ROLE_ID");
                error.setError_description("Role ID is required");
                return ResponseEntity.ok().body(error);
            }


            if (!roleService.isRoleExists(roleId)) {
                APIError error = new APIError();
                error.setError_code(2004);
                error.setError_name("ROLE_NOT_FOUND");
                error.setError_description("Role not found with ID: " + roleId);
                return ResponseEntity.ok().body(error);
            }
            ApiResponse apiResponse = new ApiResponse();

            if (roleService.assignRole(roleId, userId)) {
                userRoleMappingService.assignRoleToUser(roleId, userId);
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Role assigned successfully to user.");
                apiResponse.setData(roleService.getAssignedRoles(userId));
                UserRequestEvent userEvent = new UserRequestEvent(
                        "Role assigned successfully to user.",
                        "SUCCESS",
                        LocalDateTime.now(),
                        request.getRemoteAddr(),
                        userId
                );
                userProducer.sendMessage(userEvent);
            }else{
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Role not assigned to this user.");
                apiResponse.setData("");
                UserRequestEvent userEvent = new UserRequestEvent(
                        "Role not assigned to this user.",
                        "FAILURE",
                        LocalDateTime.now(),
                        request.getRemoteAddr(),
                        userId
                );
                userProducer.sendMessage(userEvent);
            }

            return ResponseEntity.ok().body(apiResponse);
        } catch (HttpMessageNotReadableException e) {
            APIError error = new APIError();
            error.setError_code(2005);
            error.setError_name("REQUEST_BODY_MISSING");
            error.setError_description("Request body is missing");
            UserRequestEvent userEvent = new UserRequestEvent(
                    "Request body is missing.",
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    ""
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.ok().body(error);
        }
    }

    @DeleteMapping("/deleteUserByRoleIdAndUserId/{roleId}")
    public ResponseEntity<?> deleteUserFromRole(@PathVariable String roleId, @RequestBody UserIdDTO userIdDTO, HttpServletRequest request) {
        try {
            if (roleId == null || roleId.isEmpty()) {
                APIError error = new APIError();
                error.setError_code(2003);
                error.setError_name("MISSING_ROLE_ID");
                error.setError_description("Role ID is required");
                return ResponseEntity.ok().body(error);
            }


            if (!roleService.isRoleExists(roleId)) {
                APIError error = new APIError();
                error.setError_code(2004);
                error.setError_name("ROLE_NOT_FOUND");
                error.setError_description("Role not found with ID: " + roleId);
                return ResponseEntity.ok().body(error);
            }

            if (userIdDTO.getUserId() == null || userIdDTO.getUserId().isEmpty()) {
                APIError error = new APIError();
                error.setError_code(2002);
                error.setError_name("MISSING_USER_ID");
                error.setError_description("User ID is required");
                return ResponseEntity.ok().body(error);
            }

            if (!userService.isUserExists(userIdDTO.getUserId())) {
                APIError error = new APIError();
                error.setError_code(2001);
                error.setError_name("USER_NOT_FOUND");
                error.setError_description("User not found with ID: " + userIdDTO.getUserId());
                return ResponseEntity.ok().body(error);
            }

            ApiResponse apiResponse = new ApiResponse();
            if (roleService.removeRole(roleId, userIdDTO.getUserId())) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("User deleted successfully");
                apiResponse.setData(roleService.getAssignedRoles(userIdDTO.getUserId()));
                UserRequestEvent userEvent = new UserRequestEvent(
                        "User deleted successfully from role.",
                        "SUCCESS",
                        LocalDateTime.now(),
                        request.getRemoteAddr(),
                        userIdDTO.getUserId()
                );
                userProducer.sendMessage(userEvent);
            }else{
                apiResponse.setSuccess(false);
                apiResponse.setMessage("User not deleted to this user.");
                apiResponse.setData(roleService.getRole(roleId));
                UserRequestEvent userEvent = new UserRequestEvent(
                        "User deletion failed from role.",
                        "FAILURE",
                        LocalDateTime.now(),
                        request.getRemoteAddr(),
                        userIdDTO.getUserId()
                );
                userProducer.sendMessage(userEvent);
            }

            return ResponseEntity.ok().body(apiResponse);

        } catch (Exception e) {
            APIError error = new APIError();
            error.setError_code(5000);
            error.setError_name("DELETE_FAILED");
            error.setError_description("Failed to delete user from role");
            UserRequestEvent userEvent = new UserRequestEvent(
                    "User deletion failed from role: " + e.getMessage(),
                    "FAILURE",
                    LocalDateTime.now(),
                    request.getRemoteAddr(),
                    userIdDTO.getUserId()
            );
            userProducer.sendMessage(userEvent);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
