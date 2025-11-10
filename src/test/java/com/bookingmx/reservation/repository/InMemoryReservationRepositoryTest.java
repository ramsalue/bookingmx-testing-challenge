package com.bookingmx.reservation.repository;

import com.bookingmx.reservation.exception.InvalidReservationException;
import com.bookingmx.reservation.model.Reservation;
import com.bookingmx.reservation.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InMemoryReservationRepository class.
 * These tests verify the data persistence layer functionality
 * including CRUD operations and data consistency.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
@DisplayName("InMemoryReservationRepository Tests")
class InMemoryReservationRepositoryTest {

    private InMemoryReservationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReservationRepository();
    }

    @Test
    @DisplayName("Should save reservation successfully")
    void testSaveReservation() throws InvalidReservationException {
        // Arrange
        Reservation reservation = createTestReservation();

        // Act
        Reservation saved = repository.save(reservation);

        // Assert
        assertNotNull(saved);
        assertEquals(reservation.getId(), saved.getId());
    }

    @Test
    @DisplayName("Should throw exception when saving null reservation")
    void testSaveNullReservation() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    @DisplayName("Should find reservation by ID")
    void testFindById() throws InvalidReservationException {
        // Arrange
        Reservation reservation = createTestReservation();
        repository.save(reservation);

        // Act
        Optional<Reservation> found = repository.findById(reservation.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(reservation.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Should return empty when reservation not found")
    void testFindByIdNotFound() {
        // Act
        Optional<Reservation> found = repository.findById("non-existent-id");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception for null ID in findById")
    void testFindByIdNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.findById(null);
        });
    }

    @Test
    @DisplayName("Should find all reservations")
    void testFindAll() throws InvalidReservationException {
        // Arrange
        repository.save(createTestReservation());
        repository.save(createTestReservation());
        repository.save(createTestReservation());

        // Act
        List<Reservation> all = repository.findAll();

        // Assert
        assertEquals(3, all.size());
    }

    @Test
    @DisplayName("Should return empty list when no reservations exist")
    void testFindAllEmpty() {
        // Act
        List<Reservation> all = repository.findAll();

        // Assert
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    @DisplayName("Should update existing reservation")
    void testUpdate() throws InvalidReservationException {
        // Arrange
        Reservation reservation = createTestReservation();
        repository.save(reservation);
        reservation.setGuestName("Updated Name");

        // Act
        Reservation updated = repository.update(reservation);

        // Assert
        assertEquals("Updated Name", updated.getGuestName());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent reservation")
    void testUpdateNonExistent() throws InvalidReservationException {
        // Arrange
        Reservation reservation = createTestReservation();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repository.update(reservation);
        });
    }

    @Test
    @DisplayName("Should delete reservation")
    void testDelete() throws InvalidReservationException {
        // Arrange
        Reservation reservation = createTestReservation();
        repository.save(reservation);

        // Act
        boolean deleted = repository.delete(reservation.getId());

        // Assert
        assertTrue(deleted);
        assertFalse(repository.exists(reservation.getId()));
    }

    @Test
    @DisplayName("Should return false when deleting non-existent reservation")
    void testDeleteNonExistent() {
        // Act
        boolean deleted = repository.delete("non-existent-id");

        // Assert
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Should check if reservation exists")
    void testExists() throws InvalidReservationException {
        // Arrange
        Reservation reservation = createTestReservation();
        repository.save(reservation);

        // Act & Assert
        assertTrue(repository.exists(reservation.getId()));
        assertFalse(repository.exists("non-existent-id"));
    }

    @Test
    @DisplayName("Should count reservations")
    void testCount() throws InvalidReservationException {
        // Arrange
        repository.save(createTestReservation());
        repository.save(createTestReservation());

        // Act
        int count = repository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should clear all reservations")
    void testClear() throws InvalidReservationException {
        // Arrange
        repository.save(createTestReservation());
        repository.save(createTestReservation());

        // Act
        repository.clear();

        // Assert
        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void testToString() {
        // Act
        String toString = repository.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("InMemoryReservationRepository"));
        assertTrue(toString.contains("reservations="));
    }

    private Reservation createTestReservation() throws InvalidReservationException {
        return new Reservation(
                "Test Guest",
                "test@example.com",
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10),
                RoomType.DOUBLE
        );
    }

    @Test
    @DisplayName("Should throw exception when updating null reservation")
    void testUpdateNullReservation() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.update(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting null ID")
    void testDeleteNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.delete(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when checking existence of null ID")
    void testExistsNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.exists(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when saving reservation with null ID")
    void testSaveReservationWithNullId() throws InvalidReservationException {
        // Arrange
        Reservation res = createTestReservation();
        res.setId(null); // Forzar un ID nulo

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(res);
        });
    }
}