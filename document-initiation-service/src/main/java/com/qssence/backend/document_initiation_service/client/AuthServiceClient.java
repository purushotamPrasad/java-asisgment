package com.qssence.backend.document_initiation_service.client;

import com.qssence.backend.document_initiation_service.dto.RolePermissionRequest;
import com.qssence.backend.document_initiation_service.dto.UserMasterDto;
import com.qssence.backend.document_initiation_service.dto.response.GroupByIdResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.GroupUsersResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.GroupsResponseDto;
import com.qssence.backend.document_initiation_service.dto.response.UsersByIdsResponseDto;
import com.qssence.backend.document_initiation_service.config.FeignClientLoggingConfig;
import com.qssence.backend.document_initiation_service.exeptionHandler.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//@FeignClient(name = "auth-service", url = "http://localhost:8082", configuration = FeignClientLoggingConfig.class)
@FeignClient(name = "auth-service", url = "http://46.28.44.11:8082", configuration = FeignClientLoggingConfig.class)
public interface AuthServiceClient {

    @PostMapping("/auth/roles/check")
    Boolean checkRolePermission(@RequestBody RolePermissionRequest request);

//    @GetMapping("/auth/permissions/check")
//    ResponseEntity<Boolean> checkPermission(
//            @RequestParam Long userId,
//            @RequestParam Long documentId,
//            @RequestParam String permission
//    );

    @GetMapping("/api/permissions/check")
    Boolean checkPermission(@RequestParam("userId") Long userId, @RequestParam("permission") String permission);

    @GetMapping("/api/v1/groups/{groupId}/users")
    GroupUsersResponseDto getUsersByGroupId(@PathVariable("groupId") Long groupId);

    @GetMapping("/api/v1/groups/getAll")
    GroupsResponseDto getAllGroups();

    @PostMapping("/api/v1/user/by-ids")
    UsersByIdsResponseDto getUsersByIds(@RequestBody List<Long> userIds);

    @GetMapping("/api/v1/groups/getById/{id}")
    GroupByIdResponseDto getGroupById(@PathVariable("id") Long groupId);

    @GetMapping("/api/v1/user/getAll")
    ApiResponse<List<UserMasterDto>> getAllUsers();

    // 🔹 Get all plants
    @GetMapping("/api/v1/plants/getAll")
    Map<String, Object> getAllPlants();

    // 🔹 Get plant by Id
    @GetMapping("/api/v1/plants/getById/{id}")
    Map<String, Object> getPlantById(@PathVariable("id") Long id);

    @GetMapping("/actuator/health")
    String healthCheck();
}
