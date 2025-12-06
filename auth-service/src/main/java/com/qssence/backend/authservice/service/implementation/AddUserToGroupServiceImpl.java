package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dbo.Group;
import com.qssence.backend.authservice.dbo.Status;
import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dto.ApiResponse;
import com.qssence.backend.authservice.dto.request.AddUserToGroupRequest;
import com.qssence.backend.authservice.dto.responce.AddUserToGroupResponse;
import com.qssence.backend.authservice.dto.responce.GroupInfo;
import com.qssence.backend.authservice.repository.GroupRepository;
import com.qssence.backend.authservice.repository.UserMasterRepository;
import com.qssence.backend.authservice.service.MailServices.SendMail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddUserToGroupServiceImpl implements IAddUserToGroupService {

    private final UserMasterRepository userMasterRepository;
    private final GroupRepository groupRepository;
    private final SendMail sendMail;

    @Override
    public ApiResponse<AddUserToGroupResponse> assignUserToGroups(AddUserToGroupRequest request) {
        UserMaster user = userMasterRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the groups corresponding to the provided groupIds
        Set<Group> groups = groupRepository.findAllById(request.getGroupIds()).stream().collect(Collectors.toSet());
        if (groups.isEmpty()) {
            throw new RuntimeException("No valid groups found");
        }

        // Prevent duplicate group assignments and set default status ACTIVE
        groups.forEach(group -> {
            if (user.getGroups().contains(group)) {
                throw new RuntimeException("Group " + group.getName() + " is already assigned to the user");
            }
            group.setStatus(String.valueOf(Status.ACTIVE)); // Automatically set status to ACTIVE
        });

        // Adding new groups
        user.getGroups().addAll(groups);
        user.setStatus(Status.ACTIVE);
        userMasterRepository.save(user);

        // ✅ Send email notification for group assignment
        try {
            Set<String> groupNames = groups.stream()
                    .map(Group::getName)
                    .collect(Collectors.toSet());
            
            System.out.println("📧 Sending group assignment email to: " + user.getEmailId() + " for groups: " + groupNames);
            
            sendMail.sendGroupAssignmentEmail(
                    user.getEmailId(),
                    user.getFirstName(),
                    user.getLastName(),
                    groupNames
            );
            
            System.out.println("✅ Group assignment email sent successfully to: " + user.getEmailId());
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("❌ Failed to send group assignment email to " + user.getEmailId() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return convertToResponse(user);
    }

    @Override
    public ApiResponse<AddUserToGroupResponse> getUserGroups(Long userId) {
        UserMaster user = userMasterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToResponse(user);
    }

    @Override
    public ApiResponse<String> updateUserGroupStatus(Long userId, Long groupId, Status status) {
        UserMaster user = userMasterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group groupToUpdate = user.getGroups().stream()
                .filter(group -> group.getGroupsId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found for this user"));

        // ✅ Update group status
        groupToUpdate.setStatus(status.name()); // Convert Enum to String
        userMasterRepository.save(user);

        return new ApiResponse<>(true, "Group status updated successfully", null);
    }


    @Override
    public ApiResponse<String> removeUserFromGroup(Long userId, Long groupId) {
        UserMaster user = userMasterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find the group to be removed
        Group groupToRemove = user.getGroups().stream()
                .filter(group -> group.getGroupsId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group not found for this user"));

        // Remove the group from the user's groups
        user.getGroups().remove(groupToRemove);

        // Save the updated user entity
        userMasterRepository.save(user);

        return new ApiResponse<>(true, "User removed from the group successfully", null);
    }


    private ApiResponse<AddUserToGroupResponse> convertToResponse(UserMaster user) {
        // GroupInfo objects ko map kar rahe hain jisme groupId aur groupName dono honge
        Set<GroupInfo> groups = user.getGroups().stream()
                .map(group -> GroupInfo.builder()
                        .groupId(group.getGroupsId())   // Assuming `getGroupsId()` is the method to fetch groupId
                        .groupName(group.getName())
                        .build())
                .collect(Collectors.toSet());

        AddUserToGroupResponse response = AddUserToGroupResponse.builder()
                .userId(user.getUserId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .groups(groups)  // ❗ groupNames ko groups se replace kiya gaya hai
                .status(user.getStatus().name())
                .build();

        return new ApiResponse<>(true, "User assigned to groups successfully", response);
    }



}
