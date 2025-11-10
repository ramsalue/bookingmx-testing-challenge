# Testing Documentation - BookingMx Reservation System

## Sprint 1: JUnit Testing Implementation

---

## Testing Overview

This document provides detailed information about the testing strategy, test execution, and coverage analysis for the BookingMx Reservation System.

---

## Testing Objectives

1. Achieve minimum 90% code coverage across all modules
2. Verify all business logic functions correctly
3. Test positive and negative scenarios
4. Validate exception handling
5. Ensure thread-safe operations
6. Document testing approach for future development

---

## Testing Framework and Tools

### JUnit 5 (Jupiter)
- **Version**: 5.10.0
- **Purpose**: Unit testing framework
- **Features Used**:
  - @Test annotations
  - @DisplayName for readable test names
  - @BeforeEach for test setup
  - @ParameterizedTest for data-driven tests
  - @EnumSource for testing all enum values
  - Assertions library (assertEquals, assertTrue, assertThrows, etc.)

### JaCoCo (Java Code Coverage)
- **Version**: 0.8.11
- **Purpose**: Code coverage analysis
- **Report Location**: `target/site/jacoco/index.html`
- **Minimum Coverage**: 90% (configured in pom.xml)

### Maven Surefire Plugin
- **Version**: 3.1.2
- **Purpose**: Test execution during Maven build

---

## Test Structure

```
src/test/java/
└── com/bookingmx/reservation/
    ├── model/
    │   └── ReservationTest.java          (44 tests)
    ├── util/
    │   ├── DateValidatorTest.java        (38 tests)
    │   ├── PriceCalculatorTest.java      (39 tests)
    │   └── AvailabilityCheckerTest.java  (29 tests)
    ├── service/
    │   └── ReservationServiceTest.java   (31 tests)
    └── repository/
        └── InMemoryReservationRepositoryTest.java (19 tests)
```

---

## Test Coverage by Module

### 1. Domain Model Tests (ReservationTest.java)

**Coverage**: 90%

**Test Categories**:
- Constructor validation (valid and invalid inputs)
- Date validation (past dates, invalid ranges)
- Email validation (format checking)
- Business logic (price calculation, night counting)
- Status transitions (confirm, cancel)
- Getters and setters validation
- Edge cases (null values, boundary conditions)

**Key Test Examples**:
```java
@Test
void testCreateReservationWithValidData()
@Test
void testCreateReservationWithPastCheckIn()
@ParameterizedTest
@ValueSource(strings = {"", "invalid", "invalid@"})
void testCreateReservationWithInvalidEmail(String invalidEmail)
```

---

### 2. Utility Classes Tests

#### DateValidatorTest.java (Coverage: 86%)

**Test Categories**:
- Past/present/future date detection
- Check-in date validation
- Check-out date validation
- Date range validation
- Overlap detection
- Minimum nights checking
- Validation messages
- Night calculation

**Key Features Tested**:
- Maximum advance booking (365 days)
- Minimum stay requirement (1 night)
- Date range overlap logic
- User-friendly error messages

#### PriceCalculatorTest.java (Coverage: 92%)

**Test Categories**:
- Base price calculation for all room types
- Discount tier logic (5%, 10%, 15%)
- Tax calculation (16%)
- Price breakdown generation
- Price formatting
- Negative value handling

**Discount Tiers Tested**:
```
1-6 nights: No discount
7-13 nights: 5% discount
14-29 nights: 10% discount
30+ nights: 15% discount
```

#### AvailabilityCheckerTest.java (Coverage: 98%)

**Test Categories**:
- Room capacity retrieval
- Active reservation identification
- Reservation overlap detection
- Overlapping reservation counting
- Room availability checking
- Available room count calculation
- Availability report generation
- Validation result generation

**Capacity Limits Tested**:
- SINGLE: 10 rooms
- DOUBLE: 8 rooms
- SUITE: 5 rooms
- DELUXE: 3 rooms

---

### 3. Service Layer Tests (ReservationServiceTest.java)

**Coverage**: 91%

**Test Categories**:
- Create reservation with validation
- Confirm reservation
- Cancel reservation
- Update reservation dates
- Search by guest name
- Search by email
- Filter by status
- Filter by room type
- Find reservations for date range
- Check room availability
- Get available room count
- Get price quote
- Get reservation summary
- Delete reservation

**Business Logic Verified**:
- All validation rules enforced
- Availability checked before booking
- Price calculated correctly with discounts
- Only active reservations block availability
- Search operations work correctly
- Update operations maintain data integrity

---

### 4. Repository Layer Tests (InMemoryReservationRepositoryTest.java)

**Coverage**: 96%

**Test Categories**:
- Save operations
- Find by ID operations
- Find all operations
- Update operations
- Delete operations
- Exists checking
- Count operations
- Clear operations
- Thread safety (implicit through ConcurrentHashMap)

---

##  Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=ReservationTest
mvn test -Dtest=DateValidatorTest
mvn test -Dtest=PriceCalculatorTest
mvn test -Dtest=AvailabilityCheckerTest
mvn test -Dtest=ReservationServiceTest
mvn test -Dtest=InMemoryReservationRepositoryTest
```

### Run Tests with Coverage Report
```bash
mvn clean test jacoco:report
```

### View Coverage Report
1. Run the coverage command above
2. Open `target/site/jacoco/index.html` in a web browser
3. Navigate through packages to see detailed coverage

---

## Coverage Results

### Overall Coverage: 91%

### Coverage Screenshots

Screenshots of coverage reports are available in:
- `screenshots/sprint1/overall-coverage.png`

---

## Issues Found and Fixed Through Testing

1. **Date Validation**: Discovered check-in could equal check-out
   - Fix: Added validation for minimum 1-night stay

2. **Price Calculation**: Tax calculated on base price instead of discounted price
   - Fix: Changed calculation order (discount first, then tax)

3. **Availability**: Pending reservations blocked rooms
   - Fix: Only count CONFIRMED and CHECKED_IN as active

4. **Email Validation**: Simple regex didn't catch all invalid formats
   - Fix: Enhanced regex pattern

---

## Continuous Integration

### Build Command
```bash
mvn clean install
```

This runs:
1. Compilation
2. All tests
3. JaCoCo coverage analysis
4. Coverage threshold check (90%)
5. Package creation

### Expected Output
```
[INFO] Tests run: 203, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
---

## Future Testing Enhancements

1. Integration tests with real database
2. Performance tests for concurrent access
3. Load testing for high availability scenarios
4. Mutation testing to verify test quality
5. Property-based testing for edge cases

---

**Document Version**: 1.0  
**Last Updated**: November 2025  
**Status**: Sprint 1 Complete