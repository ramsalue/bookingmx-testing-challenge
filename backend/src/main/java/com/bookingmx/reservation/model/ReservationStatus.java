package com.bookingmx.reservation.model;

/**
 * Enumeration representing the possible states of a reservation.
 * Used to track the lifecycle of a reservation from creation to completion or cancellation.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public enum ReservationStatus {

    /**
     * Reservation has been created but not yet confirmed by payment.
     */
    PENDING,

    /**
     * Reservation has been confirmed and payment received.
     */
    CONFIRMED,

    /**
     * Guest has checked in and is currently staying at the hotel.
     */
    CHECKED_IN,

    /**
     * Guest has checked out and the reservation is complete.
     */
    COMPLETED,

    /**
     * Reservation has been cancelled by the guest or hotel.
     */
    CANCELLED
}