package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dbo.Status;
import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dbo.UserRole;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.AddUserToRoleRequest;
import com.qssence.backend.authservice.dto.responce.AddUserToRoleResponse;
import com.qssence.backend.authservice.dto.responce.RoleInfo;
import com.qssence.backend.authservice.repository.UserMasterRepository;
import com.qssence.backend.authservice.repository.UserRoleRepository;
import com.qssence.backend.authservice.service.IAddUserToRoleService;
import com.qssence.backend.authservice.service.MailServices.SendMail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddUserToRoleServiceImpl implements IAddUserToRoleService {

    private final UserMasterRepository userMasterRepository;
    private final UserRoleRepository userRoleRepository;
    private final SendMail sendMail;

    @Override
    public ApiResponse<AddUserToRoleResponse> assignUserToRoles(AddUserToRoleRequest request) {
        UserMaster user = userMasterRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the groups corresponding to the provided groupIds
        Set<UserRole> roles = userRoleRepository.findAllById(request.getRoleIds()).stream().collect(Collectors.toSet());
        if (roles.isEmpty()) {
            throw new RuntimeException("No valid roles found");
        }

        // Prevent duplicate role assignments and set default status ACTIVE
        roles.forEach(userRole -> {
            if (user.getRole().contains(userRole)) {
                throw new RuntimeException("Group " + userRole.getUserRoleName() + " is already assigned to the user");
            }
            userRole.setStatus(String.valueOf(Status.ACTIVE)); // Automatically set status to ACTIVE
        });

        user.getRole().addAll(roles);
        user.setStatus(Status.ACTIVE);
        userMasterRepository.save(user);

        // ✅ Send email notification for role assignment
        try {
            Set<String> roleNames = roles.stream()
                    .map(role -> role.getUserRoleName())
                    .collect(Collectors.toSet());
            
            System.out.println("📧 Sending role assignment email to: " + user.getEmailId() + " for roles: " + roleNames);
            
            sendMail.sendRoleAssignmentEmail(
                    user.getEmailId(),
                    user.getFirstName(),
                    user.getLastName(),
                    roleNames
            );
            
            System.out.println("✅ Role assignment email sent successfully to: " + user.getEmailId());
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("❌ Failed to send role assignment email to " + user.getEmailId() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return convertToResponse(user, "User assigned to roles successfully");
    }

    @Override
    public ApiResponse<String> updateUserRoleStatus(Long userId, Long roleId, Status status) {
        UserMaster user = userMasterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRole roleToUpdate = user.getRole().stream()
                .filter(role -> role.getUserRoleId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found for this user"));

        // ✅ Update group status
        roleToUpdate.setStatus(status.name()); // Convert Enum to String
        userMasterRepository.save(user);

        return new ApiResponse<>(true, "Role status updated successfully", null);
    }


    @Override
    public ApiResponse<AddUserToRoleResponse> getUserRoles(Long userId) {
        UserMaster user = userMasterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToResponse(user,"Role fetch successfully");
    }

    @Override
    public ApiResponse<String> removeUserRole(Long userId, Long roleId) {
        UserMaster user = userMasterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find the group to be removed
        UserRole roleToRemove = user.getRole().stream()
                .filter(role -> role.getUserRoleId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found for this user"));

        // Remove the role from the user's roles
        user.getRole().remove(roleToRemove);

        // Save the updated user entity
        userMasterRepository.save(user);

        return new ApiResponse<>(true, "User removed from the Role successfully", null);
    }

//    private Set<UserRole> fetchRoles(Set<Long> roleIds) {
//        Set<UserRole> roles = userRoleRepository.findAllById(roleIds).stream().collect(Collectors.toSet());
//        if (roles.isEmpty()) {
//            throw new RuntimeException("No valid roles found");
//        }
//        return roles;
//    }

    private ApiResponse<AddUserToRoleResponse> convertToResponse(UserMaster user, String message) {
        Set<RoleInfo> roles = user.getRole().stream()
                .map(role -> RoleInfo.builder()
                        .roleId(role.getUserRoleId())  // Assuming getRoleId() fetches the role's ID
                        .roleName(role.getUserRoleName())
                        .status(role.getStatus())  // Ensure role.getStatus() returns a String like "ACTIVE" or "INACTIVE"
                        .build())
                .collect(Collectors.toSet());

        AddUserToRoleResponse response = AddUserToRoleResponse.builder()
                .userId(user.getUserId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .roles(roles)  // Updated to roles
                .status(user.getStatus().name())
                .build();

        return new ApiResponse<>(true, message, response);
    }

}
