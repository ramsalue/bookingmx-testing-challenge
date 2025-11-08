package com.bookingmx.reservation.exception;

/**
 * Custom exception thrown when reservation data is invalid.
 * This exception is used to indicate problems with reservation parameters
 * such as invalid dates, missing required fields, or business rule violations.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class InvalidReservationException extends Exception {

    /**
     * Constructs a new InvalidReservationException with the specified detail message.
     *
     * @param message the detail message explaining why the reservation is invalid
     */
    public InvalidReservationException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidReservationException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining why the reservation is invalid
     * @param cause the underlying cause of this exception
     */
    public InvalidReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}