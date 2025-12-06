package com.qssence.backend.subscription_panel.service;

import com.qssence.backend.subscription_panel.dto.request.UserLicenseRequestDto;
import com.qssence.backend.subscription_panel.dto.response.UserLicenseResponseDto;

import java.util.List;


public interface UserLicenseService {

    UserLicenseResponseDto createUserLicense(UserLicenseRequestDto request);

    List<UserLicenseResponseDto> getAllUserLicenses();

    UserLicenseResponseDto getUserLicenseById(Long id);

    UserLicenseResponseDto decodeLicenseKey(String encodedKey);

    UserLicenseResponseDto updateUserLicense(Long id, UserLicenseRequestDto request);

    void deleteUserLicense(Long licenseId);

//    void renewLicense(Long licenseId, int additionalMonths);
}
