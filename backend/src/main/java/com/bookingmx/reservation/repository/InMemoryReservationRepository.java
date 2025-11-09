package com.bookingmx.reservation.repository;

import com.bookingmx.reservation.model.Reservation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the ReservationRepository interface.
 * This implementation stores reservations in a thread-safe ConcurrentHashMap,
 * making it suitable for multi-threaded environments.
 *
 * Note: This is an in-memory implementation, so all data will be lost
 * when the application stops. For production use, consider implementing
 * a database-backed repository.
 *
 * Thread Safety: This implementation is thread-safe and can be safely
 * used in concurrent environments.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class InMemoryReservationRepository implements ReservationRepository {

    /**
     * Thread-safe map to store reservations by their ID.
     * Using ConcurrentHashMap ensures thread-safe operations without
     * external synchronization.
     */
    private final Map<String, Reservation> reservations;

    /**
     * Default constructor that initializes an empty repository.
     */
    public InMemoryReservationRepository() {
        this.reservations = new ConcurrentHashMap<>();
    }

    /**
     * Saves a new reservation to the repository.
     * If a reservation with the same ID already exists, it will be replaced.
     *
     * @param reservation the reservation to save
     * @return the saved reservation
     * @throws IllegalArgumentException if reservation is null
     */
    @Override
    public Reservation save(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservation.getId() == null || reservation.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be null or empty");
        }

        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    /**
     * Updates an existing reservation in the repository.
     *
     * @param reservation the reservation to update
     * @return the updated reservation
     * @throws IllegalArgumentException if reservation is null or doesn't exist
     */
    @Override
    public Reservation update(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservation.getId() == null || reservation.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be null or empty");
        }

        if (!reservations.containsKey(reservation.getId())) {
            throw new IllegalArgumentException(
                    "Cannot update: Reservation not found with ID: " + reservation.getId());
        }

        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    /**
     * Finds a reservation by its unique identifier.
     *
     * @param id the ID of the reservation to find
     * @return an Optional containing the reservation if found, empty otherwise
     * @throws IllegalArgumentException if id is null or empty
     */
    @Override
    public Optional<Reservation> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        return Optional.ofNullable(reservations.get(id));
    }

    /**
     * Retrieves all reservations from the repository.
     * Returns a new list to prevent external modification of internal storage.
     *
     * @return a list of all reservations (empty list if none exist)
     */
    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }

    /**
     * Deletes a reservation from the repository.
     *
     * @param id the ID of the reservation to delete
     * @return true if the reservation was deleted, false if it didn't exist
     * @throws IllegalArgumentException if id is null or empty
     */
    @Override
    public boolean delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        return reservations.remove(id) != null;
    }

    /**
     * Checks if a reservation exists with the given ID.
     *
     * @param id the ID to check
     * @return true if a reservation exists with this ID, false otherwise
     * @throws IllegalArgumentException if id is null or empty
     */
    @Override
    public boolean exists(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        return reservations.containsKey(id);
    }

    /**
     * Gets the total count of reservations in the repository.
     *
     * @return the number of reservations
     */
    @Override
    public int count() {
        return reservations.size();
    }

    /**
     * Removes all reservations from the repository.
     * Use with caution - this operation cannot be undone.
     */
    @Override
    public void clear() {
        reservations.clear();
    }

    /**
     * Returns a string representation of the repository's current state.
     * Useful for debugging and logging.
     *
     * @return a string showing the number of reservations stored
     */
    @Override
    public String toString() {
        return String.format("InMemoryReservationRepository{reservations=%d}",
                reservations.size());
    }
}