package com.qssence.backend.authservice.service.implementation;
import com.qssence.backend.authservice.dbo.UserRoleMapping;
import com.qssence.backend.authservice.repository.UserRoleMappingRepository;
import com.qssence.backend.authservice.service.IUserRoleMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleMappingService implements IUserRoleMappingService  {

    @Autowired
    private UserRoleMappingRepository userRoleMappingRepository;

    @Override
//    @Transactional
    public UserRoleMapping assignRoleToUser(String roleId, String userId) {
        UserRoleMapping userRoleMapping = new UserRoleMapping();
        userRoleMapping.setRoleId(roleId);
        userRoleMapping.setUserId(userId);
        userRoleMappingRepository.save(userRoleMapping);
        return userRoleMapping;
    }


}
