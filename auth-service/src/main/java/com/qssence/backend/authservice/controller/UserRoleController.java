package com.qssence.backend.authservice.controller;

import java.util.List;

import com.qssence.backend.authservice.dto.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qssence.backend.authservice.dto.UserRoleDto;
import com.qssence.backend.authservice.service.UserRoleService;

@RestController
@RequestMapping("/api/v1/role")
public class UserRoleController {

	private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

	@Autowired
	private UserRoleService userRoleService;

	// Insert the user role data
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<UserRoleDto>> createUserRole(@Valid @RequestBody UserRoleDto userRoleDto) {
		logger.info("Creating user role...");
		ApiResponse<UserRoleDto> response = new ApiResponse<>();

		try {
			// Check if role name is provided
			if (userRoleDto.getUserRoleName() == null || userRoleDto.getUserRoleName().trim().isEmpty()) {
				response.setMessage("Role name is required");
				response.setSuccess(false);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Check if the role already exists
			UserRoleDto existingRole = userRoleService.findByUserRoleName(userRoleDto.getUserRoleName().trim());
			if (existingRole != null) {
				response.setMessage("Role name already exists");
				response.setSuccess(false);
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			}

			// Create the user role
			UserRoleDto createdUserRole = userRoleService.createUserRole(userRoleDto);
			if (createdUserRole != null) {
				response.setSuccess(true);
				response.setMessage("User role created successfully");
				response.setData(createdUserRole);
				return ResponseEntity.status(HttpStatus.CREATED).body(response);
			} else {
				response.setSuccess(false);
				response.setMessage("User role creation failed");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to create user role: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Get all user roles
	@GetMapping("/getAll")
	public ResponseEntity<ApiResponse<List<UserRoleDto>>> getAllUserRoles() {
		ApiResponse<List<UserRoleDto>> response = new ApiResponse<>();
		try {
			List<UserRoleDto> userRoles = userRoleService.getAllUserRole();
			response.setSuccess(true);
			response.setMessage("User roles retrieved successfully");
			response.setData(userRoles);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to retrieve user roles: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Search by ID user role
	@GetMapping("/getById/{id}")
	public ResponseEntity<ApiResponse<UserRoleDto>> getUserRoleById(@PathVariable(name = "id") Long id) {
		ApiResponse<UserRoleDto> response = new ApiResponse<>();
		try {
			UserRoleDto userRole = userRoleService.getUserRoleByid(id);
			response.setSuccess(true);
			response.setMessage("User role retrieved successfully");
			response.setData(userRole);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to retrieve user role: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Update the user role data
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse<UserRoleDto>> updateUserRolesData(@RequestBody UserRoleDto userRoleDto, @PathVariable(name = "id") Long id) {
		ApiResponse<UserRoleDto> response = new ApiResponse<>();
		try {
			UserRoleDto updatedUserRole = userRoleService.updateUserRoleDetails(userRoleDto, id);
			response.setSuccess(true);
			response.setMessage("User role updated successfully");
			response.setData(updatedUserRole);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to update user role: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Delete user role data
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteUserRoleData(@PathVariable(name = "id") Long id) {
		ApiResponse<Void> response = new ApiResponse<>();
		try {
			userRoleService.deleteUserRole(id);
			response.setSuccess(true);
			response.setMessage("User role deleted successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to delete user role: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
