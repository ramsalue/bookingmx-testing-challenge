# BookingMx Reservation System - Unit Testing Project

## Challenge 7: Java and JavaScript Programming Procedures
**Digital NAO - In-Mexico Program - Backend Developer Certification**

---

## Project Overview

This project implements a hotel reservation system for BookingMx with a focus on unit testing practices. The system includes a Java backend with business logic for managing hotel reservations, room availability, pricing calculations, and date validations.

The primary objective is to demonstrate proficiency in:
- Writing comprehensive unit tests using JUnit 5
- Achieving 90% code coverage using JaCoCo
- Implementing best practices in test-driven development
- Creating maintainable and well-documented code

---

## System Architecture

```
┌─────────────────────────────────────────────┐
│     BookingMx Reservation System            │
├─────────────────────────────────────────────┤
│  Service Layer                              │
│  └─ ReservationService                      │
│     - Create/Update/Cancel reservations     │
│     - Search and filtering                  │
│     - Availability checking                 │
│     - Price quoting                         │
├─────────────────────────────────────────────┤
│  Utility Layer                              │
│  ├─ DateValidator                           │
│  │  - Date range validation                 │
│  │  - Overlap detection                     │
│  ├─ PriceCalculator                         │
│  │  - Price calculations with discounts     │
│  │  - Tax computation                       │
│  └─ AvailabilityChecker                     │
│     - Room capacity management              │
│     - Conflict detection                    │
├─────────────────────────────────────────────┤
│  Repository Layer                           │
│  └─ InMemoryReservationRepository           │
│     - Thread-safe data storage              │
│     - CRUD operations                       │
├─────────────────────────────────────────────┤
│  Domain Model                               │
│  ├─ Reservation (entity)                    │
│  ├─ RoomType (enum)                         │
│  ├─ ReservationStatus (enum)                │
│  └─ Custom Exceptions                       │
└─────────────────────────────────────────────┘
```

---

## Features

### Business Features
- **Reservation Management**: Create, update, confirm, and cancel reservations
- **Room Types**: SINGLE ($50), DOUBLE ($80), SUITE ($150), DELUXE ($200)
- **Tiered Discounts**: 
  - 7-13 nights: 5% off
  - 14-29 nights: 10% off
  - 30+ nights: 15% off
- **Tax Calculation**: Automatic 16% tax on all reservations
- **Availability Checking**: Room availability with capacity limits
- **Date Validation**: Date range validation
- **Search & Filter**: By guest name, email, status, room type, date range

### Technical Features
- **100+ Unit Tests**: Comprehensive test coverage
- **91% Code Coverage**: Verified with JaCoCo
- **Thread-Safe**: Concurrent data access using ConcurrentHashMap
- **Exception Handling**: Custom exceptions with meaningful messages
- **Repository Pattern**: Abstraction for easy database migration
- **Clean Architecture**: Separation of concerns across layers

---

## Technologies Used

- **Java 17**: Core programming language
- **Maven 3.9+**: Build and dependency management
- **JUnit 5**: Unit testing framework
- **JaCoCo**: Code coverage analysis
- **Spark Framework**: Lightweight web framework (existing)
- **Git/GitHub**: Version control

---

## Installation and Setup

### Prerequisites

Ensure you have the following installed:
- Java Development Kit (JDK) 17 or higher
- Maven 3.9 or higher
- Git
- IntelliJ IDEA (recommended) or any Java IDE

### Clone the Repository

```bash
git clone https://github.com/ramsalue/bookingmx-testing-challenge.git
cd bookingmx-testing-challenge
```

### Build the Project

```bash
mvn clean install
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 10.5 s
```

### Run Tests

```bash
mvn test
```

### Generate Coverage Report

```bash
mvn clean test jacoco:report
```

View the report: `target/site/jacoco/index.html`

---

## Test Coverage

### Coverage Summary

| Package | Coverage |
|---------|----------|
| `com.bookingmx.reservation.model` | 91%      |
| `com.bookingmx.reservation.util` | 91%      |
| `com.bookingmx.reservation.service` | 91%      |
| `com.bookingmx.reservation.repository` | 96%      |
| **Overall** | **91%**  |

### Test Statistics

- **Total Tests**: 208
- **Test Classes**: 6
- **All Tests Passing**: YES

### Test Breakdown

```
ReservationTest.java (48 tests)
├── Constructor validation
├── Date validation
├── Email validation
├── Status transitions
├── Price calculations
└── Business logic

DateValidatorTest.java (38 tests)
├── Past/present/future detection
├── Date range validation
├── Overlap detection
├── Minimum nights checking
└── Validation messages

PriceCalculatorTest.java (43 tests)
├── Base price calculation
├── Discount tier logic
├── Tax computation
├── Price breakdown
└── All room types

AvailabilityCheckerTest.java (29 tests)
├── Capacity management
├── Overlap detection
├── Active reservation filtering
├── Availability validation
└── Availability reports

ReservationServiceTest.java (31 tests)
├── CRUD operations
├── Search and filtering
├── Availability checking
├── Price quoting
└── Business validation

InMemoryReservationRepositoryTest.java (19 tests)
├── Save operations
├── Find operations
├── Update operations
├── Delete operations
└── Thread safety
```

---

## Project Structure

```
bookingmx-testing-challenge/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── bookingmx/
│   │   │           └── reservation/
│   │   │               ├── exception/
│   │   │               │   ├── InvalidReservationException.java
│   │   │               │   └── ReservationNotFoundException.java
│   │   │               ├── model/
│   │   │               │   ├── Reservation.java
│   │   │               │   ├── ReservationStatus.java
│   │   │               │   └── RoomType.java
│   │   │               ├── repository/
│   │   │               │   ├── ReservationRepository.java
│   │   │               │   └── InMemoryReservationRepository.java
│   │   │               ├── service/
│   │   │               │   └── ReservationService.java
│   │   │               └── util/
│   │   │                   ├── AvailabilityChecker.java
│   │   │                   ├── DateValidator.java
│   │   │                   └── PriceCalculator.java
│   │   └── resources/
│   │       └── templates/
│   │           └── hello.mustache (existing)
│   └── test/
│       └── java/
│           └── com/
│               └── bookingmx/
│                   └── reservation/
│                       ├── model/
│                       │   └── ReservationTest.java
│                       ├── repository/
│                       │   └── InMemoryReservationRepositoryTest.java
│                       ├── service/
│                       │   └── ReservationServiceTest.java
│                       └── util/
│                           ├── AvailabilityCheckerTest.java
│                           ├── DateValidatorTest.java
│                           └── PriceCalculatorTest.java
├── target/
│   └── site/
│       └── jacoco/
│           └── index.html (coverage report)
├── screenshots/
│   └── sprint1/
├── pom.xml
├── README.md
└── .gitignore
```

---

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=ReservationTest
```

### Run Tests with Coverage

```bash
mvn clean test jacoco:report
```

### View Coverage Report

Open in browser: `target/site/jacoco/index.html`

---
## Business Rules

### Reservation Rules
- Check-in date must be today or in the future
- Check-in cannot be more than 365 days in advance
- Check-out must be after check-in
- Minimum stay: 1 night
- Email validation required
- Guest name required

### Room Capacity
- SINGLE: 10 rooms
- DOUBLE: 8 rooms
- SUITE: 5 rooms
- DELUXE: 3 rooms

### Pricing Rules
- Base price per night varies by room type
- Discounts applied automatically based on length of stay
- 16% tax calculated on discounted price
- Price recalculated when dates change

### Reservation Status Flow
```
PENDING → CONFIRMED → CHECKED_IN → COMPLETED
    ↓
CANCELLED (from PENDING, CONFIRMED, or CHECKED_IN)
```

---

## Known Limitations

1. **In-Memory Storage**: Data lost on application restart
   - Solution: Can be replaced with database implementation via Repository pattern

2. **Single Hotel**: System designed for one hotel location
   - Solution: Can be extended to support multiple properties

3. **No Payment Processing**: Prices calculated but not charged
   - Solution: Payment gateway integration can be added to service layer

4. **No User Authentication**: No user management system
   - Solution: Security layer can be added above service layer

---

## Future Enhancements

- Database persistence (MongoDB/PostgreSQL)
- REST API endpoints for external access
- Email notifications for reservations
- Payment gateway integration
- Multi-hotel support
- Reporting and analytics dashboard
- Admin panel for hotel management
- Customer loyalty program

---

## Contributors

- **Developer**: Luis E Ramirez
- **Program**: Digital NAO - In-Mexico Program
- **Challenge**: Challenge 7 - Java and JavaScript Programming Procedures
- **Date**: November 2025

---
**Project Status**: Sprint 1 Completed 

**Version**: 2.0.0  
**Last Updated**: November 2025