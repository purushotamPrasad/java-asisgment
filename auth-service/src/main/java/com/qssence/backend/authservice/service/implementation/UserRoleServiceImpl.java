package com.qssence.backend.authservice.service.implementation;
import java.util.List;
import java.util.stream.Collectors;

import com.qssence.backend.authservice.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qssence.backend.authservice.dbo.UserRole;
import com.qssence.backend.authservice.dto.UserRoleDto;
import com.qssence.backend.authservice.exception.ResourceNotFoundException;
import com.qssence.backend.authservice.repository.UserRoleRepository;
import com.qssence.backend.authservice.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private PermissionRepository permissionRepository;


	@Override
	public UserRoleDto createUserRole(UserRoleDto userRoleDto) {

		UserRole userRole = mapToEntity(userRoleDto);
		// ✅ Set default status to ACTIVE if not provided
		if (userRole.getStatus() == null || userRole.getStatus().trim().isEmpty()) {
			userRole.setStatus("ACTIVE");
		}
		UserRole newUserRole = userRoleRepository.save(userRole);
		UserRoleDto userRoleResponse = mapToDto(newUserRole);
		return userRoleResponse;
	}

	@Override
	public UserRoleDto findByUserRoleName(String roleName) {
		UserRole userRole = userRoleRepository.findByUserRoleName(roleName);
		return userRole != null ? mapToDto(userRole) : null;
	}

	@Override
	public List<UserRoleDto> getAllUserRole() {

		List<UserRole> userRoles = userRoleRepository.findAll();
		return userRoles.stream().map(userRole -> mapToDto(userRole)).collect(Collectors.toList());

	}

	@Override
	public UserRoleDto getUserRoleByid(Long id) {
		UserRole userRole = userRoleRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("UserRole", "id", id));
		return mapToDto(userRole);
	}


	@Override
	public UserRoleDto updateUserRoleDetails(UserRoleDto userRoleDto ,Long id) {
		UserRole userRole = userRoleRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("UserRole", "id", id));
		
		// Update name if provided
		if (userRoleDto.getUserRoleName() != null && !userRoleDto.getUserRoleName().trim().isEmpty()) {
			userRole.setUserRoleName(userRoleDto.getUserRoleName());
		}
		
		// Update description if provided
		if (userRoleDto.getDescription() != null) {
			userRole.setDescription(userRoleDto.getDescription());
		}
		
		// ✅ Update status if provided (ACTIVE or INACTIVE)
		if (userRoleDto.getStatus() != null && !userRoleDto.getStatus().trim().isEmpty()) {
			String status = userRoleDto.getStatus().toUpperCase();
			if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
				userRole.setStatus(status);
			} else {
				throw new IllegalArgumentException("Status must be either 'ACTIVE' or 'INACTIVE'");
			}
		}
		
		UserRole updateUserRoleData = userRoleRepository.save(userRole);
		return mapToDto(updateUserRoleData);
	}


	@Override
	public void deleteUserRole(Long id) {
		UserRole userRole = userRoleRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("UserRole", "id", id));
		userRoleRepository.delete(userRole);

	}

	//convert entity to DTO


	private UserRoleDto mapToDto(UserRole userRole) {
		UserRoleDto userRoleDto = new UserRoleDto();

		userRoleDto.setUserRoleId(userRole.getUserRoleId());
		userRoleDto.setUserRoleName(userRole.getUserRoleName());
		userRoleDto.setDescription(userRole.getDescription());
		userRoleDto.setStatus(userRole.getStatus()); // ✅ Include status in response
		return userRoleDto;
	}

	// Convert UserRoleDto to UserRole entity
	private UserRole mapToEntity(UserRoleDto userRoleDto) {
		UserRole userRole = new UserRole();

		userRole.setUserRoleId(userRoleDto.getUserRoleId());
		userRole.setUserRoleName(userRoleDto.getUserRoleName());
		userRole.setDescription(userRoleDto.getDescription());
		userRole.setStatus(userRoleDto.getStatus()); // ✅ Map status from DTO to entity
		return userRole;
	}


}
