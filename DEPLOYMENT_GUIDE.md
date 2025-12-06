# Multi-Client Deployment Guide

## 1. Deployment Architecture

### Option 1: Single Keycloak Instance (Recommended)
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client 1      │    │   Client 2      │    │   Client 3      │
│   Application   │    │   Application   │    │   Application   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Keycloak      │
                    │   (Single       │
                    │   Instance)     │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   LDAP/AD      │
                    │   Server       │
                    └─────────────────┘
```

### Option 2: Multiple Keycloak Instances
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client 1      │    │   Client 2      │    │   Client 3      │
│   Application   │    │   Application   │    │   Application   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Keycloak      │    │   Keycloak      │    │   Keycloak      │
│   Instance 1    │    │   Instance 2    │    │   Instance 3    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   LDAP/AD      │
                    │   Server       │
                    └─────────────────┘
```

## 2. Single Keycloak Instance Setup

### Step 1: Create Docker Compose for Production
```yaml
version: '3.8'
services:
  keycloak:
    image: quay.io/keycloak/keycloak:latest
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD}
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: ${KEYCLOAK_DB_PASSWORD}
      KC_HOSTNAME: ${KEYCLOAK_HOSTNAME}
      KC_HOSTNAME_PORT: 443
      KC_HTTP_ENABLED: false
      KC_HTTPS_CERTIFICATE_FILE: /opt/keycloak/conf/server.crt
      KC_HTTPS_CERTIFICATE_KEY_FILE: /opt/keycloak/conf/server.key
    ports:
      - "8080:8080"
      - "8443:8443"
    volumes:
      - ./keycloak-config:/opt/keycloak/data/import
      - ./ssl:/opt/keycloak/conf
    command: start --import-realm
    depends_on:
      - postgres

  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: ${KEYCLOAK_DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

volumes:
  postgres_data:
```

### Step 2: Environment Configuration
```bash
# .env file
KEYCLOAK_ADMIN_PASSWORD=your-secure-password
KEYCLOAK_DB_PASSWORD=your-db-password
KEYCLOAK_HOSTNAME=auth.yourdomain.com
```

### Step 3: SSL Certificate Setup
```bash
# Generate SSL certificate
openssl req -x509 -newkey rsa:4096 -keyout server.key -out server.crt -days 365 -nodes

# Copy certificates to ssl directory
cp server.crt ssl/
cp server.key ssl/
```

## 3. Automated Client Setup Script

### Step 1: Create Setup Script
```bash
#!/bin/bash
# setup-clients.sh

# Configuration
KEYCLOAK_URL="http://localhost:8080"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin"
MASTER_REALM="master"

# Function to create realm for client
create_realm() {
    local client_name=$1
    local ldap_url=$2
    local ldap_bind_dn=$3
    local ldap_bind_password=$4
    
    echo "Creating realm for client: $client_name"
    
    # Create realm
    docker exec keycloak /opt/keycloak/bin/kcadm.sh create realms \
        -s realm=$client_name \
        -s enabled=true \
        -s displayName="$client_name Realm" \
        -s loginWithEmailAllowed=true \
        -s duplicateEmailsAllowed=false \
        -s resetPasswordAllowed=true \
        -s editUsernameAllowed=false \
        -s rememberMe=true \
        -s verifyEmail=true \
        -s loginTheme=keycloak \
        -s accountTheme=keycloak \
        -s adminTheme=keycloak \
        -s emailTheme=keycloak \
        -s internationalizationEnabled=true \
        -s supportedLocales=en,es,fr,de \
        -s defaultLocale=en
        
    # Create client
    docker exec keycloak /opt/keycloak/bin/kcadm.sh create clients \
        -r $client_name \
        -s clientId=$client_name-client \
        -s enabled=true \
        -s clientAuthenticatorType=client-secret \
        -s secret=$(openssl rand -base64 32) \
        -s standardFlowEnabled=true \
        -s directAccessGrantsEnabled=true \
        -s serviceAccountsEnabled=true \
        -s authorizationServicesEnabled=true \
        -s 'redirectUris=["http://localhost:3000/*","https://'$client_name'.yourdomain.com/*"]' \
        -s 'webOrigins=["http://localhost:3000","https://'$client_name'.yourdomain.com"]'
        
    # Configure LDAP if provided
    if [ ! -z "$ldap_url" ]; then
        echo "Configuring LDAP for client: $client_name"
        
        # Create LDAP user federation
        docker exec keycloak /opt/keycloak/bin/kcadm.sh create components \
            -r $client_name \
            -s name=ldap \
            -s providerId=ldap \
            -s providerType=org.keycloak.storage.UserStorageProvider \
            -s 'config.connectionUrl=["'$ldap_url'"]' \
            -s 'config.usersDn=["'$ldap_bind_dn'"]' \
            -s 'config.bindDn=["'$ldap_bind_dn'"]' \
            -s 'config.bindCredential=["'$ldap_bind_password'"]' \
            -s 'config.authType=["simple"]' \
            -s 'config.searchScope=["1"]' \
            -s 'config.validatePasswordPolicy=["false"]' \
            -s 'config.trustEmail=["true"]' \
            -s 'config.useKerberos=["false"]' \
            -s 'config.connectionPooling=["true"]' \
            -s 'config.connectionTimeout=["0"]' \
            -s 'config.readTimeout=["0"]' \
            -s 'config.pagination=["true"]' \
            -s 'config.batchSizeForSync=["1000"]' \
            -s 'config.fullSyncPeriod=["-1"]' \
            -s 'config.changedSyncPeriod=["-1"]'
    fi
    
    echo "Realm $client_name created successfully"
}

# Main execution
echo "Setting up Keycloak for multiple clients..."

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to be ready..."
sleep 30

# Configure admin credentials
docker exec keycloak /opt/keycloak/bin/kcadm.sh config credentials \
    --server $KEYCLOAK_URL \
    --realm $MASTER_REALM \
    --user $ADMIN_USER \
    --password $ADMIN_PASSWORD

# Create realms for each client
create_realm "client1" "ldap://client1-ldap.company.com:389" "cn=admin,dc=client1,dc=company,dc=com" "client1-ldap-password"
create_realm "client2" "ldap://client2-ldap.company.com:389" "cn=admin,dc=client2,dc=company,dc=com" "client2-ldap-password"
create_realm "client3" "ldap://client3-ldap.company.com:389" "cn=admin,dc=client3,dc=company,dc=com" "client3-ldap-password"

echo "All clients configured successfully!"
```

### Step 2: Make Script Executable
```bash
chmod +x setup-clients.sh
./setup-clients.sh
```

## 4. Application Configuration for Each Client

### Step 1: Client 1 Configuration
```yaml
# client1-application.yml
spring:
  application:
    name: client1-auth-service
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: client1-client
            client-secret: ${CLIENT1_CLIENT_SECRET}
            scope: openid,profile,email
        provider:
          keycloak:
            issuer-uri: ${KEYCLOAK_ISSUER_URI}/realms/client1
            user-name-attribute: preferred_username
      resource-server:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI}/realms/client1

keycloak:
  realm: client1
  client-id: client1-client
  client-secret: ${CLIENT1_CLIENT_SECRET}
  issuer-uri: ${KEYCLOAK_ISSUER_URI}/realms/client1

company:
  security:
    allow-password-caching: false
    password-cache-timeout: 0
    enable-sso: true
    sso-session-timeout: 480
    enable-auto-logout: true
    inactivity-timeout: 30
```

### Step 2: Client 2 Configuration
```yaml
# client2-application.yml
spring:
  application:
    name: client2-auth-service
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: client2-client
            client-secret: ${CLIENT2_CLIENT_SECRET}
            scope: openid,profile,email
        provider:
          keycloak:
            issuer-uri: ${KEYCLOAK_ISSUER_URI}/realms/client2
            user-name-attribute: preferred_username
      resource-server:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI}/realms/client2

keycloak:
  realm: client2
  client-id: client2-client
  client-secret: ${CLIENT2_CLIENT_SECRET}
  issuer-uri: ${KEYCLOAK_ISSUER_URI}/realms/client2

company:
  security:
    allow-password-caching: true
    password-cache-timeout: 60
    enable-sso: true
    sso-session-timeout: 240
    enable-auto-logout: true
    inactivity-timeout: 15
```

## 5. Deployment Automation

### Step 1: Create Deployment Script
```bash
#!/bin/bash
# deploy-client.sh

CLIENT_NAME=$1
ENVIRONMENT=$2

if [ -z "$CLIENT_NAME" ] || [ -z "$ENVIRONMENT" ]; then
    echo "Usage: ./deploy-client.sh <client-name> <environment>"
    exit 1
fi

echo "Deploying $CLIENT_NAME to $ENVIRONMENT..."

# Build application
docker build -t $CLIENT_NAME-auth-service:latest .

# Deploy to environment
case $ENVIRONMENT in
    "dev")
        docker-compose -f docker-compose.dev.yml up -d
        ;;
    "staging")
        docker-compose -f docker-compose.staging.yml up -d
        ;;
    "production")
        docker-compose -f docker-compose.prod.yml up -d
        ;;
    *)
        echo "Invalid environment: $ENVIRONMENT"
        exit 1
        ;;
esac

echo "Deployment completed for $CLIENT_NAME in $ENVIRONMENT"
```

### Step 2: Environment-Specific Docker Compose Files

**docker-compose.dev.yml:**
```yaml
version: '3.8'
services:
  client1-auth-service:
    image: client1-auth-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - KEYCLOAK_ISSUER_URI=http://localhost:8080
      - CLIENT1_CLIENT_SECRET=${CLIENT1_CLIENT_SECRET}
    ports:
      - "8081:8082"
    depends_on:
      - keycloak

  client2-auth-service:
    image: client2-auth-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - KEYCLOAK_ISSUER_URI=http://localhost:8080
      - CLIENT2_CLIENT_SECRET=${CLIENT2_CLIENT_SECRET}
    ports:
      - "8082:8082"
    depends_on:
      - keycloak
```

**docker-compose.prod.yml:**
```yaml
version: '3.8'
services:
  client1-auth-service:
    image: client1-auth-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - KEYCLOAK_ISSUER_URI=https://auth.yourdomain.com
      - CLIENT1_CLIENT_SECRET=${CLIENT1_CLIENT_SECRET}
    ports:
      - "8081:8082"
    depends_on:
      - keycloak
    restart: unless-stopped

  client2-auth-service:
    image: client2-auth-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - KEYCLOAK_ISSUER_URI=https://auth.yourdomain.com
      - CLIENT2_CLIENT_SECRET=${CLIENT2_CLIENT_SECRET}
    ports:
      - "8082:8082"
    depends_on:
      - keycloak
    restart: unless-stopped
```

## 6. Monitoring and Maintenance

### Step 1: Health Check Script
```bash
#!/bin/bash
# health-check.sh

CLIENTS=("client1" "client2" "client3")
KEYCLOAK_URL="http://localhost:8080"

echo "Checking Keycloak health..."
curl -f $KEYCLOAK_URL/health/ready || echo "Keycloak is not healthy"

for client in "${CLIENTS[@]}"; do
    echo "Checking $client realm..."
    curl -f $KEYCLOAK_URL/realms/$client/.well-known/openid_configuration || echo "$client realm is not accessible"
done
```

### Step 2: Backup Script
```bash
#!/bin/bash
# backup-keycloak.sh

BACKUP_DIR="/backup/keycloak"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Backup realm configurations
for realm in client1 client2 client3; do
    echo "Backing up realm: $realm"
    docker exec keycloak /opt/keycloak/bin/kcadm.sh get realms/$realm > $BACKUP_DIR/${realm}_${DATE}.json
done

# Backup database
docker exec postgres pg_dump -U keycloak keycloak > $BACKUP_DIR/keycloak_db_${DATE}.sql

echo "Backup completed: $BACKUP_DIR"
```

## 7. Security Considerations

### Step 1: Network Security
```yaml
# docker-compose.security.yml
version: '3.8'
services:
  keycloak:
    networks:
      - keycloak-network
    environment:
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KC_HTTP_ENABLED=false
      - KC_HTTPS_CERTIFICATE_FILE=/opt/keycloak/conf/server.crt
      - KC_HTTPS_CERTIFICATE_KEY_FILE=/opt/keycloak/conf/server.key

networks:
  keycloak-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

### Step 2: Environment Variables Security
```bash
# .env.prod
KEYCLOAK_ADMIN_PASSWORD=$(openssl rand -base64 32)
KEYCLOAK_DB_PASSWORD=$(openssl rand -base64 32)
CLIENT1_CLIENT_SECRET=$(openssl rand -base64 32)
CLIENT2_CLIENT_SECRET=$(openssl rand -base64 32)
CLIENT3_CLIENT_SECRET=$(openssl rand -base64 32)
```

## 8. Troubleshooting

### Common Issues and Solutions:

1. **Realm Creation Failed**
   - Check Keycloak logs
   - Verify admin credentials
   - Ensure Keycloak is fully started

2. **LDAP Connection Failed**
   - Verify LDAP server connectivity
   - Check LDAP credentials
   - Test LDAP connection manually

3. **Client Authentication Failed**
   - Verify client secret
   - Check redirect URIs
   - Verify realm configuration

4. **SSO Not Working**
   - Check cross-realm configuration
   - Verify client configuration
   - Test token exchange

### Logs to Monitor:
- Keycloak server logs
- Application logs
- Database logs
- Network connectivity logs
