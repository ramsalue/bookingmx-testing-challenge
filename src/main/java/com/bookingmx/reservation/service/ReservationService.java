package com.bookingmx.reservation.service;

import com.bookingmx.reservation.exception.InvalidReservationException;
import com.bookingmx.reservation.exception.ReservationNotFoundException;
import com.bookingmx.reservation.model.Reservation;
import com.bookingmx.reservation.model.ReservationStatus;
import com.bookingmx.reservation.model.RoomType;
import com.bookingmx.reservation.repository.ReservationRepository;
import com.bookingmx.reservation.util.AvailabilityChecker;
import com.bookingmx.reservation.util.DateValidator;
import com.bookingmx.reservation.util.PriceCalculator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing hotel reservations.
 * This class orchestrates the business logic for creating, updating, canceling,
 * and searching reservations. It integrates all utility classes and the repository
 * to provide a comprehensive reservation management system.
 *
 * This service handles:
 * - Reservation creation with validation
 * - Availability checking before booking
 * - Price calculation with discounts
 * - Reservation cancellation and confirmation
 * - Search and filtering operations
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class ReservationService {

    private final ReservationRepository repository;

    /**
     * Constructor that initializes the service with a repository.
     *
     * @param repository the repository for data persistence
     */
    public ReservationService(ReservationRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    /**
     * Creates a new reservation with full validation.
     * This method performs the following checks:
     * 1. Validates guest information
     * 2. Validates date range
     * 3. Checks room availability
     * 4. Calculates price with discounts
     * 5. Saves the reservation
     *
     * @param guestName the name of the guest
     * @param guestEmail the email of the guest
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @param roomType the type of room requested
     * @return the created reservation
     * @throws InvalidReservationException if validation fails or room is not available
     */
    public Reservation createReservation(String guestName, String guestEmail,
                                         LocalDate checkInDate, LocalDate checkOutDate,
                                         RoomType roomType) throws InvalidReservationException {
        // Validate guest information
        validateGuestInfo(guestName, guestEmail);

        // Validate dates
        if (!DateValidator.isValidDateRange(checkInDate, checkOutDate)) {
            String errorMessage = DateValidator.getDateValidationMessage(checkInDate, checkOutDate);
            throw new InvalidReservationException(errorMessage);
        }

        // Check room availability
        List<Reservation> existingReservations = repository.findAll();
        AvailabilityChecker.ValidationResult validationResult =
                AvailabilityChecker.validateNewReservation(existingReservations, roomType,
                        checkInDate, checkOutDate);

        if (!validationResult.isValid()) {
            throw new InvalidReservationException(validationResult.getMessage());
        }

        // Create reservation with calculated price
        double totalPrice = PriceCalculator.calculateTotalPrice(roomType, checkInDate, checkOutDate);
        Reservation reservation = new Reservation(guestName, guestEmail, checkInDate,
                checkOutDate, roomType, totalPrice);

        // Save and return
        return repository.save(reservation);
    }

    /**
     * Validates guest information (name and email).
     *
     * @param guestName the guest name to validate
     * @param guestEmail the guest email to validate
     * @throws InvalidReservationException if validation fails
     */
    private void validateGuestInfo(String guestName, String guestEmail)
            throws InvalidReservationException {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidReservationException("Guest name is required");
        }
        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            throw new InvalidReservationException("Guest email is required");
        }
        if (!guestEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new InvalidReservationException("Invalid email format");
        }
    }

    /**
     * Finds a reservation by its ID.
     *
     * @param reservationId the ID of the reservation to find
     * @return the reservation if found
     * @throws ReservationNotFoundException if no reservation exists with the given ID
     */
    public Reservation findReservationById(String reservationId)
            throws ReservationNotFoundException {
        return repository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(
                        "Reservation not found with ID: " + reservationId));
    }

    /**
     * Retrieves all reservations in the system.
     *
     * @return a list of all reservations
     */
    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    /**
     * Confirms a pending reservation.
     * Only reservations in PENDING status can be confirmed.
     *
     * @param reservationId the ID of the reservation to confirm
     * @return the confirmed reservation
     * @throws ReservationNotFoundException if reservation doesn't exist
     * @throws InvalidReservationException if reservation cannot be confirmed
     */
    public Reservation confirmReservation(String reservationId)
            throws ReservationNotFoundException, InvalidReservationException {
        Reservation reservation = findReservationById(reservationId);
        reservation.confirm();
        return repository.update(reservation);
    }

    /**
     * Cancels an existing reservation.
     *
     * @param reservationId the ID of the reservation to cancel
     * @return the cancelled reservation
     * @throws ReservationNotFoundException if reservation doesn't exist
     * @throws InvalidReservationException if reservation cannot be cancelled
     */
    public Reservation cancelReservation(String reservationId)
            throws ReservationNotFoundException, InvalidReservationException {
        Reservation reservation = findReservationById(reservationId);
        reservation.cancel();
        return repository.update(reservation);
    }

    /**
     * Updates a reservation's dates.
     * This method checks availability for the new dates before updating.
     *
     * @param reservationId the ID of the reservation to update
     * @param newCheckInDate the new check-in date
     * @param newCheckOutDate the new check-out date
     * @return the updated reservation
     * @throws ReservationNotFoundException if reservation doesn't exist
     * @throws InvalidReservationException if new dates are invalid or room not available
     */
    public Reservation updateReservationDates(String reservationId,
                                              LocalDate newCheckInDate,
                                              LocalDate newCheckOutDate)
            throws ReservationNotFoundException, InvalidReservationException {
        Reservation reservation = findReservationById(reservationId);

        // Validate new dates
        if (!DateValidator.isValidDateRange(newCheckInDate, newCheckOutDate)) {
            String errorMessage = DateValidator.getDateValidationMessage(newCheckInDate, newCheckOutDate);
            throw new InvalidReservationException(errorMessage);
        }

        // Check availability for new dates (excluding this reservation)
        List<Reservation> otherReservations = repository.findAll().stream()
                .filter(r -> !r.getId().equals(reservationId))
                .collect(Collectors.toList());

        AvailabilityChecker.ValidationResult validationResult =
                AvailabilityChecker.validateNewReservation(otherReservations,
                        reservation.getRoomType(),
                        newCheckInDate, newCheckOutDate);

        if (!validationResult.isValid()) {
            throw new InvalidReservationException(validationResult.getMessage());
        }

        // Update dates and recalculate price
        reservation.setCheckInDate(newCheckInDate);
        reservation.setCheckOutDate(newCheckOutDate);
        double newPrice = PriceCalculator.calculateTotalPrice(
                reservation.getRoomType(), newCheckInDate, newCheckOutDate);
        reservation.setTotalPrice(newPrice);

        return repository.update(reservation);
    }

    /**
     * Searches for reservations by guest name.
     *
     * @param guestName the name to search for (case-insensitive, partial match)
     * @return list of matching reservations
     */
    public List<Reservation> findReservationsByGuestName(String guestName) {
        if (guestName == null || guestName.trim().isEmpty()) {
            return List.of();
        }

        String searchTerm = guestName.toLowerCase();
        return repository.findAll().stream()
                .filter(r -> r.getGuestName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Searches for reservations by guest email.
     *
     * @param guestEmail the email to search for (case-insensitive)
     * @return list of matching reservations
     */
    public List<Reservation> findReservationsByGuestEmail(String guestEmail) {
        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            return List.of();
        }

        String searchTerm = guestEmail.toLowerCase();
        return repository.findAll().stream()
                .filter(r -> r.getGuestEmail().toLowerCase().equals(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Finds all reservations with a specific status.
     *
     * @param status the status to filter by
     * @return list of reservations with the given status
     */
    public List<Reservation> findReservationsByStatus(ReservationStatus status) {
        if (status == null) {
            return List.of();
        }

        return repository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Finds all reservations for a specific room type.
     *
     * @param roomType the room type to filter by
     * @return list of reservations for the given room type
     */
    public List<Reservation> findReservationsByRoomType(RoomType roomType) {
        if (roomType == null) {
            return List.of();
        }

        return repository.findAll().stream()
                .filter(r -> r.getRoomType() == roomType)
                .collect(Collectors.toList());
    }

    /**
     * Finds all reservations that overlap with a given date range.
     * Useful for finding potential conflicts or occupancy reports.
     *
     * @param checkInDate the start date of the range
     * @param checkOutDate the end date of the range
     * @return list of reservations that overlap with the date range
     */
    public List<Reservation> findReservationsForDateRange(LocalDate checkInDate,
                                                          LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            return List.of();
        }

        return repository.findAll().stream()
                .filter(r -> DateValidator.doDateRangesOverlap(
                        r.getCheckInDate(), r.getCheckOutDate(), checkInDate, checkOutDate))
                .collect(Collectors.toList());
    }

    /**
     * Gets availability report for a specific date range.
     *
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return an AvailabilityReport showing availability for all room types
     */
    public AvailabilityChecker.AvailabilityReport getAvailabilityReport(
            LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> allReservations = repository.findAll();
        return AvailabilityChecker.getAvailabilityReport(
                allReservations, checkInDate, checkOutDate);
    }

    /**
     * Checks if a specific room type is available for given dates.
     *
     * @param roomType the room type to check
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return true if at least one room is available, false otherwise
     */
    public boolean isRoomAvailable(RoomType roomType, LocalDate checkInDate,
                                   LocalDate checkOutDate) {
        List<Reservation> allReservations = repository.findAll();
        return AvailabilityChecker.isRoomAvailable(
                allReservations, roomType, checkInDate, checkOutDate);
    }

    /**
     * Gets the number of available rooms for a specific type and date range.
     *
     * @param roomType the room type to check
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return the number of available rooms
     */
    public int getAvailableRoomCount(RoomType roomType, LocalDate checkInDate,
                                     LocalDate checkOutDate) {
        List<Reservation> allReservations = repository.findAll();
        return AvailabilityChecker.getAvailableRoomCount(
                allReservations, roomType, checkInDate, checkOutDate);
    }

    /**
     * Calculates a price quote for a potential reservation without creating it.
     *
     * @param roomType the room type
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return a PriceBreakdown showing detailed pricing information
     * @throws InvalidReservationException if dates are invalid
     */
    public PriceCalculator.PriceBreakdown getPriceQuote(RoomType roomType,
                                                        LocalDate checkInDate,
                                                        LocalDate checkOutDate)
            throws InvalidReservationException {
        if (roomType == null) {
            throw new InvalidReservationException("Room type is required");
        }

        if (!DateValidator.isValidDateRange(checkInDate, checkOutDate)) {
            String errorMessage = DateValidator.getDateValidationMessage(checkInDate, checkOutDate);
            throw new InvalidReservationException(errorMessage);
        }

        long nights = DateValidator.calculateNights(checkInDate, checkOutDate);
        return PriceCalculator.getPriceBreakdown(roomType, nights);
    }

    /**
     * Gets count of reservations grouped by status.
     * Useful for dashboard and reporting.
     *
     * @return a string summary of reservation counts by status
     */
    public String getReservationSummary() {
        List<Reservation> allReservations = repository.findAll();

        long pending = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .count();
        long confirmed = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        long checkedIn = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
                .count();
        long completed = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                .count();
        long cancelled = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();

        return String.format(
                "Reservation Summary:\n" +
                        "  Total: %d\n" +
                        "  Pending: %d\n" +
                        "  Confirmed: %d\n" +
                        "  Checked In: %d\n" +
                        "  Completed: %d\n" +
                        "  Cancelled: %d",
                allReservations.size(), pending, confirmed, checkedIn, completed, cancelled
        );
    }

    /**
     * Deletes a reservation from the system.
     * Use with caution - this permanently removes the reservation.
     *
     * @param reservationId the ID of the reservation to delete
     * @return true if deleted successfully, false if not found
     */
    public boolean deleteReservation(String reservationId) {
        return repository.delete(reservationId);
    }

    /**
     * Gets the total count of reservations in the system.
     *
     * @return the total number of reservations
     */
    public int getTotalReservationCount() {
        return repository.findAll().size();
    }
}