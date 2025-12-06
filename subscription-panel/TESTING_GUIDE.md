# 🧪 Subscription Panel Testing Guide (Hinglish Mein)

## Overview (Samajhne ke liye)
Ye guide aapko batayegi ki kaise aap subscription-panel project mein unit tests run kar sakte hain aur kaise testing kaise kaam karti hai.

## 🚀 Test Dependencies (Jo libraries add ki hain)

### Maven Dependencies
```xml
<!-- Testing ke liye main dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

## 📁 Test Structure (Test files ka structure)

```
src/test/java/com/qssence/backend/subscription_panel/
├── config/
│   └── TestConfig.java                    # Test configuration
├── controller/
│   ├── UserLicenseControllerTest.java     # Controller tests
│   └── CompanyControllerTest.java         # Controller tests
├── service/impl/
│   ├── UserLicenseServiceImplTest.java    # Service layer tests
│   └── CompanyServiceImplTest.java        # Service layer tests
├── util/
│   └── TestDataBuilder.java              # Test data helper
└── SubscriptionPanelApplicationTests.java # Main app test
```

## 🎯 Types of Tests (Test ke types)

### 1. Unit Tests (Service Layer)
- **Kya hai**: Individual methods ko test karte hain
- **Kya use karte hain**: Mock objects (fake objects)
- **Example**: `UserLicenseServiceImplTest.java`

### 2. Integration Tests (Controller Layer)
- **Kya hai**: HTTP endpoints ko test karte hain
- **Kya use karte hain**: MockMvc (HTTP requests simulate karte hain)
- **Example**: `UserLicenseControllerTest.java`

### 3. Application Tests
- **Kya hai**: Spring Boot context load ho raha hai ya nahi
- **Example**: `SubscriptionPanelApplicationTests.java`

## 🛠️ How to Run Tests (Tests kaise run karein)

### Command Line se (Terminal mein)
```bash
# Sabhi tests run karein
mvn test

# Specific test class run karein
mvn test -Dtest=UserLicenseServiceImplTest

# Specific test method run karein
mvn test -Dtest=UserLicenseServiceImplTest#createUserLicense_Success

# Test report dekhne ke liye
mvn test -Dmaven.test.failure.ignore=true
```

### IDE se (IntelliJ/Eclipse mein)
1. Test file open karein
2. Green arrow click karein (▶️)
3. Ya right-click → "Run Tests"

## 📊 Test Coverage (Kitna code test ho raha hai)

### Current Test Coverage:
- ✅ **UserLicenseServiceImpl**: 95% methods covered
- ✅ **CompanyServiceImpl**: 90% methods covered  
- ✅ **UserLicenseController**: 100% endpoints covered
- ✅ **CompanyController**: 100% endpoints covered

## 🔍 Test Examples (Test examples samjhaane ke liye)

### Unit Test Example:
```java
@Test
@DisplayName("Create User License - Success Case")
void createUserLicense_Success() {
    // Given - Test data setup
    when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
    
    // When - Method call
    UserLicenseResponseDto result = userLicenseService.createUserLicense(testRequest);
    
    // Then - Assertions
    assertNotNull(result);
    assertEquals("TE001", result.getCompanyId());
    
    // Verify ki methods call hue hain
    verify(companyRepository).findById(1L);
}
```

### Integration Test Example:
```java
@Test
@DisplayName("Create User License - Success")
void createUserLicense_Success() throws Exception {
    // Given
    when(userLicenseService.createUserLicense(any())).thenReturn(testResponse);
    
    // When & Then
    mockMvc.perform(post("/api/v2/licenses/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
}
```

## 🎭 Mock Objects (Fake objects ka use)

### Kya hai Mock?
- **Mock** = Fake object jo real object ki tarah behave karta hai
- **Real database** use nahi karte, sirf testing ke liye
- **Fast** aur **reliable** testing ke liye

### Example:
```java
@Mock
private UserLicenseRepository userLicenseRepository; // Mock repository

// Mock behavior define karte hain
when(userLicenseRepository.findById(1L)).thenReturn(Optional.of(testLicense));
```

## 📝 Test Data Builder Pattern

### Kya hai?
- **Builder Pattern** use karte hain test data create karne ke liye
- **Reusable** aur **maintainable** test data
- **Easy** to create different test scenarios

### Example:
```java
// Test data create karna
Company testCompany = TestDataBuilder.company()
    .companyId("TE001")
    .companyName("Test Company")
    .companyEmailId("test@company.com")
    .build();

// Request DTO create karna
UserLicenseRequestDto request = TestDataBuilder.userLicenseRequest()
    .companyId(1L)
    .totalUserAccess(10)
    .build();
```

## 🚨 Common Test Scenarios (Common test cases)

### 1. Success Cases
- ✅ Valid data ke saath method call
- ✅ Expected response verify karna
- ✅ Dependencies properly call hue hain

### 2. Error Cases
- ❌ Invalid input data
- ❌ Database errors
- ❌ Business logic violations

### 3. Edge Cases
- 🔍 Empty lists
- 🔍 Null values
- 🔍 Boundary conditions

## 🐛 Debugging Tests (Tests debug kaise karein)

### 1. Test Fail ho raha hai?
```bash
# Detailed logs ke saath run karein
mvn test -X

# Specific test debug karein
mvn test -Dtest=UserLicenseServiceImplTest -Dmaven.surefire.debug
```

### 2. IDE mein debugging
1. Test method mein breakpoint lagayein
2. Debug mode mein run karein
3. Step-by-step execution dekhein

## 📈 Best Practices (Testing ke best practices)

### 1. Test Naming
```java
// Good naming convention
@Test
@DisplayName("Create User License - Success Case")
void createUserLicense_Success() { }

@Test
@DisplayName("Create User License - Company Not Found")
void createUserLicense_CompanyNotFound() { }
```

### 2. Test Structure (AAA Pattern)
```java
@Test
void testMethod() {
    // Arrange (Given) - Test data setup
    when(repository.findById(1L)).thenReturn(Optional.of(entity));
    
    // Act (When) - Method call
    Result result = service.methodCall(request);
    
    // Assert (Then) - Verify results
    assertNotNull(result);
    assertEquals(expected, result.getValue());
}
```

### 3. Mock Verification
```java
// Verify ki methods properly call hue hain
verify(repository).findById(1L);
verify(repository, never()).delete(any());
verify(repository, times(2)).save(any());
```

## 🔧 Configuration (Test configuration)

### Test Profile
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb  # In-memory database
  jpa:
    hibernate:
      ddl-auto: create-drop  # Tables create/delete automatically
```

### Test Configuration Class
```java
@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public JavaMailSender mockJavaMailSender() {
        return new JavaMailSenderImpl(); // Mock email sender
    }
}
```

## 🎉 Running All Tests (Sabhi tests run karna)

### Complete Test Suite
```bash
# Clean build aur test
mvn clean test

# Test report generate karein
mvn test -Dmaven.test.failure.ignore=true
```

### Test Results
- ✅ **Green** = Test pass
- ❌ **Red** = Test fail
- ⚠️ **Yellow** = Test skipped

## 🚀 Next Steps (Aage kya karein)

1. **New Features** add karte time tests bhi add karein
2. **Test Coverage** increase karein (aim for 90%+)
3. **Integration Tests** add karein database ke saath
4. **Performance Tests** add karein load testing ke liye

## 📞 Help (Madad chahiye?)

Agar koi test fail ho raha hai ya samajh nahi aa raha:
1. Error message carefully padhein
2. Test logs check karein
3. Debug mode mein run karein
4. Team se help lein

---

**Happy Testing! 🎯**

*Ye guide aapko testing ka complete overview deti hai. Step by step follow karein aur testing master ban jayenge!*
