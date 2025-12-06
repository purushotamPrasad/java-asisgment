package com.qssence.backend.authservice.service;
import com.qssence.backend.authservice.dto.responce.User.UserDetailsResponse;
import com.qssence.backend.authservice.dto.responce.UserResponse;

import java.util.List;
import java.util.UUID;

import com.qssence.backend.authservice.dto.request.UserRequest;

/**
 * Interface for user service.
 * Defines methods for user CRUD operations.
 */
public interface IUserService {
    Object createUser(UserRequest userRequest);
    Object getAllUsers();
    Object getUserById(String userId);
    boolean isUserExist(String userId);
    UserDetailsResponse getUserDetailsById(UUID userId);
    Object getUserByUsername(String username);
    Object updateUserById(String userId, UserRequest userRequest);
    Object updateUserByUsername(String username, UserRequest userRequest);
    Object deleteUserById(String userId);
    Object deleteUserByUsername(String username);
    List<UserResponse> getUsersByRoleId(String roleId);
    void deleteUserFromRole(String roleId, String userId);
    void deleteUserByGroupAndUserId(String userId, String groupId);
}
