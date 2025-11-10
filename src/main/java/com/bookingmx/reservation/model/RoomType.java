package com.bookingmx.reservation.model;

/**
 * Enumeration representing the different types of rooms available in the hotel.
 * Each room type has an associated base price per night.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public enum RoomType {

    /**
     * Standard single room with basic amenities.
     * Base price: $50 per night
     */
    SINGLE(50.0),

    /**
     * Double room with two beds or one queen bed.
     * Base price: $80 per night
     */
    DOUBLE(80.0),

    /**
     * Suite with separate living area and premium amenities.
     * Base price: $150 per night
     */
    SUITE(150.0),

    /**
     * Deluxe room with luxury features and premium location.
     * Base price: $200 per night
     */
    DELUXE(200.0);

    private final double basePrice;

    /**
     * Constructor for RoomType enum.
     *
     * @param basePrice the base price per night for this room type
     */
    RoomType(double basePrice) {
        this.basePrice = basePrice;
    }

    /**
     * Gets the base price per night for this room type.
     *
     * @return the base price as a double value
     */
    public double getBasePrice() {
        return basePrice;
    }
}