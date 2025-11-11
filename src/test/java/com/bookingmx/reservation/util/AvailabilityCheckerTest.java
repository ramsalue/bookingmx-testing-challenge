package com.bookingmx.reservation.util;

import com.bookingmx.reservation.exception.InvalidReservationException;
import com.bookingmx.reservation.model.Reservation;
import com.bookingmx.reservation.model.ReservationStatus;
import com.bookingmx.reservation.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AvailabilityChecker utility class.
 * These tests verify room availability logic including capacity management,
 * overlap detection, and validation.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
@DisplayName("AvailabilityChecker Utility Tests")
class AvailabilityCheckerTest {

    private List<Reservation> testReservations;

    @BeforeEach
    void setUp() throws InvalidReservationException {
        testReservations = new ArrayList<>();

        // Create some test reservations
        Reservation res1 = new Reservation(
                "John Doe",
                "john@example.com",
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15),
                RoomType.DOUBLE
        );
        res1.confirm();
        testReservations.add(res1);

        Reservation res2 = new Reservation(
                "Jane Smith",
                "jane@example.com",
                LocalDate.now().plusDays(13),
                LocalDate.now().plusDays(17),
                RoomType.DOUBLE
        );
        res2.confirm();
        testReservations.add(res2);

        Reservation res3 = new Reservation(
                "Bob Johnson",
                "bob@example.com",
                LocalDate.now().plusDays(20),
                LocalDate.now().plusDays(25),
                RoomType.SUITE
        );
        res3.confirm();
        testReservations.add(res3);
    }

    @Test
    @DisplayName("Should throw exception when instantiating AvailabilityChecker")
    void testConstructorThrowsException() throws NoSuchMethodException {
        // Get the constructor even when is private
        Constructor<AvailabilityChecker> constructor = AvailabilityChecker.class.getDeclaredConstructor();

        // Ignore the private and make the constructor accesible
        constructor.setAccessible(true);

        // Wait for an InvocationTargetException
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        });

        // Verify the cause of the error I want
        assertEquals(UnsupportedOperationException.class, exception.getCause().getClass());
        assertEquals("AvailabilityChecker is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @ParameterizedTest
    @EnumSource(RoomType.class)
    @DisplayName("Should return correct capacity for all room types")
    void testGetRoomCapacity(RoomType roomType) {
        // Act
        int capacity = AvailabilityChecker.getRoomCapacity(roomType);

        // Assert
        assertTrue(capacity > 0);
        switch (roomType) {
            case SINGLE:
                assertEquals(10, capacity);
                break;
            case DOUBLE:
                assertEquals(8, capacity);
                break;
            case SUITE:
                assertEquals(5, capacity);
                break;
            case DELUXE:
                assertEquals(3, capacity);
                break;
        }
    }

    @Test
    @DisplayName("Should throw exception for null room type in capacity check")
    void testGetRoomCapacityNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.getRoomCapacity(null);
        });
    }

    @Test
    @DisplayName("Should identify confirmed reservation as active")
    void testIsActiveReservationConfirmed() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                "Test Guest",
                "test@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.SINGLE
        );
        reservation.confirm();

        // Act & Assert
        assertTrue(AvailabilityChecker.isActiveReservation(reservation));
    }

    @Test
    @DisplayName("Should identify checked-in reservation as active")
    void testIsActiveReservationCheckedIn() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                "Test Guest",
                "test@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.SINGLE
        );
        reservation.setStatus(ReservationStatus.CHECKED_IN);

        // Act & Assert
        assertTrue(AvailabilityChecker.isActiveReservation(reservation));
    }

    @Test
    @DisplayName("Should identify pending reservation as not active")
    void testIsActiveReservationPending() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                "Test Guest",
                "test@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.SINGLE
        );

        // Act & Assert
        assertFalse(AvailabilityChecker.isActiveReservation(reservation));
    }

    @Test
    @DisplayName("Should identify cancelled reservation as not active")
    void testIsActiveReservationCancelled() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                "Test Guest",
                "test@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.SINGLE
        );
        reservation.confirm();
        reservation.cancel();

        // Act & Assert
        assertFalse(AvailabilityChecker.isActiveReservation(reservation));
    }

    @Test
    @DisplayName("Should detect overlapping reservations")
    void testDoReservationsOverlap() throws InvalidReservationException {
        // Arrange
        Reservation res1 = new Reservation(
                "Guest 1",
                "guest1@example.com",
                LocalDate.of(2025, 11, 11),
                LocalDate.of(2025, 11, 15),
                RoomType.DOUBLE
        );

        Reservation res2 = new Reservation(
                "Guest 2",
                "guest2@example.com",
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 18),
                RoomType.DOUBLE
        );

        // Act & Assert
        assertTrue(AvailabilityChecker.doReservationsOverlap(res1, res2));
    }

    @Test
    @DisplayName("Should detect non-overlapping reservations")
    void testDoReservationsNotOverlap() throws InvalidReservationException {
        // Arrange
        Reservation res1 = new Reservation(
                "Guest 1",
                "guest1@example.com",
                LocalDate.of(2025, 11, 11),
                LocalDate.of(2025, 11, 15),
                RoomType.DOUBLE
        );

        Reservation res2 = new Reservation(
                "Guest 2",
                "guest2@example.com",
                LocalDate.of(2025, 11, 20),
                LocalDate.of(2025, 11, 25),
                RoomType.DOUBLE
        );

        // Act & Assert
        assertFalse(AvailabilityChecker.doReservationsOverlap(res1, res2));
    }

    @Test
    @DisplayName("Should get active reservations for room type")
    void testGetActiveReservationsForRoomType() {
        // Act
        List<Reservation> doubleRooms =
                AvailabilityChecker.getActiveReservationsForRoomType(testReservations, RoomType.DOUBLE);

        // Assert
        assertEquals(2, doubleRooms.size());
        assertTrue(doubleRooms.stream().allMatch(r -> r.getRoomType() == RoomType.DOUBLE));
    }

    @Test
    @DisplayName("Should count overlapping reservations correctly")
    void testCountOverlappingReservations() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(12);
        LocalDate checkOut = LocalDate.now().plusDays(16);

        // Act
        int count = AvailabilityChecker.countOverlappingReservations(
                testReservations, RoomType.DOUBLE, checkIn, checkOut);

        // Assert
        assertEquals(2, count); // Both DOUBLE reservations overlap with this range
    }

    @Test
    @DisplayName("Should return zero for non-overlapping date range")
    void testCountOverlappingReservationsNone() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = LocalDate.now().plusDays(35);

        // Act
        int count = AvailabilityChecker.countOverlappingReservations(
                testReservations, RoomType.DOUBLE, checkIn, checkOut);

        // Assert
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Should confirm room is available when capacity allows")
    void testIsRoomAvailable() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = LocalDate.now().plusDays(33);

        // Act
        boolean available = AvailabilityChecker.isRoomAvailable(
                testReservations, RoomType.SINGLE, checkIn, checkOut);

        // Assert
        assertTrue(available);
    }

    @Test
    @DisplayName("Should return false when room capacity is exceeded")
    void testIsRoomNotAvailable() throws InvalidReservationException {
        // Arrange - Fill up DELUXE capacity (3 rooms)
        LocalDate checkIn = LocalDate.now().plusDays(50);
        LocalDate checkOut = LocalDate.now().plusDays(53);

        for (int i = 0; i < 3; i++) {
            Reservation res = new Reservation(
                    "Guest " + i,
                    "guest" + i + "@example.com",
                    checkIn,
                    checkOut,
                    RoomType.DELUXE
            );
            res.confirm();
            testReservations.add(res);
        }

        // Act
        boolean available = AvailabilityChecker.isRoomAvailable(
                testReservations, RoomType.DELUXE, checkIn, checkOut);

        // Assert
        assertFalse(available); // All 3 DELUXE rooms are booked
    }

    @Test
    @DisplayName("Should get correct available room count")
    void testGetAvailableRoomCount() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(12);
        LocalDate checkOut = LocalDate.now().plusDays(16);
        // 2 DOUBLE rooms are booked for overlapping dates

        // Act
        int available = AvailabilityChecker.getAvailableRoomCount(
                testReservations, RoomType.DOUBLE, checkIn, checkOut);

        // Assert
        assertEquals(6, available); // 8 total - 2 booked = 6 available
    }

    @Test
    @DisplayName("Should return zero when no rooms available")
    void testGetAvailableRoomCountZero() throws InvalidReservationException {
        // Arrange - Book all DELUXE rooms
        LocalDate checkIn = LocalDate.now().plusDays(60);
        LocalDate checkOut = LocalDate.now().plusDays(63);

        for (int i = 0; i < 3; i++) {
            Reservation res = new Reservation(
                    "Guest " + i,
                    "guest" + i + "@example.com",
                    checkIn,
                    checkOut,
                    RoomType.DELUXE
            );
            res.confirm();
            testReservations.add(res);
        }

        // Act
        int available = AvailabilityChecker.getAvailableRoomCount(
                testReservations, RoomType.DELUXE, checkIn, checkOut);

        // Assert
        assertEquals(0, available);
    }

    @Test
    @DisplayName("Should generate availability report")
    void testGetAvailabilityReport() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = LocalDate.now().plusDays(33);

        // Act
        AvailabilityChecker.AvailabilityReport report =
                AvailabilityChecker.getAvailabilityReport(testReservations, checkIn, checkOut);

        // Assert
        assertNotNull(report);
        String reportString = report.toString();
        assertTrue(reportString.contains("SINGLE"));
        assertTrue(reportString.contains("DOUBLE"));
        assertTrue(reportString.contains("SUITE"));
        assertTrue(reportString.contains("DELUXE"));
    }

    @Test
    @DisplayName("Should validate new reservation successfully")
    void testValidateNewReservationSuccess() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = LocalDate.now().plusDays(33);

        // Act
        AvailabilityChecker.ValidationResult result =
                AvailabilityChecker.validateNewReservation(
                        testReservations, RoomType.SINGLE, checkIn, checkOut);

        // Assert
        assertTrue(result.isValid());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Should reject reservation with invalid dates")
    void testValidateNewReservationInvalidDates() {
        // Arrange
        LocalDate checkIn = LocalDate.now().minusDays(1); // Past date
        LocalDate checkOut = LocalDate.now().plusDays(3);

        // Act
        AvailabilityChecker.ValidationResult result =
                AvailabilityChecker.validateNewReservation(
                        testReservations, RoomType.SINGLE, checkIn, checkOut);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("past"));
    }

    @Test
    @DisplayName("Should reject reservation when room not available")
    void testValidateNewReservationNoAvailability() throws InvalidReservationException {
        // Arrange - Fill up DELUXE capacity
        LocalDate checkIn = LocalDate.now().plusDays(70);
        LocalDate checkOut = LocalDate.now().plusDays(73);

        for (int i = 0; i < 3; i++) {
            Reservation res = new Reservation(
                    "Guest " + i,
                    "guest" + i + "@example.com",
                    checkIn,
                    checkOut,
                    RoomType.DELUXE
            );
            res.confirm();
            testReservations.add(res);
        }

        // Act
        AvailabilityChecker.ValidationResult result =
                AvailabilityChecker.validateNewReservation(
                        testReservations, RoomType.DELUXE, checkIn, checkOut);

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("available") ||
                result.getMessage().contains("Available"));
    }

    @Test
    @DisplayName("Should throw exception for null parameters")
    void testNullParameterHandling() {
        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.isRoomAvailable(null, RoomType.SINGLE,
                    LocalDate.now(), LocalDate.now().plusDays(1));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.isRoomAvailable(testReservations, null,
                    LocalDate.now(), LocalDate.now().plusDays(1));
        });
    }

    @Test
    @DisplayName("Should throw exception for null in isActiveReservation")
    void testIsActiveReservationNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.isActiveReservation(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for nulls in countOverlappingReservations")
    void testCountOverlappingReservationsNulls() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.countOverlappingReservations(null, RoomType.SINGLE, checkIn, checkOut);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.countOverlappingReservations(testReservations, null, checkIn, checkOut);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.countOverlappingReservations(testReservations, RoomType.SINGLE, null, checkOut);
        });
    }

    @Test
    @DisplayName("Should throw exception for nulls in getActiveReservationsForRoomType")
    void testGetActiveReservationsForRoomTypeNulls() {
        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.getActiveReservationsForRoomType(null, RoomType.SINGLE);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.getActiveReservationsForRoomType(testReservations, null);
        });
    }

    @Test
    @DisplayName("Should throw exception for null dates in availability checks")
    void testNullDatesInAvailabilityChecks() {
        // Arreglo (listo en @BeforeEach)

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.countOverlappingReservations(testReservations, RoomType.SINGLE, null, LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.isRoomAvailable(testReservations, RoomType.SINGLE, null, LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.getAvailableRoomCount(testReservations, RoomType.SINGLE, LocalDate.now(), null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.getAvailabilityReport(testReservations, null, LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.validateNewReservation(testReservations, RoomType.SINGLE, LocalDate.now(), null);
        });
    }
    @Test
    @DisplayName("Should throw exception for nulls in doReservationsOverlap")
    void testDoReservationsOverlapNulls() throws InvalidReservationException {
        // Arrange
        Reservation res1 = new Reservation(
                "Test Guest",
                "test@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.DOUBLE
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.doReservationsOverlap(null, res1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            AvailabilityChecker.doReservationsOverlap(res1, null);
        });
    }
}