package com.qssence.backend.authservice.service.implementation;

import com.qssence.backend.authservice.dto.request.LoginRequest;
import com.qssence.backend.authservice.dto.request.LogoutReq;
import com.qssence.backend.authservice.service.IAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * A service for handling user authentication and login functionality.
 * Implements the {@link IAuthService} interface.
 * @author ayushtamrakar
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService implements IAuthService {


    @Value("${authService.tokenEndpoint}")
    private String tokenEndpoint;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.keycloak.scope}")
    private String scope;

    private final RestTemplate restTemplate;

    /**
     * Authenticates a user by verifying the provided credentials in {@link LoginRequest}.
     *
     * @param loginRequest The username of the user attempting to log in.
     * @return Something not yet decided
     */
    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "password");
        map.add("scope", scope);
        map.add("username", loginRequest.getUsername());
        map.add("password", loginRequest.getPassword());


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        log.info(request.toString());
        try {
            ResponseEntity<?> responseEntity = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            log.info(responseEntity.toString());

            // Check if the response indicates success (e.g., HTTP status code 2xx)
            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                return responseEntity;
            } else {
                // Handle error cases based on different HTTP status codes
                HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
                if (statusCode == HttpStatus.UNAUTHORIZED) {
                    // Handle unauthorized access (e.g., invalid credentials)
                    return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
                } else if (statusCode == HttpStatus.NOT_FOUND) {
                    // Handle user not found error
                    return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
                } else {
                    // Handle other error cases gracefully
                    return new ResponseEntity<>("An error occurred", statusCode);
                }
            }
        } catch (HttpClientErrorException ex) {
            // Handle HTTP client errors (e.g., 4xx errors)
            HttpStatus statusCode = (HttpStatus) ex.getStatusCode();
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                // Handle unauthorized access (e.g., invalid credentials)
                return new ResponseEntity<>("Unauthorized access", HttpStatus.UNAUTHORIZED);
            } else if (statusCode == HttpStatus.NOT_FOUND) {
                // Handle user not found error
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            } else {
                // Handle other client errors gracefully
                return new ResponseEntity<>("Client error occurred :: "+ex, statusCode);
            }
        } catch (HttpServerErrorException ex) {
            // Handle HTTP server errors (e.g., 5xx errors)
            HttpStatus statusCode = (HttpStatus) ex.getStatusCode();
            // Handle server errors gracefully
            return new ResponseEntity<>("Server error occurred", statusCode);
        } catch (Exception ex) {
            // Handle other unexpected exceptions
            ex.printStackTrace();
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logs out a currently authenticated user.
     *
     * @param logoutReq The username of the user to log out.
     * @return Something
     */
    @Override
    public Object logout(LogoutReq logoutReq) {
        return null;
    }
}
