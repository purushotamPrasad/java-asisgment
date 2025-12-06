package com.qssence.backend.authservice.service;

import com.qssence.backend.authservice.dto.request.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    RestTemplate restTemplate;

    private final Keycloak keycloak;
    @Autowired
    public AuthService(RestTemplate restTemplate, Keycloak keycloak) {
        this.restTemplate = restTemplate;
        this.keycloak = keycloak;
    }

    public ResponseEntity<String> login(UserRequest userRequest) {
  //     String tokenEndpoint = "https://auth.qssence.com/realms/authrealm/protocol/openid-connect/token";
         String tokenEndpoint = "http://localhost:8080/realms/authrealm/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
       map.add("client_id", "authclient");
//       map.add("client_secret", "NahPuomBpi2nQJWXwjpdRyRSCBgCFVoN");//server
         map.add("client_secret", "s0ZsE7dhT0UTAhL4XqAcbnf6kfH5adip");//local
        map.add("grant_type", "password");
        map.add("scope", "openid");
        map.add("username", userRequest.getEmail());
        map.add("password", userRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            return responseEntity;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    public ResponseEntity<String> logout(String logoutReq)
    {
        log.info(logoutReq.toString());
//        String tokenEndpoint = "https://auth.qssence.com/realms/authrealm/protocol/openid-connect/token";
        String tokenEndpoint = "http://localhost:8080/realms/authrealm/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "authclient");
//        map.add("client_secret", "NahPuomBpi2nQJWXwjpdRyRSCBgCFVoN");//server
        map.add("client_secret", "s0ZsE7dhT0UTAhL4XqAcbnf6kfH5adip");//local
        map.add("grant_type", "password");
        map.add("scope", "openid");
        map.add("id_token_hint", logoutReq);
        map.add("post_logout_redirect_uri", "https://google.com");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

//    public void createUser(UserRequest userRequest) {
//        UserRepresentation user = new UserRepresentation();
//        user.setUsername(userRequest.getEmail());
//        user.setEnabled(true);
//
//        // Set up user credentials
//        CredentialRepresentation credential = new CredentialRepresentation();
//        credential.setType(CredentialRepresentation.PASSWORD);
//        credential.setValue(userRequest.getPassword());
//        credential.setTemporary(false);
//        user.setCredentials(Collections.singletonList(credential));
//
//        keycloak.realm("QssenceRealm").users().create(user);
//    }
//    public ResponseEntity<UserRepresentation> getUserById(String userId) {
//        try {
//            UserRepresentation userRepresentation = keycloak.realm("QssenceRealm").users().get(userId).toRepresentation();
//            return ResponseEntity.ok(userRepresentation);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    public ResponseEntity<UserRepresentation> getUserByUsername(String userName) {
//        try {
//            List<UserRepresentation> users = keycloak.realm("QssenceRealm").users().search(userName);
//            if (!users.isEmpty()) {
//                return ResponseEntity.ok(users.get(0));
//            }
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<List<UserRepresentation>> getAllUsers() {
//        try {
//            List<UserRepresentation> users = keycloak.realm("QssenceRealm").users().list();
//            return ResponseEntity.ok(users);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<UserRepresentation> updateUserById(String userId, UserRequest userRequest) {
//        try {
//            UserRepresentation userToUpdate = keycloak.realm("QssenceRealm").users().get(userId).toRepresentation();
//            userToUpdate.setEmail(userRequest.getEmail());
//            keycloak.realm("QssenceRealm").users().get(userId).update(userToUpdate);
//            return ResponseEntity.ok(userToUpdate);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<UserRepresentation> updateUserByUsername(String userName, UserRequest userRequest) {
//        try {
//            List<UserRepresentation> users = keycloak.realm("QssenceRealm").users().search(userName);
//            if (!users.isEmpty()) {
//                return updateUserById(users.get(0).getId(), userRequest);
//            }
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<Void> deleteUserById(String userId) {
//        try {
//            keycloak.realm("QssenceRealm").users().get(userId).remove();
//            return ResponseEntity.noContent().build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ResponseEntity<Void> deleteUserByUsername(String username) {
//        try {
//            List<UserRepresentation> users = keycloak.realm("QssenceRealm").users().search(username);
//            if (!users.isEmpty()) {
//                keycloak.realm("QssenceRealm").users().get(users.get(0).getId()).remove();
//                return ResponseEntity.noContent().build();
//            }
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}

