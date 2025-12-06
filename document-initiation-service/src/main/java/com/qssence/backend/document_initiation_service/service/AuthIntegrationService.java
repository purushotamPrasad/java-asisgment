package com.qssence.backend.document_initiation_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.document_initiation_service.client.AuthServiceClient;
import com.qssence.backend.document_initiation_service.dto.UserDto;
import com.qssence.backend.document_initiation_service.dto.UserMasterDto;
import com.qssence.backend.document_initiation_service.dto.response.*;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthIntegrationService {

    private final AuthServiceClient authServiceClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 🔹 Plants Integration

    /**
     * Fetch all plants from auth-service
     */
    public List<PlantResponse> getAllPlants() {
        try {
            log.info("Fetching all plants from auth-service");
            Map<String, Object> response = authServiceClient.getAllPlants();

            if (response != null && response.containsKey("data")) {
                Object data = response.get("data");
                return objectMapper.convertValue(data, new TypeReference<List<PlantResponse>>() {});
            } else {
                log.warn("No plants found or API returned null");
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching plants from auth-service", e);
            return List.of();
        }
    }

    /**
     * Fetch plant by ID from auth-service
     */
    public PlantResponse getPlantById(Long plantId) {
        try {
            log.info("Fetching plant with ID: {}", plantId);
            Map<String, Object> response = authServiceClient.getPlantById(plantId);

            if (response != null && response.containsKey("data")) {
                Object data = response.get("data");
                return objectMapper.convertValue(data, PlantResponse.class);
            } else {
                log.warn("Plant not found for ID: {}", plantId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching plant for ID: {}", plantId, e);
            return null;
        }
    }

    /**
     * Fetch users from a specific group and convert them to WorkflowStateResponseDto.UserDto
     */
    public List<UserDto> getUsersByGroupId(Long groupId) {
        try {
            log.info("Fetching users for group ID: {}", groupId);
            
            GroupUsersResponseDto response = authServiceClient.getUsersByGroupId(groupId);
            
            if (response != null && response.getSuccess() && response.getData() != null) {
                return response.getData().getUsers();
            } else {
                log.warn("No users found for group ID: {} or API call failed", groupId);
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching users for group ID: {}", groupId, e);
            return List.of();
        }
    }

    /**
     * Fetch all groups from auth-service
     */
    public List<GroupsResponseDto.GroupDto> getAllGroups() {
        try {
            log.info("Fetching all groups from auth-service");
            
            GroupsResponseDto response = authServiceClient.getAllGroups();
            log.info("Response from auth-service: {}", response);
            
            if (response != null && response.getSuccess() && response.getData() != null) {
                log.info("Found {} groups", response.getData().size());
                return response.getData();
            } else {
                log.warn("No groups found or API call failed. Response: {}", response);
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching groups from auth-service", e);
            return List.of();
        }
    }

    /**
     * Fetch a single group by its ID from auth-service
     */
    public GroupByIdResponseDto.GroupData getGroupById(Long groupId) {
        if (groupId == null) {
            return null;
        }
        try {
            log.info("Fetching group with ID: {}", groupId);
            GroupByIdResponseDto response = authServiceClient.getGroupById(groupId);
            log.info("Group response: {}", response);

            if (response != null && response.getSuccess() && response.getData() != null) {
                log.info("Group found: {}", response.getData().getName());
                return response.getData();
            } else {
                log.warn("Group not found for ID: {} or API call failed. Response: {}", groupId, response);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching group for ID: {}", groupId, e);
            return null;
        }
    }

    /**
     * Fetch user details for a list of user IDs
     */
    public List<UserDto> getUsersDetails(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        try {
            log.info("Fetching details for user IDs: {}", userIds);
            UsersByIdsResponseDto response = authServiceClient.getUsersByIds(userIds);
            log.info("Users response: {}", response);

            if (response != null && response.getSuccess() && response.getData() != null) {
                log.info("Found {} users", response.getData().size());
                // Convert UserMasterDto to UserDto
                return response.getData().stream()
                        .map(this::convertToUserDto)
                        .collect(Collectors.toList());
            } else {
                log.warn("No user details found for IDs: {} or API call failed. Response: {}", userIds, response);
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching user details for IDs: {}", userIds, e);
            return List.of();
        }
    }

    /**
     * Convert UserMasterDto to UserDto
     */
    private UserDto convertToUserDto(UserMasterDto userMasterDto) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userMasterDto.getUserId());
        userDto.setUserFirstName(userMasterDto.getUserFirstName());
        userDto.setUserMiddleName(userMasterDto.getUserMiddleName());
        userDto.setUserLastName(userMasterDto.getUserLastName());
        userDto.setUserEmailId(userMasterDto.getUserEmailId());
        userDto.setEmployeeId(userMasterDto.getEmployeeId());
        userDto.setStatus(userMasterDto.getStatus());
        userDto.setDesignation(userMasterDto.getDesignation());
        return userDto;
    }
    public List<UserMasterDto> getAllUsers() {
        try {
            ApiResponse<List<UserMasterDto>> response = authServiceClient.getAllUsers();
            if (response != null && response.getSuccess() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("No users found or API call failed");
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching all users from auth-service", e);
            return List.of();
        }
    }

    /**
     * Test connection to auth-service
     */
    public String testAuthServiceConnection() {
        try {
            log.info("Testing connection to auth-service");
            String health = authServiceClient.healthCheck();
            log.info("Auth service health check response: {}", health);
            return health;
        } catch (Exception e) {
            log.error("Error testing auth-service connection", e);
            throw e;
        }
    }
}