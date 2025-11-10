package com.bookingmx.reservation.repository;

import com.bookingmx.reservation.model.Reservation;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing reservation data persistence.
 * This interface defines the contract for data access operations
 * related to reservations.
 *
 * Implementations of this interface should handle data storage and retrieval,
 * whether from memory, database, file system, or other storage mechanisms.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public interface ReservationRepository {

    /**
     * Saves a new reservation to the repository.
     * If a reservation with the same ID already exists, it will be updated.
     *
     * @param reservation the reservation to save
     * @return the saved reservation
     * @throws IllegalArgumentException if reservation is null
     */
    Reservation save(Reservation reservation);

    /**
     * Updates an existing reservation in the repository.
     *
     * @param reservation the reservation to update
     * @return the updated reservation
     * @throws IllegalArgumentException if reservation is null or doesn't exist
     */
    Reservation update(Reservation reservation);

    /**
     * Finds a reservation by its unique identifier.
     *
     * @param id the ID of the reservation to find
     * @return an Optional containing the reservation if found, empty otherwise
     * @throws IllegalArgumentException if id is null or empty
     */
    Optional<Reservation> findById(String id);

    /**
     * Retrieves all reservations from the repository.
     *
     * @return a list of all reservations (empty list if none exist)
     */
    List<Reservation> findAll();

    /**
     * Deletes a reservation from the repository.
     *
     * @param id the ID of the reservation to delete
     * @return true if the reservation was deleted, false if it didn't exist
     * @throws IllegalArgumentException if id is null or empty
     */
    boolean delete(String id);

    /**
     * Checks if a reservation exists with the given ID.
     *
     * @param id the ID to check
     * @return true if a reservation exists with this ID, false otherwise
     * @throws IllegalArgumentException if id is null or empty
     */
    boolean exists(String id);

    /**
     * Gets the total count of reservations in the repository.
     *
     * @return the number of reservations
     */
    int count();

    /**
     * Removes all reservations from the repository.
     * Use with caution - this operation cannot be undone.
     */
    void clear();
}