package com.bookingmx.reservation.service;

import com.bookingmx.reservation.exception.InvalidReservationException;
import com.bookingmx.reservation.exception.ReservationNotFoundException;
import com.bookingmx.reservation.model.Reservation;
import com.bookingmx.reservation.model.ReservationStatus;
import com.bookingmx.reservation.model.RoomType;
import com.bookingmx.reservation.repository.InMemoryReservationRepository;
import com.bookingmx.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ReservationService class.
 * These tests verify the business logic layer including reservation creation,
 * updates, cancellations, and search operations.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
@DisplayName("ReservationService Tests")
class ReservationServiceTest {

    private ReservationService service;
    private ReservationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReservationRepository();
        service = new ReservationService(repository);
    }

    @Test
    @DisplayName("Should throw exception when creating service with null repository")
    void testConstructorWithNullRepository() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ReservationService(null);
        });
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void testCreateReservation() throws InvalidReservationException {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        // Act
        Reservation reservation = service.createReservation(
                "John Doe",
                "john@example.com",
                checkIn,
                checkOut,
                RoomType.DOUBLE
        );

        // Assert
        assertNotNull(reservation);
        assertNotNull(reservation.getId());
        assertEquals("John Doe", reservation.getGuestName());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertTrue(reservation.getTotalPrice() > 0);
    }

    @Test
    @DisplayName("Should throw exception for null guest name")
    void testCreateReservationNullName() {
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        assertThrows(InvalidReservationException.class, () -> {
            service.createReservation(null, "test@example.com",
                    checkIn, checkOut, RoomType.SINGLE);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty guest name")
    void testCreateReservationEmptyName() {
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        assertThrows(InvalidReservationException.class, () -> {
            service.createReservation("", "test@example.com",
                    checkIn, checkOut, RoomType.SINGLE);
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid email")
    void testCreateReservationInvalidEmail() {
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        assertThrows(InvalidReservationException.class, () -> {
            service.createReservation("John Doe", "invalid-email",
                    checkIn, checkOut, RoomType.SINGLE);
        });
    }

    @Test
    @DisplayName("Should throw exception for past check-in date")
    void testCreateReservationPastDate() {
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        assertThrows(InvalidReservationException.class, () -> {
            service.createReservation("John Doe", "john@example.com",
                    checkIn, checkOut, RoomType.SINGLE);
        });
    }

    @Test
    @DisplayName("Should find reservation by ID")
    void testFindReservationById() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange
        Reservation created = service.createReservation(
                "John Doe",
                "john@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.DOUBLE
        );

        // Act
        Reservation found = service.findReservationById(created.getId());

        // Assert
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getGuestName(), found.getGuestName());
    }

    @Test
    @DisplayName("Should throw exception when reservation not found")
    void testFindReservationByIdNotFound() {
        assertThrows(ReservationNotFoundException.class, () -> {
            service.findReservationById("non-existent-id");
        });
    }

    @Test
    @DisplayName("Should get all reservations")
    void testGetAllReservations() throws InvalidReservationException {
        // Arrange
        service.createReservation("Guest 1", "guest1@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.SINGLE);
        service.createReservation("Guest 2", "guest2@example.com",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(18), RoomType.DOUBLE);

        // Act
        List<Reservation> all = service.getAllReservations();

        // Assert
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Should confirm pending reservation")
    void testConfirmReservation() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange
        Reservation created = service.createReservation(
                "John Doe",
                "john@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.DOUBLE
        );
        assertEquals(ReservationStatus.PENDING, created.getStatus());

        // Act
        Reservation confirmed = service.confirmReservation(created.getId());

        // Assert
        assertEquals(ReservationStatus.CONFIRMED, confirmed.getStatus());
    }

    @Test
    @DisplayName("Should cancel reservation")
    void testCancelReservation() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange
        Reservation created = service.createReservation(
                "John Doe",
                "john@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.DOUBLE
        );
        service.confirmReservation(created.getId());

        // Act
        Reservation cancelled = service.cancelReservation(created.getId());

        // Assert
        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    @DisplayName("Should update reservation dates")
    void testUpdateReservationDates() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange
        Reservation created = service.createReservation(
                "John Doe",
                "john@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.DOUBLE
        );
        LocalDate newCheckIn = LocalDate.now().plusDays(14);
        LocalDate newCheckOut = LocalDate.now().plusDays(17);

        // Act
        Reservation updated = service.updateReservationDates(
                created.getId(), newCheckIn, newCheckOut);

        // Assert
        assertEquals(newCheckIn, updated.getCheckInDate());
        assertEquals(newCheckOut, updated.getCheckOutDate());
    }

    @Test
    @DisplayName("Should throw exception when updating to past date")
    void testUpdateReservationDatesToPast() throws InvalidReservationException {
        // Arrange
        Reservation created = service.createReservation(
                "John Doe",
                "john@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.DOUBLE
        );
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            service.updateReservationDates(created.getId(), pastDate,
                    LocalDate.now().plusDays(2));
        });
    }

    @Test
    @DisplayName("Should find reservations by guest name")
    void testFindReservationsByGuestName() throws InvalidReservationException {
        // Arrange
        service.createReservation("John Doe", "john@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.SINGLE);
        service.createReservation("Jane Smith", "jane@example.com",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(18), RoomType.DOUBLE);
        service.createReservation("John Smith", "johnsmith@example.com",
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(23), RoomType.SUITE);

        // Act
        List<Reservation> johns = service.findReservationsByGuestName("John");

        // Assert
        assertEquals(2, johns.size());
        assertTrue(johns.stream().allMatch(r -> r.getGuestName().contains("John")));
    }

    @Test
    @DisplayName("Should find reservations by email")
    void testFindReservationsByEmail() throws InvalidReservationException {
        // Arrange
        service.createReservation("John Doe", "john@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.SINGLE);
        service.createReservation("Jane Smith", "jane@example.com",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(18), RoomType.DOUBLE);

        // Act
        List<Reservation> found = service.findReservationsByGuestEmail("john@example.com");

        // Assert
        assertEquals(1, found.size());
        assertEquals("john@example.com", found.get(0).getGuestEmail());
    }

    @Test
    @DisplayName("Should find reservations by status")
    void testFindReservationsByStatus() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange
        Reservation res1 = service.createReservation("Guest 1", "guest1@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.SINGLE);
        Reservation res2 = service.createReservation("Guest 2", "guest2@example.com",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(18), RoomType.DOUBLE);
        service.confirmReservation(res1.getId());

        // Act
        List<Reservation> confirmed = service.findReservationsByStatus(ReservationStatus.CONFIRMED);
        List<Reservation> pending = service.findReservationsByStatus(ReservationStatus.PENDING);

        // Assert
        assertEquals(1, confirmed.size());
        assertEquals(1, pending.size());
    }

    @Test
    @DisplayName("Should find reservations by room type")
    void testFindReservationsByRoomType() throws InvalidReservationException {
        // Arrange
        service.createReservation("Guest 1", "guest1@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.DOUBLE);
        service.createReservation("Guest 2", "guest2@example.com",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(18), RoomType.DOUBLE);
        service.createReservation("Guest 3", "guest3@example.com",
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(23), RoomType.SUITE);

        // Act
        List<Reservation> doubles = service.findReservationsByRoomType(RoomType.DOUBLE);

        // Assert
        assertEquals(2, doubles.size());
        assertTrue(doubles.stream().allMatch(r -> r.getRoomType() == RoomType.DOUBLE));
    }

    @Test
    @DisplayName("Should find reservations for date range")
    void testFindReservationsForDateRange() throws InvalidReservationException {
        // Arrange
        service.createReservation("Guest 1", "guest1@example.com",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), RoomType.DOUBLE);
        service.createReservation("Guest 2", "guest2@example.com",
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(25), RoomType.SUITE);

        // Act
        List<Reservation> overlapping = service.findReservationsForDateRange(
                LocalDate.now().plusDays(12), LocalDate.now().plusDays(17));

        // Assert
        assertEquals(1, overlapping.size());
        assertEquals("Guest 1", overlapping.get(0).getGuestName());
    }

    @Test
    @DisplayName("Should check room availability")
    void testIsRoomAvailable() throws InvalidReservationException {
        // Arrange
        service.createReservation("Guest 1", "guest1@example.com",
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), RoomType.DOUBLE);

        // Act
        boolean availableNow = service.isRoomAvailable(RoomType.DOUBLE,
                LocalDate.now().plusDays(20), LocalDate.now().plusDays(23));
        boolean availableOverlapping = service.isRoomAvailable(RoomType.DOUBLE,
                LocalDate.now().plusDays(12), LocalDate.now().plusDays(17));

        // Assert
        assertTrue(availableNow);
        assertTrue(availableOverlapping); // Still have capacity (8 DOUBLE rooms)
    }

    @Test
    @DisplayName("Should get available room count")
    void testGetAvailableRoomCount() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(15);

        Reservation res1 = service.createReservation("Guest 1", "guest1@example.com",
                checkIn, checkOut, RoomType.DOUBLE);
        Reservation res2 = service.createReservation("Guest 2", "guest2@example.com",
                checkIn, checkOut, RoomType.DOUBLE);

        service.confirmReservation(res1.getId());
        service.confirmReservation(res2.getId());

        // Act
        int available = service.getAvailableRoomCount(RoomType.DOUBLE, checkIn, checkOut);

        // Assert
        assertEquals(6, available); // 8 total - 2 booked = 6
    }

    @Test
    @DisplayName("Should get price quote")
    void testGetPriceQuote() throws InvalidReservationException {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        // Act
        var quote = service.getPriceQuote(RoomType.DOUBLE, checkIn, checkOut);

        // Assert
        assertNotNull(quote);
        assertTrue(quote.getBasePrice() > 0);
        assertTrue(quote.getTotalPrice() > 0);
    }

    @Test
    @DisplayName("Should get reservation summary")
    void testGetReservationSummary() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange
        Reservation res1 = service.createReservation("Guest 1", "guest1@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.SINGLE);
        Reservation res2 = service.createReservation("Guest 2", "guest2@example.com",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(18), RoomType.DOUBLE);

        service.confirmReservation(res1.getId());

        // Act
        String summary = service.getReservationSummary();

        // Assert
        assertNotNull(summary);
        assertTrue(summary.contains("Total: 2"));
        assertTrue(summary.contains("Pending: 1"));
        assertTrue(summary.contains("Confirmed: 1"));
    }

    @Test
    @DisplayName("Should delete reservation")
    void testDeleteReservation() throws InvalidReservationException {
        // Arrange
        Reservation created = service.createReservation("John Doe", "john@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.SINGLE);

        // Act
        boolean deleted = service.deleteReservation(created.getId());

        // Assert
        assertTrue(deleted);
        assertEquals(0, service.getTotalReservationCount());
    }

    @Test
    @DisplayName("Should return false when deleting non-existent reservation")
    void testDeleteNonExistentReservation() {
        // Act
        boolean deleted = service.deleteReservation("non-existent-id");

        // Assert
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Should get total reservation count")
    void testGetTotalReservationCount() throws InvalidReservationException {
        // Arrange
        service.createReservation("Guest 1", "guest1@example.com",
                LocalDate.now().plusDays(7), LocalDate.now().plusDays(10), RoomType.SINGLE);
        service.createReservation("Guest 2", "guest2@example.com",
                LocalDate.now().plusDays(15), LocalDate.now().plusDays(18), RoomType.DOUBLE);

        // Act
        int count = service.getTotalReservationCount();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should reject reservation when room is not available")
    void testCreateReservationNoAvailability() throws InvalidReservationException, ReservationNotFoundException {
        // Arrange - Fill DELUXE capacity (3 rooms)
        LocalDate checkIn = LocalDate.now().plusDays(50);
        LocalDate checkOut = LocalDate.now().plusDays(53);

        for (int i = 0; i < 3; i++) {
            Reservation res = service.createReservation(
                    "Guest " + i,
                    "guest" + i + "@example.com",
                    checkIn,
                    checkOut,
                    RoomType.DELUXE
            );
            service.confirmReservation(res.getId());
        }

        // Act & Assert
        assertThrows(InvalidReservationException.class, () -> {
            service.createReservation(
                    "Guest 4",
                    "guest4@example.com",
                    checkIn,
                    checkOut,
                    RoomType.DELUXE
            );
        });
    }
    @Test
    @DisplayName("Should return empty list for null or empty guest name")
    void testFindReservationsByGuestName_NullOrEmpty() {
        // Act
        List<Reservation> r1 = service.findReservationsByGuestName(null);
        List<Reservation> r2 = service.findReservationsByGuestName("   ");

        // Assert
        assertTrue(r1.isEmpty());
        assertTrue(r2.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null or empty guest email")
    void testFindReservationsByGuestEmail_NullOrEmpty() {
        // Act
        List<Reservation> r1 = service.findReservationsByGuestEmail(null);
        List<Reservation> r2 = service.findReservationsByGuestEmail("   ");

        // Assert
        assertTrue(r1.isEmpty());
        assertTrue(r2.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null status")
    void testFindReservationsByStatus_Null() {
        // Act
        List<Reservation> r1 = service.findReservationsByStatus(null);

        // Assert
        assertTrue(r1.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null room type")
    void testFindReservationsByRoomType_Null() {
        // Act
        List<Reservation> r1 = service.findReservationsByRoomType(null);

        // Assert
        assertTrue(r1.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for null date range")
    void testFindReservationsForDateRange_Nulls() {
        // Act
        List<Reservation> r1 = service.findReservationsForDateRange(null, LocalDate.now());
        List<Reservation> r2 = service.findReservationsForDateRange(LocalDate.now(), null);

        // Assert
        assertTrue(r1.isEmpty());
        assertTrue(r2.isEmpty());
    }
}