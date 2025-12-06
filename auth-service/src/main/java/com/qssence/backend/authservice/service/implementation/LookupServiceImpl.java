package com.qssence.backend.authservice.service.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qssence.backend.authservice.dbo.Lookup;
import com.qssence.backend.authservice.dto.LookupDto;
import com.qssence.backend.authservice.exception.ResourceNotFoundException;
import com.qssence.backend.authservice.repository.LookupRepository;
import com.qssence.backend.authservice.service.LookupService;

@Service
public class LookupServiceImpl implements LookupService{

	@Autowired
	private LookupRepository lookupRepository;
	
	
	@Override
	public LookupDto createLookup(LookupDto lookupDto) {
		
		Lookup lookup = mapToEntity(lookupDto);
		Lookup newLookup = lookupRepository.save(lookup);
		LookupDto lookupResponse = mapToDto(newLookup);
		return lookupResponse;
	}

	@Override
	public List<LookupDto> getAllLookup() {
		
		List<Lookup>lookups = lookupRepository.findAll();
		
		return lookups.stream().map(lookup -> mapToDto(lookup)).collect(Collectors.toList());
	}

	@Override
	public LookupDto getLooupById(Long id) {
		Lookup lookup = lookupRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("UserRole", "id", id));
		return mapToDto(lookup);
	}

	@Override
	public LookupDto updateLookup(LookupDto lookupDto, Long Id) {
	    Lookup lookup = lookupRepository.findById(Id).orElseThrow(()-> new ResourceNotFoundException("UserRole", "id", Id));
	    
	    lookup.setLkValue(lookupDto.getLkValue());
	    lookup.setLong_Name(lookupDto.getLongName());
	    lookup.setLkSetName(lookupDto.getLkSetName());
	    lookup.setLkrecStatus(lookupDto.getLkrecStatus());
	    
	    Lookup updateLookupData = lookupRepository.save(lookup);
	    
		return mapToDto(updateLookupData);
	}

	@Override
	public void deleteLookup(Long id) {
		Lookup lookup = lookupRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("UserRole", "id", id));
		
		lookupRepository.delete(lookup);
	}
	
	//convert entity to DTO
	
	private LookupDto mapToDto(Lookup lookup)
	{
		LookupDto lookupDto = new LookupDto();
		
		lookupDto.setLkid(lookup.getLkid());
		lookupDto.setLkValue(lookup.getLkValue());
		lookupDto.setLongName(lookup.getLong_Name());
		lookupDto.setLkSetName(lookup.getLkSetName());
		lookupDto.setLkrecStatus(lookup.getLkrecStatus());
		
		return lookupDto;			
	}
	
	//convert DTO to entity
	
	private Lookup mapToEntity(LookupDto lookupDto)
	{
		Lookup lookup = new Lookup();
		
		lookup.setLkid(lookupDto.getLkid());
		lookup.setLkValue(lookupDto.getLkValue());
		lookup.setLong_Name(lookupDto.getLongName());
		lookup.setLkSetName(lookupDto.getLkSetName());
		lookup.setLkrecStatus(lookupDto.getLkrecStatus());
		
		return lookup;
		
	}
	
	

}
