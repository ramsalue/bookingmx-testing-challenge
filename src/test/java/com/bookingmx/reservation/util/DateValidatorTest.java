package com.bookingmx.reservation.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DateValidator utility class.
 * These tests verify date validation logic including past/future date checking,
 * date range validation, and overlap detection.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
@DisplayName("DateValidator Utility Tests")
class DateValidatorTest {

    @Test
    @DisplayName("Should throw exception when instantiating DateValidator")
    void testConstructorThrowsException() throws NoSuchMethodException {

        // Get the constructor even when is private
        Constructor<DateValidator> constructor = DateValidator.class.getDeclaredConstructor();

        // Ignore the private and make the constructor accesible
        constructor.setAccessible(true);

        // Wait for an InvocationTargetException
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        });

        // Verify the cause of the error I want
        assertEquals(UnsupportedOperationException.class, exception.getCause().getClass());
        assertEquals("DateValidator is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Should identify past date correctly")
    void testIsPastDate() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Act & Assert
        assertTrue(DateValidator.isPastDate(yesterday));
    }

    @Test
    @DisplayName("Should identify today is not a past date")
    void testTodayIsNotPastDate() {
        // Arrange
        LocalDate today = LocalDate.now();

        // Act & Assert
        assertFalse(DateValidator.isPastDate(today));
    }

    @Test
    @DisplayName("Should identify future date is not past")
    void testFutureDateIsNotPast() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Act & Assert
        assertFalse(DateValidator.isPastDate(tomorrow));
    }

    @Test
    @DisplayName("Should throw exception for null date in isPastDate")
    void testIsPastDateWithNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.isPastDate(null);
        });
    }

    @Test
    @DisplayName("Should identify today correctly")
    void testIsToday() {
        // Arrange
        LocalDate today = LocalDate.now();

        // Act & Assert
        assertTrue(DateValidator.isToday(today));
    }

    @Test
    @DisplayName("Should identify future date correctly")
    void testIsFutureDate() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Act & Assert
        assertTrue(DateValidator.isFutureDate(tomorrow));
    }

    @Test
    @DisplayName("Should validate check-in date for today")
    void testValidCheckInDateToday() {
        // Arrange
        LocalDate today = LocalDate.now();

        // Act & Assert
        assertTrue(DateValidator.isValidCheckInDate(today));
    }

    @Test
    @DisplayName("Should validate check-in date in near future")
    void testValidCheckInDateNearFuture() {
        // Arrange
        LocalDate nextWeek = LocalDate.now().plusDays(7);

        // Act & Assert
        assertTrue(DateValidator.isValidCheckInDate(nextWeek));
    }

    @Test
    @DisplayName("Should reject check-in date in past")
    void testInvalidCheckInDatePast() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Act & Assert
        assertFalse(DateValidator.isValidCheckInDate(yesterday));
    }

    @Test
    @DisplayName("Should reject check-in date too far in future")
    void testInvalidCheckInDateTooFarFuture() {
        // Arrange
        LocalDate tooFar = LocalDate.now().plusDays(400);

        // Act & Assert
        assertFalse(DateValidator.isValidCheckInDate(tooFar));
    }

    @Test
    @DisplayName("Should validate check-out after check-in")
    void testValidCheckOutDate() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        // Act & Assert
        assertTrue(DateValidator.isValidCheckOutDate(checkIn, checkOut));
    }

    @Test
    @DisplayName("Should reject check-out before check-in")
    void testInvalidCheckOutBeforeCheckIn() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(7);

        // Act & Assert
        assertFalse(DateValidator.isValidCheckOutDate(checkIn, checkOut));
    }

    @Test
    @DisplayName("Should reject check-out equal to check-in")
    void testInvalidCheckOutEqualsCheckIn() {
        // Arrange
        LocalDate sameDate = LocalDate.now().plusDays(7);

        // Act & Assert
        assertFalse(DateValidator.isValidCheckOutDate(sameDate, sameDate));
    }

    @Test
    @DisplayName("Should validate date range meets minimum nights")
    void testMeetsMinimumNights() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(8); // 1 night

        // Act & Assert
        assertTrue(DateValidator.meetsMinimumNights(checkIn, checkOut));
    }

    @ParameterizedTest
    @CsvSource({
            "7, 10, 3",   // 3 nights
            "7, 8, 1",    // 1 night
            "7, 17, 10",  // 10 nights
            "0, 30, 30"   // 30 nights
    })
    @DisplayName("Should calculate nights correctly")
    void testCalculateNights(int checkInOffset, int checkOutOffset, long expectedNights) {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(checkInOffset);
        LocalDate checkOut = LocalDate.now().plusDays(checkOutOffset);

        // Act
        long nights = DateValidator.calculateNights(checkIn, checkOut);

        // Assert
        assertEquals(expectedNights, nights);
    }

    @Test
    @DisplayName("Should throw exception when calculating nights with checkout before checkin")
    void testCalculateNightsInvalidRange() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(7);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.calculateNights(checkIn, checkOut);
        });
    }

    @Test
    @DisplayName("Should validate complete date range")
    void testValidDateRange() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        // Act & Assert
        assertTrue(DateValidator.isValidDateRange(checkIn, checkOut));
    }

    @Test
    @DisplayName("Should reject date range with past check-in")
    void testInvalidDateRangePastCheckIn() {
        // Arrange
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertFalse(DateValidator.isValidDateRange(checkIn, checkOut));
    }

    @Test
    @DisplayName("Should detect overlapping date ranges")
    void testDateRangesOverlap() {
        // Arrange
        LocalDate start1 = LocalDate.of(2025, 11, 10);
        LocalDate end1 = LocalDate.of(2025, 11, 15);
        LocalDate start2 = LocalDate.of(2025, 11, 13);
        LocalDate end2 = LocalDate.of(2025, 11, 18);

        // Act & Assert
        assertTrue(DateValidator.doDateRangesOverlap(start1, end1, start2, end2));
    }

    @Test
    @DisplayName("Should detect non-overlapping date ranges")
    void testDateRangesDoNotOverlap() {
        // Arrange
        LocalDate start1 = LocalDate.of(2025, 11, 10);
        LocalDate end1 = LocalDate.of(2025, 11, 15);
        LocalDate start2 = LocalDate.of(2025, 11, 20);
        LocalDate end2 = LocalDate.of(2025, 11, 25);

        // Act & Assert
        assertFalse(DateValidator.doDateRangesOverlap(start1, end1, start2, end2));
    }

    @Test
    @DisplayName("Should detect overlapping ranges when one contains the other")
    void testDateRangesOverlapContainment() {
        // Arrange (range2 is inside range1)
        LocalDate start1 = LocalDate.of(2025, 11, 10);
        LocalDate end1 = LocalDate.of(2025, 11, 20);
        LocalDate start2 = LocalDate.of(2025, 11, 12);
        LocalDate end2 = LocalDate.of(2025, 11, 15);

        // Act & Assert
        assertTrue(DateValidator.doDateRangesOverlap(start1, end1, start2, end2));
    }

    @Test
    @DisplayName("Should provide validation message for null check-in")
    void testValidationMessageNullCheckIn() {
        // Act
        String message = DateValidator.getDateValidationMessage(null, LocalDate.now().plusDays(3));

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("Check-in date is required"));
    }

    @Test
    @DisplayName("Should provide validation message for past check-in")
    void testValidationMessagePastCheckIn() {
        // Arrange
        LocalDate past = LocalDate.now().minusDays(1);
        LocalDate future = LocalDate.now().plusDays(3);

        // Act
        String message = DateValidator.getDateValidationMessage(past, future);

        // Assert
        assertNotNull(message);
        assertTrue(message.toLowerCase().contains("past"));
    }

    @Test
    @DisplayName("Should return null for valid dates")
    void testValidationMessageValidDates() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        // Act
        String message = DateValidator.getDateValidationMessage(checkIn, checkOut);

        // Assert
        assertNull(message);
    }

    @Test
    @DisplayName("Should format date range correctly")
    void testFormatDateRange() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        // Act
        String formatted = DateValidator.formatDateRange(checkIn, checkOut);

        // Assert
        assertNotNull(formatted);
        assertTrue(formatted.contains("3 nights"));
        assertTrue(formatted.contains(checkIn.toString()));
        assertTrue(formatted.contains(checkOut.toString()));
    }
    @Test
    @DisplayName("Should throw exception for null in isValidCheckInDate")
    void testIsValidCheckInDateWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.isValidCheckInDate(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for null in isValidCheckOutDate")
    void testIsValidCheckOutDateWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.isValidCheckOutDate(LocalDate.now(), null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.isValidCheckOutDate(null, LocalDate.now());
        });
    }

    @Test
    @DisplayName("Should throw exception for nulls in doDateRangesOverlap")
    void testDoDateRangesOverlapWithNulls() {
        // Arrange
        LocalDate date = LocalDate.now();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.doDateRangesOverlap(null, date, date, date);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.doDateRangesOverlap(date, null, date, date);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.doDateRangesOverlap(date, date, null, date);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.doDateRangesOverlap(date, date, date, null);
        });
    }
    @Test
    @DisplayName("Should provide validation message for null check-out")
    void testValidationMessageNullCheckOut() {
        // Act
        String message = DateValidator.getDateValidationMessage(LocalDate.now().plusDays(1), null);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("Check-out date is required"));
    }

    @Test
    @DisplayName("Should provide validation message for check-out before check-in")
    void testValidationMessageCheckOutBeforeCheckIn() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        // Act
        String message = DateValidator.getDateValidationMessage(checkIn, checkOut);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("must be after"));
    }

    @Test
    @DisplayName("Should provide validation message for minimum nights")
    void testValidationMessageMinimumNights() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(5); // 0 noches

        // Act
        String message = DateValidator.getDateValidationMessage(checkIn, checkOut);

        // Assert
        assertNotNull(message);
        // Fail for "checkout must be after" before check min nights
        assertTrue(message.contains("must be after"));
    }

    @Test
    @DisplayName("Should provide validation message for too far in future")
    void testValidationMessageTooFar() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(400); // Más de 365
        LocalDate checkOut = LocalDate.now().plusDays(402);

        // Act
        String message = DateValidator.getDateValidationMessage(checkIn, checkOut);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("days in advance"));
    }

    @Test
    @DisplayName("Should reject date range that does not meet minimum nights (0 nights)")
    void testDoesNotMeetMinimumNights() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(7); // 0 nights

        // Act & Assert
        assertFalse(DateValidator.meetsMinimumNights(checkIn, checkOut));
    }

    @Test
    @DisplayName("Should throw exception for null dates in calculateNights")
    void testCalculateNightsWithNulls() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.calculateNights(null, LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.calculateNights(LocalDate.now(), null);
        });
    }

}