# Keycloak Setup Guide for LDAP and SSO

## 1. LDAP/Active Directory Integration

### Step 1: Access Keycloak Admin Console
1. Open browser and go to `http://localhost:8080`
2. Login with admin credentials
3. Select your realm (authrealm)

### Step 2: Configure LDAP User Federation
1. Go to **User Federation** in the left menu
2. Click **Add provider** → **ldap**
3. Fill in the following configuration:

```
Connection URL: ldap://your-ldap-server:389
Users DN: ou=users,dc=company,dc=com
Bind DN: cn=admin,dc=company,dc=com
Bind Credential: [your-ldap-admin-password]
Connection Pooling: ON
Connection Timeout: 0
Read Timeout: 0
Pagination: ON
Batch Size for Sync: 1000
Full Sync Period: -1
Changed Sync Period: -1
```

### Step 3: LDAP Mapper Configuration
1. Go to **Mappers** tab
2. Add the following mappers:

**Username LDAP Attribute:**
- Name: username
- Mapper Type: user-attribute-ldap-mapper
- LDAP Attribute: sAMAccountName
- User Model Attribute: username

**First Name:**
- Name: firstName
- Mapper Type: user-attribute-ldap-mapper
- LDAP Attribute: givenName
- User Model Attribute: firstName

**Last Name:**
- Name: lastName
- Mapper Type: user-attribute-ldap-mapper
- LDAP Attribute: sn
- User Model Attribute: lastName

**Email:**
- Name: email
- Mapper Type: user-attribute-ldap-mapper
- LDAP Attribute: mail
- User Model Attribute: email

### Step 4: Test LDAP Connection
1. Click **Test connection** button
2. Click **Test authentication** button
3. Verify users are synced properly

## 2. SSO Configuration

### Step 1: Configure SSO Settings
1. Go to **Realm Settings** → **Sessions**
2. Configure the following:

```
SSO Session Idle Timeout: 30 minutes
SSO Session Max Lifespan: 8 hours
Client Session Idle Timeout: 30 minutes
Client Session Max Lifespan: 8 hours
Offline Session Idle Timeout: 30 days
```

### Step 2: Configure SSO for Multiple Applications
1. Go to **Clients** → **Create**
2. Create client for each application:

**Client Configuration:**
```
Client ID: [application-name]-client
Client Protocol: openid-connect
Access Type: confidential
Standard Flow Enabled: ON
Direct Access Grants Enabled: ON
Service Accounts Enabled: ON
Authorization Enabled: ON
```

**Valid Redirect URIs:**
```
http://localhost:3000/*
http://localhost:3001/*
https://your-domain.com/*
```

### Step 3: Configure SSO Mappers
1. Go to **Clients** → **[your-client]** → **Mappers**
2. Add the following mappers:

**Username:**
- Name: username
- Mapper Type: user-attribute-mapper
- User Attribute: username
- Token Claim Name: preferred_username
- Claim JSON Type: String

**Email:**
- Name: email
- Mapper Type: user-attribute-mapper
- User Attribute: email
- Token Claim Name: email
- Claim JSON Type: String

**Roles:**
- Name: realm roles
- Mapper Type: realm-role-mapper
- Token Claim Name: realm_access.roles
- Claim JSON Type: String

## 3. Multi-Client Deployment Setup

### Option 1: Single Keycloak Instance (Recommended)
**Advantages:**
- Single point of management
- Centralized user management
- Easy SSO across applications
- Cost-effective

**Setup:**
1. Create separate realms for each client
2. Configure realm-specific LDAP settings
3. Set up cross-realm SSO if needed

### Option 2: Multiple Keycloak Instances
**Advantages:**
- Complete isolation
- Client-specific configurations
- Independent scaling

**Setup:**
1. Deploy separate Keycloak instances
2. Configure each instance with client-specific settings
3. Set up federation between instances if needed

## 4. Automated Deployment Script

### Docker Compose for Multi-Client Setup
```yaml
version: '3.8'
services:
  keycloak:
    image: quay.io/keycloak/keycloak:latest
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: keycloak
    ports:
      - "8080:8080"
    volumes:
      - ./keycloak-config:/opt/keycloak/data/import
    command: start-dev --import-realm

  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: keycloak
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Keycloak Configuration Import Script
```bash
#!/bin/bash

# Import realm configuration
docker exec keycloak /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin

# Create realms for each client
for client in client1 client2 client3; do
    docker exec keycloak /opt/keycloak/bin/kcadm.sh create realms -s realm=$client -s enabled=true
    docker exec keycloak /opt/keycloak/bin/kcadm.sh create clients -r $client -s clientId=$client-client -s enabled=true
done
```

## 5. Client-Specific Configuration

### Environment Variables for Each Client
```yaml
# Client 1 Configuration
KEYCLOAK_REALM: client1
KEYCLOAK_CLIENT_ID: client1-client
KEYCLOAK_CLIENT_SECRET: [generated-secret]
KEYCLOAK_ISSUER_URI: http://keycloak:8080/realms/client1

# Client 2 Configuration  
KEYCLOAK_REALM: client2
KEYCLOAK_CLIENT_ID: client2-client
KEYCLOAK_CLIENT_SECRET: [generated-secret]
KEYCLOAK_ISSUER_URI: http://keycloak:8080/realms/client2
```

### Application Configuration
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: ${KEYCLOAK_CLIENT_ID}
            client-secret: ${KEYCLOAK_CLIENT_SECRET}
        provider:
          keycloak:
            issuer-uri: ${KEYCLOAK_ISSUER_URI}
```

## 6. Monitoring and Maintenance

### Health Checks
```bash
# Check Keycloak health
curl http://localhost:8080/health/ready

# Check realm status
curl http://localhost:8080/realms/client1/.well-known/openid_configuration
```

### Backup and Restore
```bash
# Export realm configuration
docker exec keycloak /opt/keycloak/bin/kcadm.sh get realms/client1 > client1-realm.json

# Import realm configuration
docker exec keycloak /opt/keycloak/bin/kcadm.sh create realms -f client1-realm.json
```

## 7. Security Best Practices

1. **Use HTTPS in production**
2. **Regular security updates**
3. **Monitor failed login attempts**
4. **Implement rate limiting**
5. **Regular backup of configurations**
6. **Use strong passwords for admin accounts**
7. **Enable audit logging**
8. **Implement proper network security**

## 8. Troubleshooting

### Common Issues:
1. **LDAP Connection Failed**: Check network connectivity and credentials
2. **SSO Not Working**: Verify redirect URIs and client configuration
3. **Session Timeout**: Check realm session settings
4. **User Sync Issues**: Verify LDAP mapper configuration

### Logs to Check:
- Keycloak server logs
- Application logs
- LDAP server logs
- Network connectivity logs
