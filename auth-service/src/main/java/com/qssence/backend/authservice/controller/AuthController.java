package com.qssence.backend.authservice.controller;
import com.qssence.backend.authservice.dto.APIError;
import com.qssence.backend.authservice.dto.request.LoginRequest;
import com.qssence.backend.authservice.kafka.event.LoginRequestEvent;
import com.qssence.backend.authservice.kafka.producer.AuthProducer;
import com.qssence.backend.authservice.service.AuthService;
import com.qssence.backend.authservice.service.implementation.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller for handling authentication and authorization related operations.
 * This controller manages user authentication, user management (CRUD operations),
 * role management (CRUD operations), group management (CRUD operations),
 * permission management (CRUD operations), and password management.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "http://localhost:3001")
public class AuthController {


    private final AuthService authService;
    private final LoginService loginService;
    private final AuthProducer authProducer;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult, HttpServletRequest request)
    {
        if (bindingResult.hasErrors())
        {
            APIError error = new APIError();
            error.setError_code(4000);
            error.setError_name("INVALID_REQUEST");
            error.setError_description("Invalid Username or Password.");
            LoginRequestEvent loginEvent = new LoginRequestEvent("Login attempt failed: Invalid Username or Password", "failure", LocalDateTime.now(), request.getRemoteAddr(),loginRequest.getUsername());
            authProducer.sendMessage(loginEvent);
            return ResponseEntity.badRequest().body(error);
        }
        if(loginService.login(loginRequest).getStatusCode() == HttpStatus.UNAUTHORIZED){
            APIError error = new APIError();
            error.setError_code(4010);
            error.setError_name("UNAUTHORIZED_ACCESS");
            error.setError_description("Invalid Username or Password.");
            LoginRequestEvent loginEvent = new LoginRequestEvent("Login attempt failed: Unauthorized Access", "failure", LocalDateTime.now(), request.getRemoteAddr(),loginRequest.getUsername());
            authProducer.sendMessage(loginEvent);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        if(loginService.login(loginRequest).getStatusCode() == HttpStatus.NOT_FOUND){
            APIError error = new APIError();
            error.setError_code(4040);
            error.setError_name("NOT_FOUND");
            error.setError_description("User is not registered.");
            LoginRequestEvent loginEvent = new LoginRequestEvent("Login attempt failed: User not registered.", "failure", LocalDateTime.now(), request.getRemoteAddr(),loginRequest.getUsername());
            authProducer.sendMessage(loginEvent);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        LoginRequestEvent loginEvent = new LoginRequestEvent("User login successful.", "success", LocalDateTime.now(), request.getRemoteAddr(),loginRequest.getUsername());

        authProducer.sendMessage(loginEvent);
        System.out.println("Action of Login User is :-"+loginEvent);
        return loginService.login(loginRequest);
    }

    @GetMapping("/logout/{id_token}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@PathVariable String id_token, HttpServletRequest request) {
        ResponseEntity<String> responseEntity = authService.logout(id_token);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            LoginRequestEvent loginEvent = new LoginRequestEvent("User logout successful.", "success", LocalDateTime.now(),  request.getRemoteAddr(),null);
            authProducer.sendMessage(loginEvent);
        } else {
            LoginRequestEvent logEvent = new LoginRequestEvent("Logout attempt failed", "failure", LocalDateTime.now(), request.getRemoteAddr(),null);
            authProducer.sendMessage(logEvent);
        }
        return responseEntity;
    }

}
