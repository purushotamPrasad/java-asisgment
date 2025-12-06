package com.qssence.backend.authservice.service;

import java.util.List;

import com.qssence.backend.authservice.dto.UserRoleDto;

public interface UserRoleService {

	public UserRoleDto createUserRole(UserRoleDto userRoleDto);
	public List<UserRoleDto>getAllUserRole();
	public UserRoleDto getUserRoleByid(Long id);
	public UserRoleDto updateUserRoleDetails(UserRoleDto userRoleDto, Long id);
	void deleteUserRole(Long id);

	public UserRoleDto findByUserRoleName(String roleName);

}
