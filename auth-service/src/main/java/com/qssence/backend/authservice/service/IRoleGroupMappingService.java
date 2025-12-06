package com.qssence.backend.authservice.service;

public interface IRoleGroupMappingService {
    void assignRoleToGroup(String groupId, String roleId);
}
