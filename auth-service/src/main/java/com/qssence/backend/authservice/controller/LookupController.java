package com.qssence.backend.authservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qssence.backend.authservice.dto.LookupDto;
import com.qssence.backend.authservice.service.LookupService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/API/lookupcreation")
public class LookupController {
	
	@Autowired
	private LookupService lookupService;
	
	//lookup creation
	@PostMapping("/createLookup")
	public ResponseEntity<LookupDto> createLookup(@RequestBody LookupDto lookupDto)
	{
		return new ResponseEntity<>(lookupService.createLookup(lookupDto),HttpStatus.CREATED);
	}
	
	//get all lookup data 
	public List<LookupDto>getAllLookup()
	{
		return lookupService.getAllLookup();
	}
	
	//search lookup by id 
	
	@GetMapping("/{id}")
	public ResponseEntity<LookupDto> getLookupById(@PathVariable(name = "id") Long id)
	{
		return ResponseEntity.ok(lookupService.getLooupById(id));
	}
	
	//It will Update th Looup Data Filds
	@PutMapping("/{id}")
	public ResponseEntity<LookupDto> updateLookupData(@RequestBody LookupDto lookupDto, @PathVariable(name = "id") Long id)
	{
		LookupDto lookupResponse = lookupService.updateLookup(lookupDto, id);
		return new ResponseEntity<>(lookupResponse,HttpStatus.OK);
	}
	
	public ResponseEntity<String> deleteLookupData(@PathVariable(name = "id") Long id)
	{
		lookupService.deleteLookup(id);
		return new ResponseEntity<>("User Role has been deleted successfully.",HttpStatus.OK);
	}
}
