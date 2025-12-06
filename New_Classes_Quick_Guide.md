# New Classes Quick Guide

## Overview
This document provides a quick overview of the 6 new classes created for the authentication system.

---

## 1. CompanySecurityPolicy.java
**Purpose:** Company security settings configuration
**Location:** `config/CompanySecurityPolicy.java`

**What it does:**
- Stores company security policies
- Controls browser password caching
- Manages SSO settings
- Sets session timeouts

**How it works:**
```java
// Loads from application.yml
company:
  security:
    allow-password-caching: false
    inactivity-timeout: 30
    enable-sso: true
```

**Key Properties:**
- `allowPasswordCaching` - Browser password saving
- `inactivityTimeout` - Auto logout time
- `enableSSO` - SSO functionality
- `adminSessionTimeout` - Admin session time

---

## 2. SessionTimeoutService.java
**Purpose:** Session management and timeout handling
**Location:** `service/SessionTimeoutService.java`

**What it does:**
- Checks session timeouts
- Forces user logout
- Monitors active sessions
- Handles inactivity logout

**How it works:**
```java
// Check session timeouts
sessionTimeoutService.checkSessionTimeouts();

// Force logout user
sessionTimeoutService.forceLogoutUser("user@example.com");

// Get active sessions
int count = sessionTimeoutService.getActiveSessionCount("user@example.com");
```

**Key Methods:**
- `checkSessionTimeouts()` - Auto logout after 30 minutes
- `forceLogoutUser()` - Admin can logout users
- `getActiveSessionCount()` - Track user sessions

---

## 3. AdminSecurityController.java
**Purpose:** Admin panel security management
**Location:** `controller/AdminSecurityController.java`

**What it does:**
- Manages security policies
- Controls user sessions
- Handles admin operations
- Updates security settings

**How it works:**
```java
// Get security policy
GET /api/v1/admin/security/policy

// Update security policy
PUT /api/v1/admin/security/policy

// Force logout user
POST /api/v1/admin/security/force-logout/{username}
```

**Key Endpoints:**
- `/policy` - Security policy management
- `/force-logout/{username}` - Force logout users
- `/sessions/{username}` - User session info
- `/check-timeouts` - Session timeout check

---

## 4. SSOConfigurationController.java
**Purpose:** SSO settings management
**Location:** `controller/SSOConfigurationController.java`

**What it does:**
- Manages SSO settings
- Configures SSO providers
- Handles SSO URLs
- Updates SSO configuration

**How it works:**
```java
// Get SSO settings
GET /api/v1/admin/sso/settings

// Update SSO settings
PUT /api/v1/admin/sso/settings
```

**Key Features:**
- SSO provider configuration
- SSO URL management
- Client ID settings
- Redirect URI management

---

## 5. HRSystemIntegrationService.java
**Purpose:** HR system integration
**Location:** `service/HRSystemIntegrationService.java`

**What it does:**
- Deactivates employee accounts
- Syncs user status with HR
- Handles employee deactivation
- Manages user status changes

**How it works:**
```java
// Deactivate employee
hrSystemIntegrationService.deactivateEmployeeAccount("employee@company.com");

// Sync user status
hrSystemIntegrationService.syncUserStatus("employee@company.com", "INACTIVE");
```

**Key Methods:**
- `deactivateEmployeeAccount()` - Disable employee account
- `syncUserStatus()` - Sync with HR system

---

## 6. Updated KeycloakUserService.java
**Purpose:** Enhanced user management
**Location:** `service/implementation/KeycloakUserService.java`

**What it does:**
- Enables/disables users
- Manages user accounts
- Handles user status changes
- Integrates with Keycloak

**How it works:**
```java
// Disable user
keycloakUserService.disableUser("user@example.com");

// Enable user
keycloakUserService.enableUser("user@example.com");
```

**New Methods:**
- `disableUser()` - Disable user account
- `enableUser()` - Enable user account

---

## Complete Workflow

### 1. User Login Flow
```
User Login → SecurityConfig → Session Created → SessionRegistry
```

### 2. Session Timeout Flow
```
SessionTimeoutService → Check Inactivity → Auto Logout
```

### 3. Admin Operations Flow
```
Admin Login → AdminSecurityController → Manage Security → Force Logout
```

### 4. HR Integration Flow
```
HR System → HRSystemIntegrationService → KeycloakUserService → User Disabled
```

### 5. SSO Configuration Flow
```
Admin Panel → SSOConfigurationController → Update Settings → Keycloak
```

---

## API Summary

| Class | Purpose | Key Methods | Endpoints |
|-------|---------|-------------|-----------|
| **CompanySecurityPolicy** | Security settings | Configuration | N/A |
| **SessionTimeoutService** | Session management | checkSessionTimeouts() | N/A |
| **AdminSecurityController** | Admin security | Policy management | 5 endpoints |
| **SSOConfigurationController** | SSO settings | SSO management | 2 endpoints |
| **HRSystemIntegrationService** | HR integration | Employee management | N/A |
| **KeycloakUserService** | User management | Enable/disable users | N/A |

---

## Quick Usage Examples

### Session Management
```java
@Autowired
private SessionTimeoutService sessionTimeoutService;

// Check timeouts
sessionTimeoutService.checkSessionTimeouts();

// Force logout
sessionTimeoutService.forceLogoutUser("user@example.com");
```

### Admin Security
```java
// Get security policy
GET /api/v1/admin/security/policy

// Update policy
PUT /api/v1/admin/security/policy
{
  "enableAutoLogout": true,
  "inactivityTimeout": 45
}
```

### HR Integration
```java
@Autowired
private HRSystemIntegrationService hrService;

// Deactivate employee
hrService.deactivateEmployeeAccount("employee@company.com");
```

### SSO Configuration
```java
// Get SSO settings
GET /api/v1/admin/sso/settings

// Update SSO settings
PUT /api/v1/admin/sso/settings
{
  "enableSSO": true,
  "ssoSessionTimeout": 240
}
```

---

## Configuration

### application.yml
```yaml
company:
  security:
    allow-password-caching: false
    inactivity-timeout: 30
    enable-sso: true
    admin-session-timeout: 60
```

### SecurityConfig.java
```java
.sessionManagement(session -> session
    .maximumSessions(1)
    .maxSessionsPreventsLogin(false)
    .sessionRegistry(sessionRegistry())
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    .invalidSessionUrl("/api/v1/auth/login?expired"))
```

---

## Summary

**6 New Classes Created:**
1. **CompanySecurityPolicy** - Security settings
2. **SessionTimeoutService** - Session management
3. **AdminSecurityController** - Admin security
4. **SSOConfigurationController** - SSO settings
5. **HRSystemIntegrationService** - HR integration
6. **KeycloakUserService** - Enhanced user management

**Total APIs:** 7 new endpoints
**Features:** Session management, HR integration, SSO configuration, Admin security
**Status:** Ready for production use

---

## End of Document