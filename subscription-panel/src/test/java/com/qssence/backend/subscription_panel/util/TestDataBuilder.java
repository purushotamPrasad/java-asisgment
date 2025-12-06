//package com.qssence.backend.subscription_panel.util;
//
//import com.qssence.backend.subscription_panel.dbo.Company;
//import com.qssence.backend.subscription_panel.dbo.Status;
//import com.qssence.backend.subscription_panel.dbo.UserLicense;
//import com.qssence.backend.subscription_panel.dto.request.CompanyRequestDto;
//import com.qssence.backend.subscription_panel.dto.request.UserLicenseRequestDto;
//
//import java.time.LocalDate;
//
///**
// * Test Data Builder - Test cases ke liye data create karne ka helper class
// * Builder pattern use kiya hai easy data creation ke liye
// */
//public class TestDataBuilder {
//
//    /**
//     * Company entity create karne ka builder
//     */
//    public static class CompanyBuilder {
//        private String companyId = "TE001";
//        private String companyName = "Test Company";
//        private String companyPrefix = "TE";
//        private String location = "Mumbai";
//        private String region = "West";
//        private String timezone = "Asia/Kolkata";
//        private String phoneNo = "9876543210";
//        private String licenseNo = "LIC123456";
//        private String companyEmailId = "test@company.com";
//        private String password = "password123";
//        private String country = "India";
//        private Status status = Status.ACTIVE;
//
//        public CompanyBuilder companyId(String companyId) {
//            this.companyId = companyId;
//            return this;
//        }
//
//        public CompanyBuilder companyName(String companyName) {
//            this.companyName = companyName;
//            return this;
//        }
//
//        public CompanyBuilder companyEmailId(String companyEmailId) {
//            this.companyEmailId = companyEmailId;
//            return this;
//        }
//
//        public CompanyBuilder status(Status status) {
//            this.status = status;
//            return this;
//        }
//
//        public Company build() {
//            Company company = new Company();
//            company.setCompanyId(companyId);
//            company.setCompanyName(companyName);
//            company.setCompanyPrefix(companyPrefix);
//            company.setLocation(location);
//            company.setRegion(region);
//            company.setTimezone(timezone);
//            company.setPhoneNo(phoneNo);
//            company.setLicenseNo(licenseNo);
//            company.setCompanyEmailId(companyEmailId);
//            company.setPassword(password);
//            company.setCountry(country);
//            company.setStatus(status);
//            return company;
//        }
//    }
//
//    /**
//     * CompanyRequestDto create karne ka builder
//     */
//    public static class CompanyRequestDtoBuilder {
//        private String companyName = "Test Company";
//        private String companyPrefix = "TE";
//        private String location = "Mumbai";
//        private String region = "West";
//        private String timezone = "Asia/Kolkata";
//        private String phoneNo = "9876543210";
//        private String licenseNo = "LIC123456";
//        private String companyEmailId = "test@company.com";
//        private String password = "password123";
//        private String country = "India";
//        private String status = "ACTIVE";
//
//        public CompanyRequestDtoBuilder companyName(String companyName) {
//            this.companyName = companyName;
//            return this;
//        }
//
//        public CompanyRequestDtoBuilder companyEmailId(String companyEmailId) {
//            this.companyEmailId = companyEmailId;
//            return this;
//        }
//
//        public CompanyRequestDtoBuilder companyPrefix(String companyPrefix) {
//            this.companyPrefix = companyPrefix;
//            return this;
//        }
//
//        public CompanyRequestDto build() {
//            CompanyRequestDto dto = new CompanyRequestDto();
//            dto.setCompanyName(companyName);
//            dto.setCompanyPrefix(companyPrefix);
//            dto.setLocation(location);
//            dto.setRegion(region);
//            dto.setTimezone(timezone);
//            dto.setPhoneNo(phoneNo);
//            dto.setLicenseNo(licenseNo);
//            dto.setCompanyEmailId(companyEmailId);
//            dto.setPassword(password);
//            dto.setCountry(country);
//            dto.setStatus(status);
//            return dto;
//        }
//    }
//
//    /**
//     * UserLicense entity create karne ka builder
//     */
//    public static class UserLicenseBuilder {
//        private Long licenseId = 1L;
//        private Company company;
//        private LocalDate purchaseDate = LocalDate.now();
//        private LocalDate expiryDate = LocalDate.now().plusYears(1);
//        private Double purchaseCost = 1000.0;
//        private Integer totalUserAccess = 10;
//        private Integer adminAccountAllowed = 2;
//        private Integer userAccountAllowed = 8;
//        private String description = "Test License";
//        private boolean active = true;
//
//        public UserLicenseBuilder licenseId(Long licenseId) {
//            this.licenseId = licenseId;
//            return this;
//        }
//
//        public UserLicenseBuilder company(Company company) {
//            this.company = company;
//            return this;
//        }
//
//        public UserLicenseBuilder expiryDate(LocalDate expiryDate) {
//            this.expiryDate = expiryDate;
//            return this;
//        }
//
//        public UserLicenseBuilder active(boolean active) {
//            this.active = active;
//            return this;
//        }
//
//        public UserLicense build() {
//            UserLicense license = new UserLicense();
//            license.setLicenseId(licenseId);
//            license.setCompany(company);
//            license.setPurchaseDate(purchaseDate);
//            license.setExpiryDate(expiryDate);
//            license.setPurchaseCost(purchaseCost);
//            license.setTotalUserAccess(totalUserAccess);
//            license.setAdminAccountAllowed(adminAccountAllowed);
//            license.setUserAccountAllowed(userAccountAllowed);
//            license.setDescription(description);
//            license.setActive(active);
//            return license;
//        }
//    }
//
//    /**
//     * UserLicenseRequestDto create karne ka builder
//     */
//    public static class UserLicenseRequestDtoBuilder {
//        private Long companyId = 1L;
//        private LocalDate purchaseDate = LocalDate.now();
//        private LocalDate expiryDate = LocalDate.now().plusYears(1);
//        private Double purchaseCost = 1000.0;
//        private Integer totalUserAccess = 10;
//        private Integer adminAccountAllowed = 2;
//        private Integer userAccountAllowed = 8;
//        private String description = "Test License";
//
//        public UserLicenseRequestDtoBuilder companyId(Long companyId) {
//            this.companyId = companyId;
//            return this;
//        }
//
//        public UserLicenseRequestDtoBuilder totalUserAccess(Integer totalUserAccess) {
//            this.totalUserAccess = totalUserAccess;
//            return this;
//        }
//
//        public UserLicenseRequestDtoBuilder adminAccountAllowed(Integer adminAccountAllowed) {
//            this.adminAccountAllowed = adminAccountAllowed;
//            return this;
//        }
//
//        public UserLicenseRequestDtoBuilder userAccountAllowed(Integer userAccountAllowed) {
//            this.userAccountAllowed = userAccountAllowed;
//            return this;
//        }
//
//        public UserLicenseRequestDto build() {
//            UserLicenseRequestDto dto = new UserLicenseRequestDto();
//            dto.setCompanyId(companyId);
//            dto.setPurchaseDate(purchaseDate);
//            dto.setExpiryDate(expiryDate);
//            dto.setPurchaseCost(purchaseCost);
//            dto.setTotalUserAccess(totalUserAccess);
//            dto.setAdminAccountAllowed(adminAccountAllowed);
//            dto.setUserAccountAllowed(userAccountAllowed);
//            dto.setDescription(description);
//            return dto;
//        }
//    }
//
//    // Static factory methods for easy access
//    public static CompanyBuilder company() {
//        return new CompanyBuilder();
//    }
//
//    public static CompanyRequestDtoBuilder companyRequest() {
//        return new CompanyRequestDtoBuilder();
//    }
//
//    public static UserLicenseBuilder userLicense() {
//        return new UserLicenseBuilder();
//    }
//
//    public static UserLicenseRequestDtoBuilder userLicenseRequest() {
//        return new UserLicenseRequestDtoBuilder();
//    }
//}
