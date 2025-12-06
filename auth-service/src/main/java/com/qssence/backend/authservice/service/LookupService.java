package com.qssence.backend.authservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qssence.backend.authservice.dto.LookupDto;

@Service
public interface LookupService {
	
	public LookupDto createLookup(LookupDto lookupDto);
	public List<LookupDto> getAllLookup();
	public LookupDto getLooupById(Long id);
	public LookupDto updateLookup(LookupDto lookupDto, Long Id);
	void deleteLookup(Long id);

}
