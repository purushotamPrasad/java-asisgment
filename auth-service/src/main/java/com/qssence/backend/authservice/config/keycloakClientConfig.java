package com.qssence.backend.authservice.config;


import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class keycloakClientConfig {

        @Bean
        public Keycloak keycloak() {
            return KeycloakBuilder.builder()
                   .serverUrl("https://auth.qssence.com")
                   .realm("authrealm")  // Assuming this is a fixed value and not read from YAML
                   .clientId("auth-client")
                //   .clientSecret("NahPuomBpi2nQJWXwjpdRyRSCBgCFVoN")
                    .clientSecret("JLE8Aae60DDBlGjMUn05OIqgmx1lCIcI")
                   .grantType("client_credentials")
                   .build();

//                    .serverUrl("http://localhost:8080")
//                    .realm("authrealm")  // Assuming this is a fixed value and not read from YAML
//                    .clientId("auth-client")
//                    .clientSecret("s0ZsE7dhT0UTAhL4XqAcbnf6kfH5adip")
//                    .grantType("client_credentials")
//                    .build();
        }
}

