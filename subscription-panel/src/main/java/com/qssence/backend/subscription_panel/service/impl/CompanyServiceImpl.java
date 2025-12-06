package com.qssence.backend.subscription_panel.service.impl;

import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dbo.Status;
import com.qssence.backend.subscription_panel.dto.request.CompanyRequestDto;
import com.qssence.backend.subscription_panel.dto.response.CompanyResponseDto;
import com.qssence.backend.subscription_panel.repository.CompanyRepository;
import com.qssence.backend.subscription_panel.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto) {
        // ✅ Check if email already exists
        Optional<Company> existingCompany = companyRepository.findByCompanyEmailId(companyRequestDto.getCompanyEmailId());
        if (existingCompany.isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + companyRequestDto.getCompanyEmailId());
        }

        // ✅ Ensure prefix is provided
        if (companyRequestDto.getCompanyPrefix() == null || companyRequestDto.getCompanyPrefix().isBlank()) {
            throw new IllegalArgumentException("Company prefix is required and cannot be blank");
        }

        Company company = mapToEntity(companyRequestDto);

        // ✅ Generate companyId
        String companyId = generateCompanyId(company.getCompanyName());
        company.setCompanyId(companyId);

        // ✅ Set prefix (always required)
        company.setCompanyPrefix(companyRequestDto.getCompanyPrefix().toUpperCase());

        Company savedCompany = companyRepository.save(company);
        return mapToResponseDto(savedCompany);
    }

    @Override
    public CompanyResponseDto getCompanyById(String companyId) {
        Optional<Company> company = companyRepository.findById(companyId);
        if (company.isEmpty()) {
            throw new RuntimeException("Company with ID " + companyId + " not found.");
        }
        return mapToResponseDto(company.get());
    }

    @Override
    public List<CompanyResponseDto> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCompany(String companyId) {
        Optional<Company> company = companyRepository.findById(companyId);
        if (company.isEmpty()) {
            throw new RuntimeException("Company with ID " + companyId + " not found.");
        }
        companyRepository.delete(company.get());
    }

    @Override
    public CompanyResponseDto updateCompany(String companyId, CompanyRequestDto companyRequestDto) {
        try {
            Optional<Company> existingCompany = companyRepository.findById(companyId);
            if (existingCompany.isEmpty()) {
                throw new RuntimeException("Company with ID " + companyId + " not found.");
            }

            Company company = existingCompany.get();
            company.setCompanyName(companyRequestDto.getCompanyName());
            company.setLocation(companyRequestDto.getLocation());
            company.setRegion(companyRequestDto.getRegion());
            company.setTimezone(companyRequestDto.getTimezone());
            company.setPhoneNo(companyRequestDto.getPhoneNo());
            company.setLicenseNo(companyRequestDto.getLicenseNo());
            company.setCountry(companyRequestDto.getCountry());
            company.setCompanyEmailId(companyRequestDto.getCompanyEmailId());

            // ✅ Update prefix (must be provided)
            if (companyRequestDto.getCompanyPrefix() == null || companyRequestDto.getCompanyPrefix().isBlank()) {
                throw new IllegalArgumentException("Company prefix is required and cannot be blank");
            }
            company.setCompanyPrefix(companyRequestDto.getCompanyPrefix().toUpperCase());

            // ✅ Update status if provided
            if (companyRequestDto.getStatus() != null && !companyRequestDto.getStatus().isEmpty()) {
                try {
                    Status status = Status.valueOf(companyRequestDto.getStatus().toUpperCase());
                    company.setStatus(status);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid status value: " + companyRequestDto.getStatus());
                }
            }

            Company updatedCompany = companyRepository.save(company);
            return mapToResponseDto(updatedCompany);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Constraint violation error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to update company: " + e.getMessage());
        }
    }

    // ✅ DTO → Entity mapping
    private Company mapToEntity(CompanyRequestDto companyRequestDto) {
        return Company.builder()
                .companyName(companyRequestDto.getCompanyName())
                .location(companyRequestDto.getLocation())
                .region(companyRequestDto.getRegion())
                .timezone(companyRequestDto.getTimezone())
                .phoneNo(companyRequestDto.getPhoneNo())
                .licenseNo(companyRequestDto.getLicenseNo())
                .companyEmailId(companyRequestDto.getCompanyEmailId())
                .password(companyRequestDto.getPassword()) // TODO: encode this with BCryptPasswordEncoder
                .country(companyRequestDto.getCountry())
                .status(
                        companyRequestDto.getStatus() != null
                                ? Status.valueOf(companyRequestDto.getStatus().toUpperCase())
                                : Status.ACTIVE
                )
                .build();
    }

    // ✅ Entity → Response mapping
    private CompanyResponseDto mapToResponseDto(Company company) {
        return CompanyResponseDto.builder()
                .companyId(company.getCompanyId())
                .companyName(company.getCompanyName())
                .companyPrefix(company.getCompanyPrefix())
                .location(company.getLocation())
                .region(company.getRegion())
                .timezone(company.getTimezone())
                .phoneNo(company.getPhoneNo())
                .licenseNo(company.getLicenseNo())
                .companyEmailId(company.getCompanyEmailId())
                .createdAt(company.getCreatedAt())
                .country(company.getCountry())
                .password(company.getPassword())
                .status(company.getStatus().name())
                .build();
    }

    // ✅ CompanyId generate karne ka logic
    private String generateCompanyId(String companyName) {
        String prefix = companyName.substring(0, 2).toUpperCase();
        long count = companyRepository.count();
        String sequence = String.format("%03d", count + 1);
        return prefix + sequence;
    }
}
