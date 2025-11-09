package com.bookingmx.reservation.util;

import com.bookingmx.reservation.model.RoomType;
import java.time.LocalDate;

/**
 * Utility class for calculating prices in the reservation system.
 * This class handles all price-related calculations including base prices,
 * discounts for extended stays, taxes, and final totals.
 *
 * All methods are static and the class cannot be instantiated.
 *
 * Business Rules:
 * - 7-13 nights: 5% discount
 * - 14-29 nights: 10% discount
 * - 30+ nights: 15% discount
 * - Tax rate: 16% (standard hotel tax)
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
public class PriceCalculator {

    /**
     * Tax rate applied to all reservations (16% as decimal).
     */
    public static final double TAX_RATE = 0.16;

    /**
     * Minimum nights for first discount tier (5% off).
     */
    public static final int DISCOUNT_TIER_1_NIGHTS = 7;

    /**
     * Discount percentage for tier 1 (7-13 nights).
     */
    public static final double DISCOUNT_TIER_1_RATE = 0.05;

    /**
     * Minimum nights for second discount tier (10% off).
     */
    public static final int DISCOUNT_TIER_2_NIGHTS = 14;

    /**
     * Discount percentage for tier 2 (14-29 nights).
     */
    public static final double DISCOUNT_TIER_2_RATE = 0.10;

    /**
     * Minimum nights for third discount tier (15% off).
     */
    public static final int DISCOUNT_TIER_3_NIGHTS = 30;

    /**
     * Discount percentage for tier 3 (30+ nights).
     */
    public static final double DISCOUNT_TIER_3_RATE = 0.15;

    /**
     * Private constructor to prevent instantiation.
     * This class should only be used through its static methods.
     */
    private PriceCalculator() {
        throw new UnsupportedOperationException("PriceCalculator is a utility class and cannot be instantiated");
    }

    /**
     * Calculates the base price for a reservation without any discounts or taxes.
     * Base price = room base price per night × number of nights
     *
     * @param roomType the type of room being reserved
     * @param numberOfNights the number of nights for the stay
     * @return the base price before discounts and taxes
     * @throws IllegalArgumentException if roomType is null or numberOfNights is negative
     */
    public static double calculateBasePrice(RoomType roomType, long numberOfNights) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (numberOfNights < 0) {
            throw new IllegalArgumentException("Number of nights cannot be negative");
        }

        return roomType.getBasePrice() * numberOfNights;
    }

    /**
     * Calculates the base price using check-in and check-out dates.
     *
     * @param roomType the type of room being reserved
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return the base price before discounts and taxes
     * @throws IllegalArgumentException if any parameter is null or dates are invalid
     */
    public static double calculateBasePrice(RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        long nights = DateValidator.calculateNights(checkInDate, checkOutDate);
        return calculateBasePrice(roomType, nights);
    }

    /**
     * Determines the applicable discount rate based on the number of nights.
     *
     * Discount tiers:
     * - 1-6 nights: 0% (no discount)
     * - 7-13 nights: 5% discount
     * - 14-29 nights: 10% discount
     * - 30+ nights: 15% discount
     *
     * @param numberOfNights the number of nights for the stay
     * @return the discount rate as a decimal (0.0 to 0.15)
     * @throws IllegalArgumentException if numberOfNights is negative
     */
    public static double getDiscountRate(long numberOfNights) {
        if (numberOfNights < 0) {
            throw new IllegalArgumentException("Number of nights cannot be negative");
        }

        if (numberOfNights >= DISCOUNT_TIER_3_NIGHTS) {
            return DISCOUNT_TIER_3_RATE;
        } else if (numberOfNights >= DISCOUNT_TIER_2_NIGHTS) {
            return DISCOUNT_TIER_2_RATE;
        } else if (numberOfNights >= DISCOUNT_TIER_1_NIGHTS) {
            return DISCOUNT_TIER_1_RATE;
        } else {
            return 0.0; // No discount for stays less than 7 nights
        }
    }

    /**
     * Calculates the discount amount based on base price and number of nights.
     *
     * @param basePrice the base price before discount
     * @param numberOfNights the number of nights for the stay
     * @return the discount amount to be subtracted from base price
     * @throws IllegalArgumentException if basePrice is negative or numberOfNights is negative
     */
    public static double calculateDiscount(double basePrice, long numberOfNights) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("Base price cannot be negative");
        }
        if (numberOfNights < 0) {
            throw new IllegalArgumentException("Number of nights cannot be negative");
        }

        double discountRate = getDiscountRate(numberOfNights);
        return basePrice * discountRate;
    }

    /**
     * Calculates the price after applying discount but before tax.
     *
     * @param roomType the type of room being reserved
     * @param numberOfNights the number of nights for the stay
     * @return the price after discount, before tax
     * @throws IllegalArgumentException if roomType is null or numberOfNights is negative
     */
    public static double calculatePriceAfterDiscount(RoomType roomType, long numberOfNights) {
        double basePrice = calculateBasePrice(roomType, numberOfNights);
        double discount = calculateDiscount(basePrice, numberOfNights);
        return basePrice - discount;
    }

    /**
     * Calculates the tax amount based on a given price.
     *
     * @param price the price on which to calculate tax
     * @return the tax amount
     * @throws IllegalArgumentException if price is negative
     */
    public static double calculateTax(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        return price * TAX_RATE;
    }

    /**
     * Calculates the total price including discount and tax.
     * This is the final price the customer will pay.
     *
     * Formula: (base price - discount) + tax on discounted price
     *
     * @param roomType the type of room being reserved
     * @param numberOfNights the number of nights for the stay
     * @param includeTax whether to include tax in the calculation
     * @return the total final price
     * @throws IllegalArgumentException if roomType is null or numberOfNights is negative
     */
    public static double calculateTotalPrice(RoomType roomType, long numberOfNights, boolean includeTax) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (numberOfNights < 0) {
            throw new IllegalArgumentException("Number of nights cannot be negative");
        }

        double priceAfterDiscount = calculatePriceAfterDiscount(roomType, numberOfNights);

        if (includeTax) {
            double tax = calculateTax(priceAfterDiscount);
            return priceAfterDiscount + tax;
        }

        return priceAfterDiscount;
    }

    /**
     * Calculates the total price including discount and tax (convenience method).
     * By default includes tax.
     *
     * @param roomType the type of room being reserved
     * @param numberOfNights the number of nights for the stay
     * @return the total final price including tax
     * @throws IllegalArgumentException if roomType is null or numberOfNights is negative
     */
    public static double calculateTotalPrice(RoomType roomType, long numberOfNights) {
        return calculateTotalPrice(roomType, numberOfNights, true);
    }

    /**
     * Calculates the total price using check-in and check-out dates.
     *
     * @param roomType the type of room being reserved
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @param includeTax whether to include tax in the calculation
     * @return the total final price
     * @throws IllegalArgumentException if any parameter is null or dates are invalid
     */
    public static double calculateTotalPrice(RoomType roomType, LocalDate checkInDate,
                                             LocalDate checkOutDate, boolean includeTax) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        long nights = DateValidator.calculateNights(checkInDate, checkOutDate);
        return calculateTotalPrice(roomType, nights, includeTax);
    }

    /**
     * Calculates the total price using dates, including tax by default.
     *
     * @param roomType the type of room being reserved
     * @param checkInDate the check-in date
     * @param checkOutDate the check-out date
     * @return the total final price including tax
     * @throws IllegalArgumentException if any parameter is null or dates are invalid
     */
    public static double calculateTotalPrice(RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        return calculateTotalPrice(roomType, checkInDate, checkOutDate, true);
    }

    /**
     * Gets a breakdown of the price calculation showing all components.
     * Useful for displaying itemized pricing to customers.
     *
     * @param roomType the type of room being reserved
     * @param numberOfNights the number of nights for the stay
     * @return a PriceBreakdown object containing all price components
     * @throws IllegalArgumentException if roomType is null or numberOfNights is negative
     */
    public static PriceBreakdown getPriceBreakdown(RoomType roomType, long numberOfNights) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (numberOfNights < 0) {
            throw new IllegalArgumentException("Number of nights cannot be negative");
        }

        double basePrice = calculateBasePrice(roomType, numberOfNights);
        double discountRate = getDiscountRate(numberOfNights);
        double discountAmount = calculateDiscount(basePrice, numberOfNights);
        double priceAfterDiscount = basePrice - discountAmount;
        double tax = calculateTax(priceAfterDiscount);
        double totalPrice = priceAfterDiscount + tax;

        return new PriceBreakdown(basePrice, discountRate, discountAmount,
                priceAfterDiscount, tax, totalPrice);
    }

    /**
     * Formats a price as a currency string with two decimal places.
     *
     * @param price the price to format
     * @return formatted price string (e.g., "$150.00")
     */
    public static String formatPrice(double price) {
        return String.format("$%.2f", price);
    }

    /**
     * Inner class to represent a detailed breakdown of price components.
     * This provides transparency to customers about how the final price is calculated.
     */
    public static class PriceBreakdown {
        private final double basePrice;
        private final double discountRate;
        private final double discountAmount;
        private final double priceAfterDiscount;
        private final double tax;
        private final double totalPrice;

        /**
         * Constructor for PriceBreakdown.
         *
         * @param basePrice the base price before any adjustments
         * @param discountRate the discount rate applied (as decimal)
         * @param discountAmount the dollar amount of discount
         * @param priceAfterDiscount the price after discount but before tax
         * @param tax the tax amount
         * @param totalPrice the final total price
         */
        public PriceBreakdown(double basePrice, double discountRate, double discountAmount,
                              double priceAfterDiscount, double tax, double totalPrice) {
            this.basePrice = basePrice;
            this.discountRate = discountRate;
            this.discountAmount = discountAmount;
            this.priceAfterDiscount = priceAfterDiscount;
            this.tax = tax;
            this.totalPrice = totalPrice;
        }

        public double getBasePrice() {
            return basePrice;
        }

        public double getDiscountRate() {
            return discountRate;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }

        public double getPriceAfterDiscount() {
            return priceAfterDiscount;
        }

        public double getTax() {
            return tax;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        /**
         * Returns a formatted string representation of the price breakdown.
         *
         * @return formatted breakdown string
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Price Breakdown:\n");
            sb.append(String.format("  Base Price:         %s\n", formatPrice(basePrice)));
            if (discountRate > 0) {
                sb.append(String.format("  Discount (%.0f%%):     -%s\n",
                        discountRate * 100, formatPrice(discountAmount)));
                sb.append(String.format("  Subtotal:           %s\n", formatPrice(priceAfterDiscount)));
            }
            sb.append(String.format("  Tax (%.0f%%):          %s\n", TAX_RATE * 100, formatPrice(tax)));
            sb.append(String.format("  Total:              %s\n", formatPrice(totalPrice)));
            return sb.toString();
        }
    }
}