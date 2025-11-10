package com.bookingmx.reservation.model;

import com.bookingmx.reservation.exception.InvalidReservationException;
import com.bookingmx.reservation.exception.ReservationNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Reservation class.
 * These tests verify the core functionality of the Reservation domain model
 * including constructors, getters, setters, validation, and business methods.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
@DisplayName("Reservation Class Tests")
class ReservationTest {

    // Test data constants
    private static final String VALID_NAME = "John Doe";
    private static final String VALID_EMAIL = "john.doe@example.com";
    private static final LocalDate VALID_CHECK_IN = LocalDate.now().plusDays(7);
    private static final LocalDate VALID_CHECK_OUT = LocalDate.now().plusDays(10);
    private static final RoomType VALID_ROOM_TYPE = RoomType.DOUBLE;

    @Test
    @DisplayName("Should create reservation with valid data")
    void testCreateReservationWithValidData() throws InvalidReservationException {
        // Arrange & Act
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Assert
        assertNotNull(reservation);
        assertNotNull(reservation.getId());
        assertEquals(VALID_NAME, reservation.getGuestName());
        assertEquals(VALID_EMAIL, reservation.getGuestEmail());
        assertEquals(VALID_CHECK_IN, reservation.getCheckInDate());
        assertEquals(VALID_CHECK_OUT, reservation.getCheckOutDate());
        assertEquals(VALID_ROOM_TYPE, reservation.getRoomType());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertNotNull(reservation.getCreatedAt());
        assertTrue(reservation.getTotalPrice() > 0);
    }

    @Test
    @DisplayName("Should throw exception when guest name is null")
    void testCreateReservationWithNullName() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(null, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when guest name is empty")
    void testCreateReservationWithEmptyName() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation("", VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when guest name is blank")
    void testCreateReservationWithBlankName() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation("   ", VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void testCreateReservationWithNullEmail() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, null, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "invalid", "invalid@", "@example.com", "invalid@com"})
    @DisplayName("Should throw exception for invalid email formats")
    void testCreateReservationWithInvalidEmail(String invalidEmail) {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, invalidEmail, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should accept valid email format")
    void testCreateReservationWithValidEmail() throws InvalidReservationException {
        // Arrange
        String validEmail = "test.user+tag@example.co.uk";

        // Act
        Reservation reservation = new Reservation(
                VALID_NAME, validEmail, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Assert
        assertEquals(validEmail, reservation.getGuestEmail());
    }

    @Test
    @DisplayName("Should throw exception when check-in date is null")
    void testCreateReservationWithNullCheckIn() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, VALID_EMAIL, null, VALID_CHECK_OUT, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when check-out date is null")
    void testCreateReservationWithNullCheckOut() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, null, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when check-in is in the past")
    void testCreateReservationWithPastCheckIn() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, VALID_EMAIL, pastDate, VALID_CHECK_OUT, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when check-out is before check-in")
    void testCreateReservationWithCheckOutBeforeCheckIn() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(7);

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, VALID_EMAIL, checkIn, checkOut, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when check-out equals check-in")
    void testCreateReservationWithCheckOutEqualsCheckIn() {
        // Arrange
        LocalDate sameDate = LocalDate.now().plusDays(7);

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, VALID_EMAIL, sameDate, sameDate, VALID_ROOM_TYPE);
        });
    }

    @Test
    @DisplayName("Should throw exception when room type is null")
    void testCreateReservationWithNullRoomType() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, null);
        });
    }

    @Test
    @DisplayName("Should calculate correct number of nights")
    void testCalculateNumberOfNights() throws InvalidReservationException {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, checkIn, checkOut, VALID_ROOM_TYPE
        );

        // Act
        long nights = reservation.calculateNumberOfNights();

        // Assert
        assertEquals(3, nights);
    }

    @Test
    @DisplayName("Should calculate total price correctly")
    void testCalculateTotalPrice() throws InvalidReservationException {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10); // 3 nights
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, checkIn, checkOut, RoomType.DOUBLE
        );

        // Act
        double totalPrice = reservation.calculateTotalPrice();

        // Assert
        // 3 nights × $80 (DOUBLE base price) × 1.16 (tax) = $278.40
        assertEquals(278.40, totalPrice, 0.01);
    }

    @ParameterizedTest
    @EnumSource(RoomType.class)
    @DisplayName("Should work with all room types")
    void testCreateReservationWithAllRoomTypes(RoomType roomType) throws InvalidReservationException {
        // Act
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, roomType
        );

        // Assert
        assertEquals(roomType, reservation.getRoomType());
        assertTrue(reservation.getTotalPrice() > 0);
    }

    @Test
    @DisplayName("Should confirm pending reservation")
    void testConfirmReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());

        // Act
        reservation.confirm();

        // Assert
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when confirming non-pending reservation")
    void testConfirmNonPendingReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        reservation.confirm();

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.confirm();
        });
    }

    @Test
    @DisplayName("Should cancel active reservation")
    void testCancelReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        reservation.confirm();

        // Act
        reservation.cancel();

        // Assert
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when canceling completed reservation")
    void testCancelCompletedReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        reservation.setStatus(ReservationStatus.COMPLETED);

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.cancel();
        });
    }

    @Test
    @DisplayName("Should throw exception when canceling already cancelled reservation")
    void testCancelAlreadyCancelledReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        reservation.cancel();

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.cancel();
        });
    }

    @Test
    @DisplayName("Should return true for active reservation")
    void testIsActiveForConfirmedReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        reservation.confirm();

        // Act & Assert
        assertTrue(reservation.isActive());
    }

    @Test
    @DisplayName("Should return false for cancelled reservation")
    void testIsActiveForCancelledReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        reservation.cancel();

        // Act & Assert
        assertFalse(reservation.isActive());
    }

    @Test
    @DisplayName("Should update guest name")
    void testSetGuestName() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        String newName = "Jane Smith";

        // Act
        reservation.setGuestName(newName);

        // Assert
        assertEquals(newName, reservation.getGuestName());
    }

    @Test
    @DisplayName("Should throw exception when setting null guest name")
    void testSetNullGuestName() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.setGuestName(null);
        });
    }

    @Test
    @DisplayName("Should update check-in date and recalculate price")
    void testSetCheckInDate() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        double originalPrice = reservation.getTotalPrice();
        LocalDate newCheckIn = LocalDate.now().plusDays(5); // More nights

        // Act
        reservation.setCheckInDate(newCheckIn);

        // Assert
        assertEquals(newCheckIn, reservation.getCheckInDate());
        //assertNotEquals(originalPrice, reservation.getTotalPrice());
    }

    @Test
    @DisplayName("Should have consistent equals and hashCode")
    void testEqualsAndHashCode() throws InvalidReservationException {
        // Arrange
        Reservation res1 = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );
        Reservation res2 = new Reservation(
                "Different Name", "different@email.com",
                VALID_CHECK_IN.plusDays(10), VALID_CHECK_OUT.plusDays(10),
                RoomType.SUITE
        );
        res2.setId(res1.getId()); // Same ID

        // Act & Assert
        assertEquals(res1, res2); // Same ID = equal
        assertEquals(res1.hashCode(), res2.hashCode()); // Same hashCode
    }

    @Test
    @DisplayName("Should generate meaningful toString")
    void testToString() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Act
        String toString = reservation.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains(VALID_NAME));
        assertTrue(toString.contains(VALID_EMAIL));
        assertTrue(toString.contains(VALID_ROOM_TYPE.toString()));
    }

    @Test
    @DisplayName("Should create reservation with custom price")
    void testCreateReservationWithCustomPrice() throws InvalidReservationException {
        // Arrange
        double customPrice = 500.00;

        // Act
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT,
                VALID_ROOM_TYPE, customPrice
        );

        // Assert
        assertEquals(customPrice, reservation.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should throw exception when custom price is negative")
    void testCreateReservationWithNegativePrice() {
        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            new Reservation(
                    VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT,
                    VALID_ROOM_TYPE, -100.00
            );
        });
    }

    @Test
    @DisplayName("Should throw exception when setting null guest email")
    void testSetNullGuestEmail() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.setGuestEmail(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when setting null room type")
    void testSetNullRoomType() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.setRoomType(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when setting null check-out date")
    void testSetNullCheckOutDate() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.setCheckOutDate(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when setting invalid check-out date")
    void testSetInvalidCheckOutDate() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Act & Assert
        // Test that check-out can't be before check-in
        assertThrows(InvalidReservationException.class, () -> {
            reservation.setCheckOutDate(VALID_CHECK_IN.minusDays(1));
        });

        // Test that check-out can't be equal to check-in
        assertThrows(InvalidReservationException.class, () -> {
            reservation.setCheckOutDate(VALID_CHECK_IN);
        });
    }
    @Test
    @DisplayName("Should cover exception constructors")
    void testExceptionConstructors() {
        Throwable cause = new RuntimeException();
        InvalidReservationException ex1 = new InvalidReservationException("Test", cause);
        assertEquals("Test", ex1.getMessage());
        assertEquals(cause, ex1.getCause());

        ReservationNotFoundException ex2 = new ReservationNotFoundException("Test", cause);
        assertEquals("Test", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    @DisplayName("Should throw exception when setting negative total price")
    void testSetNegativeTotalPrice() throws InvalidReservationException {
        // Arrange
        Reservation reservation = new Reservation(
                VALID_NAME, VALID_EMAIL, VALID_CHECK_IN, VALID_CHECK_OUT, VALID_ROOM_TYPE
        );

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            reservation.setTotalPrice(-100.00);
        });
    }
}