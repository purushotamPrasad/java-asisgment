package com.qssence.backend.authservice.controller;

import com.qssence.backend.authservice.dbo.Status;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.AddUserToRoleRequest;
import com.qssence.backend.authservice.dto.responce.AddUserToRoleResponse;
import com.qssence.backend.authservice.service.IAddUserToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-role")
@RequiredArgsConstructor
public class AddUserToRoleController {

    private final IAddUserToRoleService addUserToRoleService;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<AddUserToRoleResponse>> assignRolesToUser(@RequestBody AddUserToRoleRequest request) {
        try {
            return ResponseEntity.ok(addUserToRoleService.assignUserToRoles(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/update/user/{userId}/role/{roleId}/status/{status}")
    public ResponseEntity<ApiResponse<String>> updateUserRoleStatus(
            @PathVariable Long userId,
            @PathVariable Long roleId,
            @PathVariable String status) {
        try {
            return ResponseEntity.ok(addUserToRoleService.updateUserRoleStatus(userId, roleId, Status.valueOf(status)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AddUserToRoleResponse>> getUserRoles(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(addUserToRoleService.getUserRoles(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/remove/{userId}/role/{roleId}")
    public ResponseEntity<ApiResponse<String>> removeUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            return ResponseEntity.ok(addUserToRoleService.removeUserRole(userId, roleId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
