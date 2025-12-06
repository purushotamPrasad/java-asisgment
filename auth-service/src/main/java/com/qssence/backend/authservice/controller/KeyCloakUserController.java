package com.qssence.backend.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qssence.backend.authservice.dbo.KeyCloakUser;
import com.qssence.backend.authservice.repository.KeyCloakUserRepository;
import com.qssence.backend.authservice.service.implementation.UserService;

@RestController
@RequestMapping("/api/v1/")
public class KeyCloakUserController {
	
	@Autowired
	private KeyCloakUserRepository keyCloakUserRepository;
	
	@Autowired
	private UserService userService;
	
	
	@PostMapping("/saveUserdb")
	public KeyCloakUser saveKeycloakUser(@RequestBody KeyCloakUser user)
	{
		System.out.println("Your data has been save ins db");
		
		return keyCloakUserRepository.save(user);
	}
}
