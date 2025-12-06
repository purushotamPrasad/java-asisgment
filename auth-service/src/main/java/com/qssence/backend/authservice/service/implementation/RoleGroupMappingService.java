package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dbo.RoleGroupMapping;
import com.qssence.backend.authservice.repository.RoleGroupMappingRepository;
import com.qssence.backend.authservice.service.IRoleGroupMappingService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleGroupMappingService implements IRoleGroupMappingService {

    @Autowired
    private RoleGroupMappingRepository roleGroupMappingRepository;

    @Override
    @Transactional
    public void assignRoleToGroup(String groupId, String roleId) {
        RoleGroupMapping roleGroupMapping = new RoleGroupMapping();
        roleGroupMapping.setGroupId(groupId);
        roleGroupMapping.setRoleId(roleId);
        roleGroupMappingRepository.save(roleGroupMapping);
    }

    public void removeRoleFromGroup(String groupId, String roleId) {
        try {
            RoleGroupMapping roleGroupMapping = roleGroupMappingRepository.findByGroupIdAndRoleId(groupId, roleId);
            if (roleGroupMapping != null) {
                roleGroupMappingRepository.delete(roleGroupMapping);
            } else {
                throw new NotFoundException("Role not found in group.");
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove role from group", e);
        }
    }

}
