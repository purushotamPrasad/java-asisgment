package com.qssence.backend.authservice.service.implementation;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.qssence.backend.authservice.dbo.Group;
import com.qssence.backend.authservice.dbo.Key.ImportedLicense;
import com.qssence.backend.authservice.dbo.Plants.Department;
import com.qssence.backend.authservice.dbo.Plants.Plant;
import com.qssence.backend.authservice.dbo.Plants.Section;
import com.qssence.backend.authservice.dbo.Status;
import com.qssence.backend.authservice.dbo.UserRole;
import com.qssence.backend.authservice.dto.responce.GroupResponseDto;
import com.qssence.backend.authservice.exception.PasswordGenerator;
import com.qssence.backend.authservice.repository.GroupRepository;
import com.qssence.backend.authservice.repository.Plants.DepartmentRepository;
import com.qssence.backend.authservice.repository.Plants.PlantRepository;
import com.qssence.backend.authservice.repository.Plants.SectionRepository;
import com.qssence.backend.authservice.repository.UserRoleRepository;
import com.qssence.backend.authservice.repository.key.ImportedLicenseRepository;
import com.qssence.backend.authservice.service.MailServices.SendMail;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dto.UserMasterDto;
import com.qssence.backend.authservice.exception.ResourceNotFoundException;
import com.qssence.backend.authservice.repository.UserMasterRepository;
import com.qssence.backend.authservice.service.UserMasterService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserMasterServiceImpl implements UserMasterService {

	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private SendMail sendMail;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private PlantRepository plantRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private SectionRepository sectionRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRoleRepository roleRepository;

    @Autowired
    private KeycloakUserService keycloakUserService;

	@Autowired
	private AmazonS3 amazonS3;

	@Value("${aws.s3.bucket}")
	private String bucketName;

    @Autowired
    private ImportedLicenseRepository licenseRepository;




    @Autowired
	public UserMasterServiceImpl(PasswordEncoder passwordEncoder, UserMasterRepository userMasterRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userMasterRepository = userMasterRepository;
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserMasterDto createUserDetails(UserMasterDto userMasterDto) throws MessagingException {
        String keycloakId = null;
        UserMaster newUser = null;
        
        try {
            // 1. Generate random password
            String generatedPassword = PasswordGenerator.generatePassword();

            // 2. License prefix fetch - Get active license FIRST
            ImportedLicense activeLicense = licenseRepository.findFirstByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active license found. Please import a valid license first."));
            String prefix = activeLicense.getCompanyPrefix();
            
            if (prefix == null || prefix.trim().isEmpty()) {
                throw new RuntimeException("Company prefix not found in active license. Please check license details.");
            }

            // 3. Generate next employeeId with company prefix
            String newEmployeeId = generateNextEmployeeId(prefix);

            // 4. Keycloak user create - ONLY if all validations pass
            keycloakId = keycloakUserService.createKeycloakUser(
                    userMasterDto.getUserEmailId(),
                    generatedPassword,
                    userMasterDto.getUserFirstName(),
                    userMasterDto.getUserLastName()
            );

            // 5. DTO -> Entity
            UserMaster userMaster = mapToEntity(userMasterDto);
            userMaster.setKeycloakId(keycloakId);
            userMaster.setStatus(Status.ACTIVE);
            userMaster.setEmployeeId(newEmployeeId);   // 👈 auto-generated employeeId with company prefix

            // 6. Save to database
            newUser = userMasterRepository.save(userMaster);

            // 7. Send email - ONLY if everything succeeds (with all user details)
            if (userMasterDto.getUserId() == null) {
                // Get groups and roles for email
                Set<String> groupNames = newUser.getGroups() != null 
                    ? newUser.getGroups().stream()
                        .map(Group::getName)
                        .collect(Collectors.toSet())
                    : new HashSet<>();
                
                Set<String> roleNames = newUser.getRole() != null
                    ? newUser.getRole().stream()
                        .map(UserRole::getUserRoleName)
                        .collect(Collectors.toSet())
                    : new HashSet<>();
                
                // Send enhanced email with employeeId, groups, and roles
                sendMail.sendUserEmail(
                        newUser.getEmailId(),
                        newUser.getFirstName(),
                        newUser.getLastName(),
                        generatedPassword,
                        newUser.getEmployeeId(),
                        groupNames,
                        roleNames
                );
            }

            return mapToDto(newUser);
            
        } catch (Exception e) {
            // ✅ If any error occurs, clean up Keycloak user if it was created
            if (keycloakId != null) {
                try {
                    keycloakUserService.deleteUser(keycloakId);
                } catch (Exception cleanupException) {
                    // Log cleanup failure but don't throw - main error is more important
                    System.err.println("Failed to cleanup Keycloak user: " + cleanupException.getMessage());
                }
            }
            
            // ✅ Re-throw with better error message
            throw new RuntimeException("User creation failed: " + e.getMessage(), e);
        }
    }






    @Override
	public UserMasterDto findUserDetailsById(Long id) {
		UserMaster userMaster = userMasterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

		// Convert UserMaster entity to UserMasterDto
		return mapToDto(userMaster);
	}


	@Override
	public List<UserMasterDto> getAllUserDetails() {
		try {
			// Fetch all user details
			List<UserMaster> users = userMasterRepository.findAll();

			// Check if the list is empty or null
			if (users == null || users.isEmpty()) {
				System.out.println("No users found!");
			}

			// Map each user to DTO
			return users.stream()
					.map(userMaster -> mapToDto(userMaster))
					.collect(Collectors.toList());
		} catch (Exception e) {
			System.err.println("Error in getAllUserDetails method: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch user details", e);
		}
	}


	private UserMaster mapToEntity(UserMasterDto dto) {
		System.out.println("UserPlantId: " + dto.getUserPlantId());
		System.out.println("UserDepartmentId: " + dto.getUserDepartmentId());
		System.out.println("UserSectionId: " + dto.getUserSectionId());
		return UserMaster.builder()
				.userId(dto.getUserId())
				.firstName(dto.getUserFirstName())
				.middleName(dto.getUserMiddleName())
				.lastName(dto.getUserLastName())
				.profileImageUrl(dto.getProfileImageUrl())
				.mobileNumber(dto.getUserMobileNumber())
				.emailId(dto.getUserEmailId())
				.address(dto.getUserAddress())
				.region(dto.getRegion())
				.country(dto.getCountry())
				// Plant lookup based on the plantId in DTO
				.plant(plantRepository.findById(dto.getUserPlantId())
						.orElseThrow(() -> new ResourceNotFoundException("Plant not found")))
				// Department lookup based on the departmentId in DTO
				.department(departmentRepository.findById(dto.getUserDepartmentId())
						.orElseThrow(() -> new ResourceNotFoundException("Department not found")))
				// Section lookup based on the sectionId in DTO
				.section(sectionRepository.findById(dto.getUserSectionId())
						.orElseThrow(() -> new ResourceNotFoundException("Section not found")))
				// Mapping groups to the set of groups in the database
				.groups(dto.getGroupIds().stream()
						.map(groupId -> groupRepository.findById(groupId)
								.orElseThrow(() -> new ResourceNotFoundException("Group not found")))
						.collect(Collectors.toSet()))
				// Mapping roles to the set of roles in the database
				.role(dto.getRoleIds().stream()
						.map(roleId -> roleRepository.findById(roleId)
								.orElseThrow(() -> new ResourceNotFoundException("Role not found")))
						.collect(Collectors.toSet()))
				.employeeId(dto.getEmployeeId())
				.location(dto.getLocation())
				.designation(dto.getDesignation())
//				.applicationAccess(dto.getUserApplicationAccess())
//				.status(dto.getStatus()) // New field for user status (active/inactive)
				.timeZone(dto.getTimeZone()) // Timezone field mapping
				.createdAt(dto.getCreatedAt()) // Created date from DTO
                .keycloakId(dto.getKeycloakUserId())   // 👈 Add this
                .build();
	}
	


	private UserMasterDto mapToDto(UserMaster user) {
		return UserMasterDto.builder()
				.userId(user.getUserId())
				.userFirstName(user.getFirstName())
				.userMiddleName(user.getMiddleName())
				.userLastName(user.getLastName())
				.userMobileNumber(user.getMobileNumber())
				.profileImageUrl(user.getProfileImageUrl())				.userEmailId(user.getEmailId())
				.userAddress(user.getAddress())
				.region(user.getRegion())
				.country(user.getCountry())
				.userPlantId(user.getPlant() != null ? user.getPlant().getId() : null)
				.userDepartmentId(user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null)
				.userSectionId(user.getSection() != null ? user.getSection().getSectionId() : null)
				.groupIds(user.getGroups().stream().map(Group::getGroupsId).collect(Collectors.toSet()))
				.roleIds(user.getRole().stream().map(UserRole::getUserRoleId).collect(Collectors.toSet()))
				.employeeId(user.getEmployeeId())
				.status(user.getStatus().name())
				.timeZone(user.getTimeZone())
				.location(user.getLocation())
//				.password(user.getPassword())
				.createdAt(user.getCreatedAt())
				.designation(user.getDesignation())
                .keycloakUserId(user.getKeycloakId())
				.build();
	}

	@Override
	public UserMasterDto getUserDetailsById(Long id) {
		UserMaster userMaster = userMasterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("UserMaster", "id", id));
		return mapToDto(userMaster);
	}

	@Override
	public List<UserMasterDto> getUsersByIds(List<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return List.of();
		}
		List<UserMaster> users = userMasterRepository.findAllById(userIds);
		return users.stream()
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}

	@Override
	public UserMasterDto updateUserDetails(UserMasterDto userMasterDto, Long id) {
		// Fetch the existing user
		UserMaster userMaster = userMasterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		// Update simple fields
		userMaster.setFirstName(userMasterDto.getUserFirstName());
		userMaster.setMiddleName(userMasterDto.getUserMiddleName());
		userMaster.setLastName(userMasterDto.getUserLastName());
		userMaster.setMobileNumber(userMasterDto.getUserMobileNumber());
		userMaster.setProfileImageUrl(userMasterDto.getProfileImageUrl());
		userMaster.setEmailId(userMasterDto.getUserEmailId());
		userMaster.setAddress(userMasterDto.getUserAddress());
		userMaster.setRegion(userMaster.getRegion());
		userMaster.setCountry(userMasterDto.getCountry());  // Corrected this line to use userMasterDto

		if (userMasterDto.getUserPlantId() != null) {
			Plant plant = plantRepository.findById(userMasterDto.getUserPlantId())
					.orElseThrow(() -> new ResourceNotFoundException("Plant", "id", userMasterDto.getUserPlantId()));
			userMaster.setPlant(plant);
		}
		if (userMasterDto.getUserDepartmentId() != null) {
			Department department = departmentRepository.findById(userMasterDto.getUserDepartmentId())
					.orElseThrow(() -> new ResourceNotFoundException("Department", "id", userMasterDto.getUserDepartmentId()));
			userMaster.setDepartment(department);
		}
		if (userMasterDto.getUserSectionId() != null) {
			Section section = sectionRepository.findById(userMasterDto.getUserSectionId())
					.orElseThrow(() -> new ResourceNotFoundException("Section", "id", userMasterDto.getUserSectionId()));
			userMaster.setSection(section);
		}

		// Update groups (many-to-many)
		if (userMasterDto.getGroupIds() != null) {
			Set<Group> groups = new HashSet<>();
			for (Long groupsId : userMasterDto.getGroupIds()) {
				Group group = groupRepository.findById(groupsId)
						.orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupsId));
				groups.add(group);
			}
			userMaster.setGroups(groups);
		}

		// Update roles (many-to-many)
		if (userMasterDto.getRoleIds() != null) {
			Set<UserRole> roles = new HashSet<>();
			for (Long roleId : userMasterDto.getRoleIds()) {
				UserRole role = roleRepository.findById(roleId)
						.orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
				roles.add(role);
			}
			userMaster.setRole(roles);
		}

		userMaster.setEmployeeId(userMasterDto.getEmployeeId());
		userMaster.setDesignation(userMasterDto.getDesignation());

		// Update status if provided
		if (userMasterDto.getStatus() != null && !userMasterDto.getStatus().isEmpty()) {
			try {
				userMaster.setStatus(Status.valueOf(userMasterDto.getStatus().toUpperCase()));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid status value: " + userMasterDto.getStatus());
			}
		}

		// Save the updated user
		UserMaster updatedUserMaster = userMasterRepository.save(userMaster);
		return mapToDto(updatedUserMaster);
	}


	//REST API for remove data from userMaster
	@Override
	public void deleteUserMasterData(Long id) {

		UserMaster userMaster = userMasterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		userMasterRepository.delete(userMaster);

	}


	public UserMaster assignPlantAndDivision(UserMasterDto userDto) {
		UserMaster user = userMasterRepository.findById(userDto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Fetch the plant and division by their IDs
		Plant plant = plantRepository.findById(Long.valueOf(userDto.getUserPlantId()))
				.orElseThrow(() -> new RuntimeException("Plant not found"));

		Department department = departmentRepository.findById(Long.valueOf(userDto.getUserDepartmentId()))
				.orElseThrow(() -> new RuntimeException("Division not found"));

		// Assign plant and division to the user
		user.setPlant(plant);
		user.setDepartment(department);

		// Save the updated user details
		return userMasterRepository.save(user);
	}



	public String uploadUserProfilePhoto(MultipartFile file, Long userId) throws IOException {
	String key = "user-profile-photos/" + userId + "/" + file.getOriginalFilename();
	amazonS3.putObject(bucketName, key, file.getInputStream(), new ObjectMetadata());
	return amazonS3.getUrl(bucketName, key).toString();
 	}

    private String generateNextEmployeeId(String prefix) {
        // Validate prefix
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Company prefix cannot be null or empty");
        }
        
        String normalizedPrefix = prefix.trim().toUpperCase();
        
        // Find the highest employee ID with this prefix
        String lastEmployeeId = userMasterRepository.findLastEmployeeIdByPrefix(normalizedPrefix);
        
        if (lastEmployeeId == null) {
            return normalizedPrefix + "001";  // First employee: QSS001
        }

        // Extract number part from last employee ID
        // Example: lastEmployeeId = "QSS007" -> numberPart = "007"
        String numberPart = lastEmployeeId.replace(normalizedPrefix, ""); 
        int nextNumber = Integer.parseInt(numberPart) + 1;

        // Format: QSS001, QSS002, QSS003, etc.
        return String.format("%s%03d", normalizedPrefix, nextNumber);
    }

    // Helper method to get company info for debugging
    public String getCompanyInfo() {
        try {
            ImportedLicense activeLicense = licenseRepository.findFirstByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active license found"));
            
            String prefix = activeLicense.getCompanyPrefix();
            Long employeeCount = userMasterRepository.countEmployeesByPrefix(prefix);
            
            return String.format("Company: %s, Prefix: %s, Employee Count: %d", 
                    activeLicense.getCompanyName(), prefix, employeeCount);
        } catch (Exception e) {
            return "No active license found: " + e.getMessage();
        }
    }

    // Test method to preview next employee ID without creating user
    public String previewNextEmployeeId() {
        try {
            ImportedLicense activeLicense = licenseRepository.findFirstByActiveTrue()
                    .orElseThrow(() -> new RuntimeException("No active license found"));
            
            String prefix = activeLicense.getCompanyPrefix();
            return generateNextEmployeeId(prefix);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


}