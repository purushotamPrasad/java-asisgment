//package com.qssence.backend.authservice.service;
//
//import com.qssence.backend.authservice.service.implementation.KeycloakUserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
///**
// * HR System Integration Service
// * Handles employee deactivation and HR system sync
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class HRSystemIntegrationService {
//
//    private final KeycloakUserService keycloakUserService;
//
//    /**
//     * Deactivate employee account when relieved from organization
//     */
//    public void deactivateEmployeeAccount(String email) {
//        try {
//            log.info("Deactivating employee account: {}", email);
//
//            // Disable user in Keycloak
//            keycloakUserService.disableUser(email);
//
//            // Update user status in database
//            // This would typically update your UserMaster table
//            log.info("Employee account deactivated successfully: {}", email);
//
//        } catch (Exception e) {
//            log.error("Error deactivating employee account {}: {}", email, e.getMessage());
//        }
//    }
//
//    /**
//     * Sync user status with HR system
//     */
//    public void syncUserStatus(String email, String status) {
//        try {
//            log.info("Syncing user status for {}: {}", email, status);
//
//            if ("INACTIVE".equals(status)) {
//                deactivateEmployeeAccount(email);
//            } else if ("ACTIVE".equals(status)) {
//                // Reactivate user if needed
//                keycloakUserService.enableUser(email);
//            }
//
//        } catch (Exception e) {
//            log.error("Error syncing user status for {}: {}", email, e.getMessage());
//        }
//    }
//}
