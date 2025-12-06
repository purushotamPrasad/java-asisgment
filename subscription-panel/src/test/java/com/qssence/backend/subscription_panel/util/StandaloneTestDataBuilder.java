package com.qssence.backend.subscription_panel.util;

import java.time.LocalDate;

/**
 * Standalone Test Data Builder - Main application code se independent
 * 
 * Ye test data builder main application code ke bina bhi test data create kar sakta hai
 * Simple POJO classes use karta hai testing ke liye
 */
public class StandaloneTestDataBuilder {

    /**
     * Simple Company class for testing
     */
    public static class TestCompany {
        private String companyId;
        private String companyName;
        private String companyPrefix;
        private String location;
        private String region;
        private String timezone;
        private String phoneNo;
        private String licenseNo;
        private String companyEmailId;
        private String password;
        private String country;
        private String status;

        // Constructors
        public TestCompany() {}

        public TestCompany(String companyId, String companyName, String companyEmailId) {
            this.companyId = companyId;
            this.companyName = companyName;
            this.companyEmailId = companyEmailId;
        }

        // Getters and Setters
        public String getCompanyId() { return companyId; }
        public void setCompanyId(String companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getCompanyPrefix() { return companyPrefix; }
        public void setCompanyPrefix(String companyPrefix) { this.companyPrefix = companyPrefix; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }

        public String getPhoneNo() { return phoneNo; }
        public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

        public String getLicenseNo() { return licenseNo; }
        public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

        public String getCompanyEmailId() { return companyEmailId; }
        public void setCompanyEmailId(String companyEmailId) { this.companyEmailId = companyEmailId; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * Simple UserLicense class for testing
     */
    public static class TestUserLicense {
        private Long licenseId;
        private TestCompany company;
        private LocalDate purchaseDate;
        private LocalDate expiryDate;
        private Double purchaseCost;
        private Integer totalUserAccess;
        private Integer adminAccountAllowed;
        private Integer userAccountAllowed;
        private String description;
        private boolean active;

        // Constructors
        public TestUserLicense() {}

        public TestUserLicense(Long licenseId, TestCompany company) {
            this.licenseId = licenseId;
            this.company = company;
        }

        // Getters and Setters
        public Long getLicenseId() { return licenseId; }
        public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }

        public TestCompany getCompany() { return company; }
        public void setCompany(TestCompany company) { this.company = company; }

        public LocalDate getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

        public Double getPurchaseCost() { return purchaseCost; }
        public void setPurchaseCost(Double purchaseCost) { this.purchaseCost = purchaseCost; }

        public Integer getTotalUserAccess() { return totalUserAccess; }
        public void setTotalUserAccess(Integer totalUserAccess) { this.totalUserAccess = totalUserAccess; }

        public Integer getAdminAccountAllowed() { return adminAccountAllowed; }
        public void setAdminAccountAllowed(Integer adminAccountAllowed) { this.adminAccountAllowed = adminAccountAllowed; }

        public Integer getUserAccountAllowed() { return userAccountAllowed; }
        public void setUserAccountAllowed(Integer userAccountAllowed) { this.userAccountAllowed = userAccountAllowed; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    /**
     * Company Request DTO for testing
     */
    public static class TestCompanyRequestDto {
        private String companyName;
        private String companyPrefix;
        private String location;
        private String region;
        private String timezone;
        private String phoneNo;
        private String licenseNo;
        private String companyEmailId;
        private String password;
        private String country;
        private String status;

        // Constructors
        public TestCompanyRequestDto() {}

        // Getters and Setters
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getCompanyPrefix() { return companyPrefix; }
        public void setCompanyPrefix(String companyPrefix) { this.companyPrefix = companyPrefix; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }

        public String getPhoneNo() { return phoneNo; }
        public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

        public String getLicenseNo() { return licenseNo; }
        public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

        public String getCompanyEmailId() { return companyEmailId; }
        public void setCompanyEmailId(String companyEmailId) { this.companyEmailId = companyEmailId; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * UserLicense Request DTO for testing
     */
    public static class TestUserLicenseRequestDto {
        private Long companyId;
        private LocalDate purchaseDate;
        private LocalDate expiryDate;
        private Double purchaseCost;
        private Integer totalUserAccess;
        private Integer adminAccountAllowed;
        private Integer userAccountAllowed;
        private String description;

        // Constructors
        public TestUserLicenseRequestDto() {}

        // Getters and Setters
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public LocalDate getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

        public Double getPurchaseCost() { return purchaseCost; }
        public void setPurchaseCost(Double purchaseCost) { this.purchaseCost = purchaseCost; }

        public Integer getTotalUserAccess() { return totalUserAccess; }
        public void setTotalUserAccess(Integer totalUserAccess) { this.totalUserAccess = totalUserAccess; }

        public Integer getAdminAccountAllowed() { return adminAccountAllowed; }
        public void setAdminAccountAllowed(Integer adminAccountAllowed) { this.adminAccountAllowed = adminAccountAllowed; }

        public Integer getUserAccountAllowed() { return userAccountAllowed; }
        public void setUserAccountAllowed(Integer userAccountAllowed) { this.userAccountAllowed = userAccountAllowed; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * Company Response DTO for testing
     */
    public static class TestCompanyResponseDto {
        private String companyId;
        private String companyName;
        private String companyPrefix;
        private String location;
        private String region;
        private String timezone;
        private String phoneNo;
        private String licenseNo;
        private String companyEmailId;
        private String country;
        private String status;

        // Constructors
        public TestCompanyResponseDto() {}

        // Getters and Setters
        public String getCompanyId() { return companyId; }
        public void setCompanyId(String companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getCompanyPrefix() { return companyPrefix; }
        public void setCompanyPrefix(String companyPrefix) { this.companyPrefix = companyPrefix; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }

        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }

        public String getPhoneNo() { return phoneNo; }
        public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

        public String getLicenseNo() { return licenseNo; }
        public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

        public String getCompanyEmailId() { return companyEmailId; }
        public void setCompanyEmailId(String companyEmailId) { this.companyEmailId = companyEmailId; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * UserLicense Response DTO for testing
     */
    public static class TestUserLicenseResponseDto {
        private Long licenseId;
        private Long companyId;
        private String companyName;
        private String licenseKey;
        private Integer totalUserAccess;
        private Integer adminAccountAllowed;
        private Integer userAccountAllowed;
        private LocalDate purchaseDate;
        private LocalDate expiryDate;
        private boolean active;
        private boolean expired;

        // Constructors
        public TestUserLicenseResponseDto() {}

        // Getters and Setters
        public Long getLicenseId() { return licenseId; }
        public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getLicenseKey() { return licenseKey; }
        public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }

        public Integer getTotalUserAccess() { return totalUserAccess; }
        public void setTotalUserAccess(Integer totalUserAccess) { this.totalUserAccess = totalUserAccess; }

        public Integer getAdminAccountAllowed() { return adminAccountAllowed; }
        public void setAdminAccountAllowed(Integer adminAccountAllowed) { this.adminAccountAllowed = adminAccountAllowed; }

        public Integer getUserAccountAllowed() { return userAccountAllowed; }
        public void setUserAccountAllowed(Integer userAccountAllowed) { this.userAccountAllowed = userAccountAllowed; }

        public LocalDate getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

        public LocalDate getExpiryDate() { return expiryDate; }
        public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public boolean isExpired() { return expired; }
        public void setExpired(boolean expired) { this.expired = expired; }
    }

    // Static factory methods for easy access
    public static TestCompany createTestCompany() {
        TestCompany company = new TestCompany();
        company.setCompanyId("TE001");
        company.setCompanyName("Test Company");
        company.setCompanyPrefix("TE");
        company.setLocation("Mumbai");
        company.setRegion("West");
        company.setTimezone("Asia/Kolkata");
        company.setPhoneNo("9876543210");
        company.setLicenseNo("LIC123456");
        company.setCompanyEmailId("test@company.com");
        company.setPassword("password123");
        company.setCountry("India");
        company.setStatus("ACTIVE");
        return company;
    }

    public static TestUserLicense createTestUserLicense() {
        TestUserLicense license = new TestUserLicense();
        license.setLicenseId(1L);
        license.setCompany(createTestCompany());
        license.setPurchaseDate(LocalDate.now());
        license.setExpiryDate(LocalDate.now().plusYears(1));
        license.setPurchaseCost(1000.0);
        license.setTotalUserAccess(10);
        license.setAdminAccountAllowed(2);
        license.setUserAccountAllowed(8);
        license.setDescription("Test License");
        license.setActive(true);
        return license;
    }

    public static TestCompanyRequestDto createTestCompanyRequest() {
        TestCompanyRequestDto request = new TestCompanyRequestDto();
        request.setCompanyName("Test Company");
        request.setCompanyPrefix("TE");
        request.setLocation("Mumbai");
        request.setRegion("West");
        request.setTimezone("Asia/Kolkata");
        request.setPhoneNo("9876543210");
        request.setLicenseNo("LIC123456");
        request.setCompanyEmailId("test@company.com");
        request.setPassword("password123");
        request.setCountry("India");
        request.setStatus("ACTIVE");
        return request;
    }

    public static TestUserLicenseRequestDto createTestUserLicenseRequest() {
        TestUserLicenseRequestDto request = new TestUserLicenseRequestDto();
        request.setCompanyId(1L);
        request.setPurchaseDate(LocalDate.now());
        request.setExpiryDate(LocalDate.now().plusYears(1));
        request.setPurchaseCost(1000.0);
        request.setTotalUserAccess(10);
        request.setAdminAccountAllowed(2);
        request.setUserAccountAllowed(8);
        request.setDescription("Test License");
        return request;
    }

    public static TestCompanyResponseDto createTestCompanyResponse() {
        TestCompanyResponseDto response = new TestCompanyResponseDto();
        response.setCompanyId("TE001");
        response.setCompanyName("Test Company");
        response.setCompanyPrefix("TE");
        response.setLocation("Mumbai");
        response.setRegion("West");
        response.setTimezone("Asia/Kolkata");
        response.setPhoneNo("9876543210");
        response.setLicenseNo("LIC123456");
        response.setCompanyEmailId("test@company.com");
        response.setCountry("India");
        response.setStatus("ACTIVE");
        return response;
    }

    public static TestUserLicenseResponseDto createTestUserLicenseResponse() {
        TestUserLicenseResponseDto response = new TestUserLicenseResponseDto();
        response.setLicenseId(1L);
        response.setCompanyId(1L);
        response.setCompanyName("Test Company");
        response.setLicenseKey("test-key");
        response.setTotalUserAccess(10);
        response.setAdminAccountAllowed(2);
        response.setUserAccountAllowed(8);
        response.setPurchaseDate(LocalDate.now());
        response.setExpiryDate(LocalDate.now().plusYears(1));
        response.setActive(true);
        response.setExpired(false);
        return response;
    }
}
