package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.config.CompanySecurityPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for managing session timeouts and automatic logout
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionTimeoutService {
    
    private final SessionRegistry sessionRegistry;
    private final CompanySecurityPolicy securityPolicy;
    
    /**
     * Check and handle session timeouts
     */
    public void checkSessionTimeouts() {
        // Get all principals (users) from session registry
        List<Object> principals = sessionRegistry.getAllPrincipals();
        
        for (Object principal : principals) {
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
            
            for (SessionInformation session : sessions) {
                if (session.isExpired()) {
                    continue;
                }
                
                LocalDateTime lastRequest = LocalDateTime.now();
                long minutesSinceLastRequest = ChronoUnit.MINUTES.between(
                    session.getLastRequest().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                    lastRequest
                );
                
                // Check if session should be expired based on inactivity
                if (securityPolicy.isEnableAutoLogout() && 
                    minutesSinceLastRequest > securityPolicy.getInactivityTimeout()) {
                    
                    log.info("Session expired due to inactivity: {}", session.getSessionId());
                    session.expireNow();
                }
            }
        }
    }
    
    /**
     * Force logout all sessions for a user
     */
    public void forceLogoutUser(String username) {
        List<Object> principals = sessionRegistry.getAllPrincipals();
        
        for (Object principal : principals) {
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                org.springframework.security.core.userdetails.UserDetails userDetails = 
                    (org.springframework.security.core.userdetails.UserDetails) principal;
                
                if (userDetails.getUsername().equals(username)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation session : sessions) {
                        log.info("Force logging out user: {}", username);
                        session.expireNow();
                    }
                }
            }
        }
    }
    
    /**
     * Get active session count for a user
     */
    public int getActiveSessionCount(String username) {
        List<Object> principals = sessionRegistry.getAllPrincipals();
        int count = 0;
        
        for (Object principal : principals) {
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                org.springframework.security.core.userdetails.UserDetails userDetails = 
                    (org.springframework.security.core.userdetails.UserDetails) principal;
                
                if (userDetails.getUsername().equals(username)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation session : sessions) {
                        if (!session.isExpired()) {
                            count++;
                        }
                    }
                }
            }
        }
        
        return count;
    }
}
