package com.qssence.backend.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Company Security Policy Configuration
 * Controls browser password caching and other security policies
 */
@Configuration
@ConfigurationProperties(prefix = "company.security")
@Data
public class CompanySecurityPolicy {
    
    /**
     * Whether users are allowed to save passwords in browser cache
     */
    private boolean allowPasswordCaching = false;
    
    /**
     * Password cache timeout in minutes (0 = no caching)
     */
    private int passwordCacheTimeout = 0;
    
    /**
     * Whether to force password change on first login
     */
    private boolean forcePasswordChangeOnFirstLogin = true;
    
    /**
     * Maximum number of failed login attempts before account lockout
     */
    private int maxFailedLoginAttempts = 5;
    
    /**
     * Account lockout duration in minutes
     */
    private int accountLockoutDuration = 30;
    
    /**
     * Whether to enable SSO across applications
     */
    private boolean enableSSO = true;
    
    /**
     * SSO session timeout in minutes
     */
    private int ssoSessionTimeout = 480; // 8 hours
    
    /**
     * Whether to enable automatic logout on inactivity
     */
    private boolean enableAutoLogout = true;
    
    /**
     * Inactivity timeout in minutes
     */
    private int inactivityTimeout = 30;
    
    /**
     * Whether to enable admin session timeout
     */
    private boolean enableAdminSessionTimeout = true;
    
    /**
     * Admin session timeout in minutes
     */
    private int adminSessionTimeout = 60;
}
