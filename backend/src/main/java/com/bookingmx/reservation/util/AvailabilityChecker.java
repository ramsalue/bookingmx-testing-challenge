package com.bookingmx.reservation.util;

import com.bookingmx.reservation.model.Reservation;
import com.bookingmx.reservation.model.ReservationStatus;
import com.bookingmx.reservation.model.RoomType;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for checking room availability in the reservation system.
 * This class handles all availability-related operations including checking
 * for date conflicts, managing room inventory, and validating capacity.
 *
 * All methods are static and the class cannot be instantiated.
 *
 * Business Rules:
 * - Each room type has a limited inventory
 * - SINGLE: 10 rooms available
 * - DOUBLE: 8 rooms available
 * - SUITE: 5 rooms available
 * - DELUXE: 3 rooms available
 * - Only active reservations (CONFIRMED, CHECKED_IN) count against inventory
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class AvailabilityChecker {

    /**
     * Number of SINGLE rooms available in the hotel.
     */
    public static final int SINGLE_ROOM_CAPACITY = 10;

    /**
     * Number of DOUBLE rooms available in the hotel.
     */
    public static final int DOUBLE_ROOM_CAPACITY = 8;

    /**
     * Number of SUITE rooms available in the hotel.
     */
    public static final int SUITE_ROOM_CAPACITY = 5;

    /**
     * Number of DELUXE rooms available in the hotel.
     */
    public static final int DELUXE_ROOM_CAPACITY = 3;

    /**
     * Private constructor to prevent instantiation.
     * This class should only be used through its static methods.
     */
    private AvailabilityChecker() {
        throw new UnsupportedOperationException("AvailabilityChecker is a utility class and cannot be instantiated");
    }

    /**
     * Gets the total capacity for a specific room type.
     *
     * @param roomType the type of room
     * @return the total number of rooms available for this type
     * @throws IllegalArgumentException if roomType is null
     */
    public static int getRoomCapacity(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }

        switch (roomType) {
            case SINGLE:
                return SINGLE_ROOM_CAPACITY;
            case DOUBLE:
                return DOUBLE_ROOM_CAPACITY;
            case SUITE:
                return SUITE_ROOM_CAPACITY;
            case DELUXE:
                return DELUXE_ROOM_CAPACITY;
            default:
                return 0;
        }
    }

    /**
     * Checks if a reservation is active and should count against inventory.
     * Only CONFIRMED and CHECKED_IN reservations are considered active.
     *
     * @param reservation the reservation to check
     * @return true if the reservation is active, false otherwise
     * @throws IllegalArgumentException if reservation is null
     */
    public static boolean isActiveReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }

        ReservationStatus status = reservation.getStatus();
        return status == ReservationStatus.CONFIRMED ||
                status == ReservationStatus.CHECKED_IN;
    }

    /**
     * Checks if two reservations have overlapping date ranges.
     *
     * @param reservation1 the first reservation
     * @param reservation2 the second reservation
     * @return true if the reservations overlap, false otherwise
     * @throws IllegalArgumentException if either reservation is null
     */
    public static boolean doReservationsOverlap(Reservation reservation1, Reservation reservation2) {
        if (reservation1 == null || reservation2 == null) {
            throw new IllegalArgumentException("Reservations cannot be null");
        }

        LocalDate start1 = reservation1.getCheckInDate();
        LocalDate end1 = reservation1.getCheckOutDate();
        LocalDate start2 = reservation2.getCheckInDate();
        LocalDate end2 = reservation2.getCheckOutDate();

        return DateValidator.doDateRangesOverlap(start1, end1, start2, end2);
    }

    /**
     * Filters a list of reservations to only include active ones for a specific room type.
     *
     * @param reservations the list of all reservations
     * @param roomType the room type to filter by
     * @return a list of active reservations for the specified room type
     * @throws IllegalArgumentException if reservations list or roomType is null
     */
    public static List<Reservation> getActiveReservationsForRoomType(
            List<Reservation> reservations, RoomType roomType) {
        if (reservations == null) {
            throw new IllegalArgumentException("Reservations list cannot be null");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }

        return reservations.stream()
                .filter(r -> r.getRoomType() == roomType)
                .filter(AvailabilityChecker::isActiveReservation)
                .collect(Collectors.toList());
    }

    /**
     * Counts how many rooms of a specific type are reserved for overlapping dates.
     *
     * @param existingReservations list of all existing reservations
     * @param roomType the type of room to check
     * @param checkInDate the check-in date for the new reservation
     * @param checkOutDate the check-out date for the new reservation
     * @return the number of rooms already reserved for overlapping dates
     * @throws IllegalArgumentException if any parameter is null
     */
    public static int countOverlappingReservations(List<Reservation> existingReservations,
                                                   RoomType roomType,
                                                   LocalDate checkInDate,
                                                   LocalDate checkOutDate) {
        if (existingReservations == null) {
            throw new IllegalArgumentException("Reservations list cannot be null");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        // Get active reservations for this room type
        List<Reservation> activeReservations = getActiveReservationsForRoomType(
                existingReservations, roomType);

        // Count how many overlap with the requested dates
        return (int) activeReservations.stream()
                .filter(reservation -> {
                    LocalDate existingCheckIn = reservation.getCheckInDate();
                    LocalDate existingCheckOut = reservation.getCheckOutDate();
                    return DateValidator.doDateRangesOverlap(
                            existingCheckIn, existingCheckOut, checkInDate, checkOutDate);
                })
                .count();
    }

    /**
     * Checks if a room of the specified type is available for the given dates.
     *
     * @param existingReservations list of all existing reservations
     * @param roomType the type of room to check
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return true if at least one room is available, false if fully booked
     * @throws IllegalArgumentException if any parameter is null
     */
    public static boolean isRoomAvailable(List<Reservation> existingReservations,
                                          RoomType roomType,
                                          LocalDate checkInDate,
                                          LocalDate checkOutDate) {
        if (existingReservations == null) {
            throw new IllegalArgumentException("Reservations list cannot be null");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        int capacity = getRoomCapacity(roomType);
        int overlappingReservations = countOverlappingReservations(
                existingReservations, roomType, checkInDate, checkOutDate);

        return overlappingReservations < capacity;
    }

    /**
     * Gets the number of rooms still available for a specific type and date range.
     *
     * @param existingReservations list of all existing reservations
     * @param roomType the type of room to check
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return the number of rooms available (0 if fully booked)
     * @throws IllegalArgumentException if any parameter is null
     */
    public static int getAvailableRoomCount(List<Reservation> existingReservations,
                                            RoomType roomType,
                                            LocalDate checkInDate,
                                            LocalDate checkOutDate) {
        if (existingReservations == null) {
            throw new IllegalArgumentException("Reservations list cannot be null");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        int capacity = getRoomCapacity(roomType);
        int overlappingReservations = countOverlappingReservations(
                existingReservations, roomType, checkInDate, checkOutDate);

        return Math.max(0, capacity - overlappingReservations);
    }

    /**
     * Checks availability for all room types and returns a report.
     *
     * @param existingReservations list of all existing reservations
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return an AvailabilityReport containing availability for all room types
     * @throws IllegalArgumentException if any parameter is null
     */
    public static AvailabilityReport getAvailabilityReport(List<Reservation> existingReservations,
                                                           LocalDate checkInDate,
                                                           LocalDate checkOutDate) {
        if (existingReservations == null) {
            throw new IllegalArgumentException("Reservations list cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        AvailabilityReport report = new AvailabilityReport(checkInDate, checkOutDate);

        for (RoomType roomType : RoomType.values()) {
            int available = getAvailableRoomCount(
                    existingReservations, roomType, checkInDate, checkOutDate);
            int capacity = getRoomCapacity(roomType);
            report.addRoomTypeAvailability(roomType, available, capacity);
        }

        return report;
    }

    /**
     * Validates that a new reservation can be created without conflicts.
     * This method checks both date validity and room availability.
     *
     * @param existingReservations list of all existing reservations
     * @param roomType the type of room being requested
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return a ValidationResult indicating if the reservation is valid
     * @throws IllegalArgumentException if any parameter is null
     */
    public static ValidationResult validateNewReservation(List<Reservation> existingReservations,
                                                          RoomType roomType,
                                                          LocalDate checkInDate,
                                                          LocalDate checkOutDate) {
        if (existingReservations == null) {
            throw new IllegalArgumentException("Reservations list cannot be null");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        // First check date validity
        if (!DateValidator.isValidDateRange(checkInDate, checkOutDate)) {
            String dateError = DateValidator.getDateValidationMessage(checkInDate, checkOutDate);
            return new ValidationResult(false, dateError);
        }

        // Then check room availability
        if (!isRoomAvailable(existingReservations, roomType, checkInDate, checkOutDate)) {
            int available = getAvailableRoomCount(existingReservations, roomType,
                    checkInDate, checkOutDate);
            String message = String.format(
                    "No %s rooms available for the selected dates. Available: %d",
                    roomType, available);
            return new ValidationResult(false, message);
        }

        return new ValidationResult(true, "Reservation can be created");
    }

    /**
     * Inner class representing an availability report for all room types.
     */
    public static class AvailabilityReport {
        private final LocalDate checkInDate;
        private final LocalDate checkOutDate;
        private final StringBuilder report;

        /**
         * Constructor for AvailabilityReport.
         *
         * @param checkInDate the check-in date for this report
         * @param checkOutDate the check-out date for this report
         */
        public AvailabilityReport(LocalDate checkInDate, LocalDate checkOutDate) {
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
            this.report = new StringBuilder();
            report.append(String.format("Availability Report for %s to %s:\n\n",
                    checkInDate, checkOutDate));
        }

        /**
         * Adds availability information for a specific room type.
         *
         * @param roomType the room type
         * @param available number of rooms available
         * @param capacity total capacity for this room type
         */
        public void addRoomTypeAvailability(RoomType roomType, int available, int capacity) {
            String status = available > 0 ? "Available" : "Fully Booked";
            report.append(String.format("  %-10s: %d/%d rooms available - %s\n",
                    roomType, available, capacity, status));
        }

        @Override
        public String toString() {
            return report.toString();
        }
    }

    /**
     * Inner class representing the result of a validation check.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        /**
         * Constructor for ValidationResult.
         *
         * @param valid whether the validation passed
         * @param message descriptive message about the validation result
         */
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        /**
         * Gets whether the validation was successful.
         *
         * @return true if valid, false otherwise
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Gets the validation message.
         *
         * @return the descriptive message
         */
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format("Valid: %s - %s", valid, message);
        }
    }
}