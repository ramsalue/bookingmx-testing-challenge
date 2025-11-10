package com.bookingmx.reservation.exception;

/**
 * Custom exception thrown when a requested reservation cannot be found.
 * This exception is typically used when searching for a reservation by ID
 * and the reservation does not exist in the system.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class ReservationNotFoundException extends Exception {

    /**
     * Constructs a new ReservationNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which reservation was not found
     */
    public ReservationNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ReservationNotFoundException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining which reservation was not found
     * @param cause the underlying cause of this exception
     */
    public ReservationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}