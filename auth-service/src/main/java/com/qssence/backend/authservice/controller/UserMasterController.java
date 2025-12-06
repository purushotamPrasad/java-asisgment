package com.qssence.backend.authservice.controller;

import java.util.List;
import java.util.Set;

import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.responce.GroupResponseDto;
import com.qssence.backend.authservice.exception.PasswordGenerator;
import com.qssence.backend.authservice.exception.ResourceNotFoundException;
import com.qssence.backend.authservice.service.implementation.KeycloakUserService;
import com.qssence.backend.authservice.service.implementation.UserMasterServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.qssence.backend.authservice.dto.UserMasterDto;
import com.qssence.backend.authservice.service.UserMasterService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
public class UserMasterController
{

	@Autowired
	private UserMasterServiceImpl userMasterService;

    @Autowired
    private KeycloakUserService keycloakUserService;

	private static final Logger logger = LoggerFactory.getLogger(UserMasterController.class);

    // -------- CREATE USER ------------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserMasterDto>> createUser(@Valid @RequestBody UserMasterDto userMasterDto) {
        logger.info("Creating user with email: {}", userMasterDto.getUserEmailId());
        ApiResponse<UserMasterDto> response = new ApiResponse<>();

        try {
            // ✅ Use the service method which handles everything (Keycloak + DB + Email + Transaction)
            UserMasterDto createdUser = userMasterService.createUserDetails(userMasterDto);

            // ✅ Return success response
            response.setSuccess(true);
            response.setData(createdUser);
            response.setMessage("User created successfully with Employee ID: " + createdUser.getEmployeeId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage(), e);
            
            // ✅ Better error messages based on exception type
            String errorMessage;
            if (e.getMessage().contains("No active license found")) {
                errorMessage = "No active license found. Please import a valid license first.";
            } else if (e.getMessage().contains("Company prefix not found")) {
                errorMessage = "Company prefix not found in license. Please check license details.";
            } else if (e.getMessage().contains("Keycloak version compatibility")) {
                errorMessage = "Keycloak version compatibility issue. Please contact administrator.";
            } else if (e.getMessage().contains("Cannot connect to Keycloak")) {
                errorMessage = "Cannot connect to Keycloak server. Please check server status.";
            } else if (e.getMessage().contains("Keycloak authentication failed")) {
                errorMessage = "Keycloak authentication failed. Please check admin credentials.";
            } else if (e.getMessage().contains("User already exists")) {
                errorMessage = "User with this email already exists in the system.";
            } else {
                errorMessage = "User creation failed: " + e.getMessage();
            }
            
            response.setSuccess(false);
            response.setMessage(errorMessage);
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @GetMapping("/getAll")
	public ResponseEntity<ApiResponse<List<UserMasterDto>>> getAllUserMasterDetails() {
		logger.info("Fetching all user details...");
		ApiResponse<List<UserMasterDto>> response = new ApiResponse<>();
		try {
			List<UserMasterDto> userMasterDtos = userMasterService.getAllUserDetails();
			response.setSuccess(true);
			response.setMessage("Fetched all users successfully");
			response.setData(userMasterDtos);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to fetch users: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getById/{id}")
	public ResponseEntity<ApiResponse<UserMasterDto>> getUserMasterDataByID(@PathVariable(name = "id") Long id) {
		logger.info("Fetching user by ID...");
		ApiResponse<UserMasterDto> response = new ApiResponse<>();
		try {
			UserMasterDto userMasterDto = userMasterService.findUserDetailsById(id);
			response.setSuccess(true);
			response.setMessage("User fetched successfully");
			response.setData(userMasterDto);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to fetch user: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

    // -------- UPDATE USER ------------
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserMasterDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UserMasterDto userMasterDto) {

        ApiResponse<UserMasterDto> response = new ApiResponse<>();
        try {
            // 1. Get existing user from DB (so we have Keycloak userId)
            UserMasterDto existingUser = userMasterService.findUserDetailsById(id);

            if (existingUser.getKeycloakUserId() != null) {
                // 2. Update user in Keycloak also
                keycloakUserService.updateKeycloakUser(
                        existingUser.getKeycloakUserId(),
                        userMasterDto.getUserEmailId(),
                        userMasterDto.getUserEmailId(),     // newEmail
                        userMasterDto.getUserFirstName(),   // newFirstName
                        userMasterDto.getUserLastName()  // newLastName
                );
            }

            // 3. Update user in DB
            UserMasterDto updatedUser = userMasterService.updateUserDetails(userMasterDto, id);

            response.setSuccess(true);
            response.setMessage("User updated successfully (Keycloak + DB)");
            response.setData(updatedUser);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating user", e);
            response.setSuccess(false);
            response.setMessage("Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserMasterData(@PathVariable Long id) {
        logger.info("Deleting user by ID...");
        ApiResponse<Void> response = new ApiResponse<>();
        try {
            UserMasterDto existingUser = userMasterService.findUserDetailsById(id);
            if (existingUser.getKeycloakUserId() != null) {
                keycloakUserService.deleteUser(existingUser.getKeycloakUserId());
            }
            userMasterService.deleteUserMasterData(id);

            response.setSuccess(true);
            response.setMessage("User data deleted successfully (Keycloak + DB)");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to delete user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


	@PostMapping("/upload-profile-image/{userId}")
	public ResponseEntity<ApiResponse<UserMasterDto>> uploadProfileImage(
			@PathVariable Long userId,
			@RequestParam("file") MultipartFile file) {
		ApiResponse<UserMasterDto> response = new ApiResponse<>();
		try {
			String imageUrl = userMasterService.uploadUserProfilePhoto(file, userId);
			UserMasterDto user = userMasterService.findUserDetailsById(userId);
			user.setProfileImageUrl(imageUrl);
			UserMasterDto updatedUser = userMasterService.updateUserDetails(user, userId);

			response.setSuccess(true);
			response.setMessage("Profile image uploaded successfully");
			response.setData(updatedUser);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to upload profile image: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// -------- COMPANY INFO & EMPLOYEE ID PREVIEW ------------
	
	@GetMapping("/company-info")
	public ResponseEntity<ApiResponse<String>> getCompanyInfo() {
		logger.info("Getting company information...");
		ApiResponse<String> response = new ApiResponse<>();
		
		try {
			String companyInfo = userMasterService.getCompanyInfo();
			response.setSuccess(true);
			response.setData(companyInfo);
			response.setMessage("Company information retrieved successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to get company information: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/preview-next-employee-id")
	public ResponseEntity<ApiResponse<String>> previewNextEmployeeId() {
		logger.info("Previewing next employee ID...");
		ApiResponse<String> response = new ApiResponse<>();
		
		try {
			String nextEmployeeId = userMasterService.previewNextEmployeeId();
			response.setSuccess(true);
			response.setData(nextEmployeeId);
			response.setMessage("Next employee ID preview generated successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage("Failed to preview next employee ID: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/by-ids")
	public ResponseEntity<ApiResponse<List<UserMasterDto>>> getUsersByIds(@RequestBody List<Long> userIds) {
		logger.info("Fetching users by IDs: {}", userIds);
		ApiResponse<List<UserMasterDto>> response = new ApiResponse<>();
		try {
			List<UserMasterDto> users = userMasterService.getUsersByIds(userIds);
			response.setSuccess(true);
			response.setMessage("Users fetched successfully");
			response.setData(users);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Failed to fetch users by IDs: {}", e.getMessage(), e);
			response.setSuccess(false);
			response.setMessage("Failed to fetch users: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
