package com.qssence.backend.authservice.controller;

import com.qssence.backend.authservice.config.CompanySecurityPolicy;
import com.qssence.backend.authservice.service.SessionTimeoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin Security Controller
 * Handles admin-specific security configurations and operations
 */
@RestController
@RequestMapping("/api/v1/admin/security")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminSecurityController {
    
    private final CompanySecurityPolicy securityPolicy;
    private final SessionTimeoutService sessionTimeoutService;
    
    /**
     * Get current security policy configuration
     */
    @GetMapping("/policy")
    public ResponseEntity<Map<String, Object>> getSecurityPolicy() {
        Map<String, Object> policy = new HashMap<>();
        policy.put("allowPasswordCaching", securityPolicy.isAllowPasswordCaching());
        policy.put("passwordCacheTimeout", securityPolicy.getPasswordCacheTimeout());
        policy.put("forcePasswordChangeOnFirstLogin", securityPolicy.isForcePasswordChangeOnFirstLogin());
        policy.put("maxFailedLoginAttempts", securityPolicy.getMaxFailedLoginAttempts());
        policy.put("accountLockoutDuration", securityPolicy.getAccountLockoutDuration());
        policy.put("enableSSO", securityPolicy.isEnableSSO());
        policy.put("ssoSessionTimeout", securityPolicy.getSsoSessionTimeout());
        policy.put("enableAutoLogout", securityPolicy.isEnableAutoLogout());
        policy.put("inactivityTimeout", securityPolicy.getInactivityTimeout());
        policy.put("enableAdminSessionTimeout", securityPolicy.isEnableAdminSessionTimeout());
        policy.put("adminSessionTimeout", securityPolicy.getAdminSessionTimeout());
        
        return ResponseEntity.ok(policy);
    }
    
    /**
     * Update security policy configuration
     */
    @PutMapping("/policy")
    public ResponseEntity<String> updateSecurityPolicy(@RequestBody Map<String, Object> policyUpdates) {
        try {
            if (policyUpdates.containsKey("allowPasswordCaching")) {
                securityPolicy.setAllowPasswordCaching((Boolean) policyUpdates.get("allowPasswordCaching"));
            }
            if (policyUpdates.containsKey("passwordCacheTimeout")) {
                securityPolicy.setPasswordCacheTimeout((Integer) policyUpdates.get("passwordCacheTimeout"));
            }
            if (policyUpdates.containsKey("enableSSO")) {
                securityPolicy.setEnableSSO((Boolean) policyUpdates.get("enableSSO"));
            }
            if (policyUpdates.containsKey("ssoSessionTimeout")) {
                securityPolicy.setSsoSessionTimeout((Integer) policyUpdates.get("ssoSessionTimeout"));
            }
            if (policyUpdates.containsKey("enableAutoLogout")) {
                securityPolicy.setEnableAutoLogout((Boolean) policyUpdates.get("enableAutoLogout"));
            }
            if (policyUpdates.containsKey("inactivityTimeout")) {
                securityPolicy.setInactivityTimeout((Integer) policyUpdates.get("inactivityTimeout"));
            }
            
            log.info("Security policy updated by admin");
            return ResponseEntity.ok("Security policy updated successfully");
            
        } catch (Exception e) {
            log.error("Error updating security policy: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to update security policy");
        }
    }
    
    /**
     * Force logout a specific user
     */
    @PostMapping("/force-logout/{username}")
    public ResponseEntity<String> forceLogoutUser(@PathVariable String username) {
        try {
            sessionTimeoutService.forceLogoutUser(username);
            log.info("User {} force logged out by admin", username);
            return ResponseEntity.ok("User logged out successfully");
        } catch (Exception e) {
            log.error("Error force logging out user {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body("Failed to logout user");
        }
    }
    
    /**
     * Get active session count for a user
     */
    @GetMapping("/sessions/{username}")
    public ResponseEntity<Map<String, Object>> getUserSessionInfo(@PathVariable String username) {
        try {
            int activeSessions = sessionTimeoutService.getActiveSessionCount(username);
            Map<String, Object> sessionInfo = new HashMap<>();
            sessionInfo.put("username", username);
            sessionInfo.put("activeSessions", activeSessions);
            
            return ResponseEntity.ok(sessionInfo);
        } catch (Exception e) {
            log.error("Error getting session info for user {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to get session info"));
        }
    }
    
    /**
     * Check and handle session timeouts
     */
    @PostMapping("/check-timeouts")
    public ResponseEntity<String> checkSessionTimeouts() {
        try {
            sessionTimeoutService.checkSessionTimeouts();
            return ResponseEntity.ok("Session timeout check completed");
        } catch (Exception e) {
            log.error("Error checking session timeouts: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to check session timeouts");
        }
    }
}
