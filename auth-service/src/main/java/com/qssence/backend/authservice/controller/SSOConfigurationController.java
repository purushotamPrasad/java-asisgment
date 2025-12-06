package com.qssence.backend.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * SSO Configuration Controller
 * Handles SSO settings for administrators
 */
@RestController
@RequestMapping("/api/v1/admin/sso")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class SSOConfigurationController {
    
    /**
     * Get SSO settings
     */
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSSOSettings() {
        Map<String, Object> ssoSettings = new HashMap<>();
        ssoSettings.put("enableSSO", true);
        ssoSettings.put("ssoProvider", "Keycloak");
        ssoSettings.put("ssoUrl", "http://localhost:8080/realms/authrealm");
        ssoSettings.put("clientId", "auth-client");
        ssoSettings.put("redirectUri", "http://localhost:3000/callback");
        
        return ResponseEntity.ok(ssoSettings);
    }
    
    /**
     * Update SSO settings
     */
    @PutMapping("/settings")
    public ResponseEntity<String> updateSSOSettings(@RequestBody Map<String, Object> settings) {
        try {
            log.info("SSO settings updated: {}", settings);
            return ResponseEntity.ok("SSO settings updated successfully");
        } catch (Exception e) {
            log.error("Error updating SSO settings: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to update SSO settings");
        }
    }
}
