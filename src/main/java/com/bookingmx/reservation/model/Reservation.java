package com.bookingmx.reservation.model;

import com.bookingmx.reservation.exception.InvalidReservationException;
import com.bookingmx.reservation.util.PriceCalculator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a hotel reservation in the BookingMx system.
 * This class encapsulates all information related to a guest's room booking,
 * including dates, guest information, room details, and pricing.
 *
 * The class includes validation logic to ensure data integrity and business rules
 * such as valid date ranges and non-negative pricing.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class Reservation {

    private String id;
    private String guestName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private RoomType roomType;
    private double totalPrice;
    private ReservationStatus status;
    private LocalDate createdAt;

    /**
     * Default constructor that generates a unique ID and sets creation date.
     * Status is automatically set to PENDING.
     */
    public Reservation() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.status = ReservationStatus.PENDING;
    }

    /**
     * Full constructor for creating a reservation with all details.
     * Automatically generates a unique ID and sets status to PENDING.
     *
     * @param guestName the name of the guest making the reservation
     * @param guestEmail the email address of the guest
     * @param checkInDate the date when the guest will check in
     * @param checkOutDate the date when the guest will check out
     * @param roomType the type of room being reserved
     * @throws InvalidReservationException if any validation fails
     */
    public Reservation(String guestName, String guestEmail,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       RoomType roomType) throws InvalidReservationException {
        this();
        validateReservationData(guestName, guestEmail, checkInDate, checkOutDate, roomType);
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        this.totalPrice = calculateTotalPrice();
    }

    /**
     * Constructor with pre-calculated total price.
     * Used primarily for testing or when loading existing reservations.
     *
     * @param guestName the name of the guest making the reservation
     * @param guestEmail the email address of the guest
     * @param checkInDate the date when the guest will check in
     * @param checkOutDate the date when the guest will check out
     * @param roomType the type of room being reserved
     * @param totalPrice the pre-calculated total price
     * @throws InvalidReservationException if any validation fails
     */
    public Reservation(String guestName, String guestEmail,
                       LocalDate checkInDate, LocalDate checkOutDate,
                       RoomType roomType, double totalPrice) throws InvalidReservationException {
        this();
        validateReservationData(guestName, guestEmail, checkInDate, checkOutDate, roomType);
        if (totalPrice < 0) {
            throw new InvalidReservationException("Total price cannot be negative");
        }
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        this.totalPrice = totalPrice;
    }

    /**
     * Validates all reservation data according to business rules.
     *
     * @param guestName the guest name to validate
     * @param guestEmail the guest email to validate
     * @param checkIn the check-in date to validate
     * @param checkOut the check-out date to validate
     * @param roomType the room type to validate
     * @throws InvalidReservationException if any validation fails
     */
    private void validateReservationData(String guestName, String guestEmail,
                                         LocalDate checkIn, LocalDate checkOut,
                                         RoomType roomType) throws InvalidReservationException {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidReservationException("Guest name cannot be null or empty");
        }

        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            throw new InvalidReservationException("Guest email cannot be null or empty");
        }

        if (!isValidEmail(guestEmail)) {
            throw new InvalidReservationException("Invalid email format");
        }

        if (checkIn == null) {
            throw new InvalidReservationException("Check-in date cannot be null");
        }

        if (checkOut == null) {
            throw new InvalidReservationException("Check-out date cannot be null");
        }

        if (roomType == null) {
            throw new InvalidReservationException("Room type cannot be null");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidReservationException("Check-in date cannot be in the past");
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new InvalidReservationException("Check-out date must be after check-in date");
        }
    }

    /**
     * Simple email validation using basic regex pattern.
     *
     * @param email the email address to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Calculates the total price for the reservation based on room type and number of nights.
     *
     * @return the calculated total price
     */
    public double calculateTotalPrice() {
        if (checkInDate == null || checkOutDate == null || roomType == null) {
            return 0.0;
        }
        return PriceCalculator.calculateTotalPrice(roomType,checkInDate,checkOutDate);
    }

    /**
     * Calculates the number of nights for this reservation.
     *
     * @return the number of nights between check-in and check-out dates
     */
    public long calculateNumberOfNights() {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    /**
     * Cancels this reservation by changing its status to CANCELLED.
     *
     * @throws InvalidReservationException if reservation is already completed or cancelled
     */
    public void cancel() throws InvalidReservationException {
        if (status == ReservationStatus.COMPLETED) {
            throw new InvalidReservationException("Cannot cancel a completed reservation");
        }
        if (status == ReservationStatus.CANCELLED) {
            throw new InvalidReservationException("Reservation is already cancelled");
        }
        this.status = ReservationStatus.CANCELLED;
    }

    /**
     * Confirms this reservation by changing its status to CONFIRMED.
     *
     * @throws InvalidReservationException if reservation is not in PENDING status
     */
    public void confirm() throws InvalidReservationException {
        if (status != ReservationStatus.PENDING) {
            throw new InvalidReservationException("Only pending reservations can be confirmed");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    /**
     * Checks if this reservation is currently active (not cancelled or completed).
     *
     * @return true if reservation is active, false otherwise
     */
    public boolean isActive() {
        return status != ReservationStatus.CANCELLED && status != ReservationStatus.COMPLETED;
    }

    // Getters and Setters

    /**
     * Gets the unique identifier for this reservation.
     *
     * @return the reservation ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this reservation.
     * Used primarily when loading existing reservations from storage.
     *
     * @param id the reservation ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the guest.
     *
     * @return the guest name
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     * Sets the guest name with validation.
     *
     * @param guestName the guest name to set
     * @throws InvalidReservationException if name is null or empty
     */
    public void setGuestName(String guestName) throws InvalidReservationException {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidReservationException("Guest name cannot be null or empty");
        }
        this.guestName = guestName;
    }

    /**
     * Gets the guest's email address.
     *
     * @return the guest email
     */
    public String getGuestEmail() {
        return guestEmail;
    }

    /**
     * Sets the guest email with validation.
     *
     * @param guestEmail the guest email to set
     * @throws InvalidReservationException if email is null, empty, or invalid format
     */
    public void setGuestEmail(String guestEmail) throws InvalidReservationException {
        if (guestEmail == null || guestEmail.trim().isEmpty()) {
            throw new InvalidReservationException("Guest email cannot be null or empty");
        }
        if (!isValidEmail(guestEmail)) {
            throw new InvalidReservationException("Invalid email format");
        }
        this.guestEmail = guestEmail;
    }

    /**
     * Gets the check-in date.
     *
     * @return the check-in date
     */
    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    /**
     * Sets the check-in date with validation.
     *
     * @param checkInDate the check-in date to set
     * @throws InvalidReservationException if date is null or in the past
     */
    public void setCheckInDate(LocalDate checkInDate) throws InvalidReservationException {
        if (checkInDate == null) {
            throw new InvalidReservationException("Check-in date cannot be null");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new InvalidReservationException("Check-in date cannot be in the past");
        }
        this.checkInDate = checkInDate;
    }

    /**
     * Gets the check-out date.
     *
     * @return the check-out date
     */
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * Sets the check-out date with validation.
     *
     * @param checkOutDate the check-out date to set
     * @throws InvalidReservationException if date is null or not after check-in
     */
    public void setCheckOutDate(LocalDate checkOutDate) throws InvalidReservationException {
        if (checkOutDate == null) {
            throw new InvalidReservationException("Check-out date cannot be null");
        }
        if (this.checkInDate != null &&
                (checkOutDate.isBefore(this.checkInDate) || checkOutDate.isEqual(this.checkInDate))) {
            throw new InvalidReservationException("Check-out date must be after check-in date");
        }
        this.checkOutDate = checkOutDate;
    }

    /**
     * Gets the room type for this reservation.
     *
     * @return the room type
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * Sets the room type with validation.
     *
     * @param roomType the room type to set
     * @throws InvalidReservationException if room type is null
     */
    public void setRoomType(RoomType roomType) throws InvalidReservationException {
        if (roomType == null) {
            throw new InvalidReservationException("Room type cannot be null");
        }
        this.roomType = roomType;
        // Recalculate price if dates exist
        if (this.checkInDate != null && this.checkOutDate != null) {
            this.totalPrice = calculateTotalPrice();
        }
    }

    /**
     * Gets the total price for this reservation.
     *
     * @return the total price
     */
    public double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Sets the total price.
     * Note: Normally price is calculated automatically, but this setter
     * allows manual override if needed.
     *
     * @param totalPrice the total price to set
     * @throws InvalidReservationException if price is negative
     */
    public void setTotalPrice(double totalPrice) throws InvalidReservationException {
        if (totalPrice < 0) {
            throw new InvalidReservationException("Total price cannot be negative");
        }
        this.totalPrice = totalPrice;
    }

    /**
     * Gets the current status of this reservation.
     *
     * @return the reservation status
     */
    public ReservationStatus getStatus() {
        return status;
    }

    /**
     * Sets the reservation status.
     *
     * @param status the status to set
     */
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    /**
     * Gets the date when this reservation was created.
     *
     * @return the creation date
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation date.
     * Used primarily when loading existing reservations.
     *
     * @param createdAt the creation date to set
     */
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Generates a string representation of this reservation.
     *
     * @return a string containing the reservation details
     */
    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", guestName='" + guestName + '\'' +
                ", guestEmail='" + guestEmail + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", roomType=" + roomType +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", nights=" + calculateNumberOfNights() +
                '}';
    }

    /**
     * Checks if this reservation is equal to another object.
     * Two reservations are equal if they have the same ID.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Generates a hash code for this reservation based on its ID.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}