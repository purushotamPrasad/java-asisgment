package com.qssence.backend.subscription_panel.service;

import com.qssence.backend.subscription_panel.dbo.Company;
import com.qssence.backend.subscription_panel.dto.request.CompanyRequestDto;
import com.qssence.backend.subscription_panel.dto.response.CompanyResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {
    CompanyResponseDto createCompany(CompanyRequestDto companyRequestDto);
    CompanyResponseDto getCompanyById(String companyId);
    List<CompanyResponseDto> getAllCompanies();
    void deleteCompany(String companyId);
    CompanyResponseDto updateCompany(String companyId, CompanyRequestDto companyRequestDto);
}
