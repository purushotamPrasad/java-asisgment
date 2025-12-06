package com.qssence.backend.authservice.Validator;

import com.qssence.backend.authservice.enums.PermissionAccessLevel;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PermissionAccessLevelValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PermissionAccessLevel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PermissionAccessLevel permissionAccessLevel = (PermissionAccessLevel) target;

        // Perform validation checks
        if (!permissionAccessLevel.isCanCreate() && !permissionAccessLevel.isCanRead() && !permissionAccessLevel.isCanUpdate() && !permissionAccessLevel.isCanDelete()) {
            errors.reject("accessLevels", "At least one access level must be true");
        }
    }
}