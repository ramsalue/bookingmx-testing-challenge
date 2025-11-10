package com.bookingmx.reservation.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for validating dates in the reservation system.
 * This class provides static methods for common date validation operations
 * such as checking if dates are in the past, validating date ranges,
 * and ensuring business rules are met.
 *
 * All methods are static and the class cannot be instantiated.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class DateValidator {

    /**
     * Minimum number of nights required for a reservation.
     * This constant defines the business rule for minimum stay duration.
     */
    public static final int MINIMUM_NIGHTS = 1;

    /**
     * Maximum number of days in advance a reservation can be made.
     * This constant limits how far into the future reservations can be created.
     */
    public static final int MAXIMUM_ADVANCE_DAYS = 365;

    /**
     * Private constructor to prevent instantiation.
     * This class should only be used through its static methods.
     */
    private DateValidator() {
        throw new UnsupportedOperationException("DateValidator is a utility class and cannot be instantiated");
    }

    /**
     * Checks if a given date is in the past (before today).
     *
     * @param date the date to check
     * @return true if the date is before today, false otherwise
     * @throws IllegalArgumentException if date is null
     */
    public static boolean isPastDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.isBefore(LocalDate.now());
    }

    /**
     * Checks if a given date is today.
     *
     * @param date the date to check
     * @return true if the date is today, false otherwise
     * @throws IllegalArgumentException if date is null
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.isEqual(LocalDate.now());
    }

    /**
     * Checks if a given date is in the future (after today).
     *
     * @param date the date to check
     * @return true if the date is after today, false otherwise
     * @throws IllegalArgumentException if date is null
     */
    public static boolean isFutureDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.isAfter(LocalDate.now());
    }

    /**
     * Validates that a check-in date is valid for a new reservation.
     * A valid check-in date must be today or in the future, and not exceed
     * the maximum advance booking period.
     *
     * @param checkInDate the check-in date to validate
     * @return true if the check-in date is valid, false otherwise
     * @throws IllegalArgumentException if checkInDate is null
     */
    public static boolean isValidCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }

        // Check if date is not in the past
        if (checkInDate.isBefore(LocalDate.now())) {
            return false;
        }

        // Check if date is not too far in the future
        long daysInAdvance = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
        return daysInAdvance <= MAXIMUM_ADVANCE_DAYS;
    }

    /**
     * Validates that a check-out date is after the check-in date.
     * The check-out must be at least one day after check-in to meet
     * the minimum nights requirement.
     *
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return true if check-out is validly after check-in, false otherwise
     * @throws IllegalArgumentException if either date is null
     */
    public static boolean isValidCheckOutDate(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }
        if (checkOutDate == null) {
            throw new IllegalArgumentException("Check-out date cannot be null");
        }

        return checkOutDate.isAfter(checkInDate);
    }

    /**
     * Validates that a date range meets the minimum nights requirement.
     *
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return true if the date range meets minimum nights, false otherwise
     * @throws IllegalArgumentException if either date is null
     */
    public static boolean meetsMinimumNights(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return nights >= MINIMUM_NIGHTS;
    }

    /**
     * Validates a complete date range for a reservation.
     * This method combines all date validation rules:
     * - Check-in date must be valid (not in past, not too far in future)
     * - Check-out date must be after check-in date
     * - Date range must meet minimum nights requirement
     *
     * @param checkInDate the check-in date to validate
     * @param checkOutDate the check-out date to validate
     * @return true if the entire date range is valid, false otherwise
     * @throws IllegalArgumentException if either date is null
     */
    public static boolean isValidDateRange(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        return isValidCheckInDate(checkInDate)
                && isValidCheckOutDate(checkInDate, checkOutDate)
                && meetsMinimumNights(checkInDate, checkOutDate);
    }

    /**
     * Calculates the number of nights between two dates.
     *
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return the number of nights as a long value
     * @throws IllegalArgumentException if either date is null or checkout is before checkin
     */
    public static long calculateNights(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (checkOutDate.isBefore(checkInDate)) {
            throw new IllegalArgumentException("Check-out date cannot be before check-in date");
        }

        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    /**
     * Checks if two date ranges overlap.
     * This is useful for checking room availability - if a room has an existing
     * reservation, we need to ensure new reservations don't overlap.
     *
     * @param start1 start date of first range
     * @param end1 end date of first range
     * @param start2 start date of second range
     * @param end2 end date of second range
     * @return true if the date ranges overlap, false otherwise
     * @throws IllegalArgumentException if any date is null
     */
    public static boolean doDateRangesOverlap(LocalDate start1, LocalDate end1,
                                              LocalDate start2, LocalDate end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        // Two ranges overlap if one starts before the other ends
        // and the other starts before the first ends
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Gets a descriptive validation message for invalid date ranges.
     * This method provides user-friendly error messages for different
     * date validation failures.
     *
     * @param checkInDate the check-in date to validate
     * @param checkOutDate the check-out date to validate
     * @return a descriptive error message, or null if dates are valid
     */
    public static String getDateValidationMessage(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null) {
            return "Check-in date is required";
        }

        if (checkOutDate == null) {
            return "Check-out date is required";
        }

        if (isPastDate(checkInDate)) {
            return "Check-in date cannot be in the past";
        }

        long daysInAdvance = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
        if (daysInAdvance > MAXIMUM_ADVANCE_DAYS) {
            return String.format("Check-in date cannot be more than %d days in advance",
                    MAXIMUM_ADVANCE_DAYS);
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            return "Check-out date must be after check-in date";
        }

        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights < MINIMUM_NIGHTS) {
            return String.format("Reservation must be for at least %d night(s)", MINIMUM_NIGHTS);
        }

        return null; // No validation errors
    }

    /**
     * Formats a date range as a readable string.
     *
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return a formatted string representing the date range
     * @throws IllegalArgumentException if either date is null
     */
    public static String formatDateRange(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        long nights = calculateNights(checkInDate, checkOutDate);
        return String.format("%s to %s (%d night%s)",
                checkInDate,
                checkOutDate,
                nights,
                nights == 1 ? "" : "s");
    }
}