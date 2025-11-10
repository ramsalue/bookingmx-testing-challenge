package com.bookingmx.reservation.util;

import com.bookingmx.reservation.model.RoomType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PriceCalculator utility class.
 * These tests verify pricing calculations including base prices, discounts,
 * taxes, and the complete price breakdown functionality.
 *
 * @author BookingMx Development Team
 * @version 1.0.0
 */
@DisplayName("PriceCalculator Utility Tests")
class PriceCalculatorTest {

    @Test
    @DisplayName("Should throw exception when instantiating PriceCalculator")
    void testConstructorThrowsException() throws NoSuchMethodException {
        // Get the constructor even when is private
        Constructor<PriceCalculator> constructor = PriceCalculator.class.getDeclaredConstructor();

        // Ignore the private and make the constructor accesible
        constructor.setAccessible(true);

        // Wait for an InvocationTargetException
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        });

        // Verify the cause of the error I want
        assertEquals(UnsupportedOperationException.class, exception.getCause().getClass());
        assertEquals("PriceCalculator is a utility class and cannot be instantiated", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Should calculate base price for SINGLE room")
    void testCalculateBasePriceSingle() {
        // Arrange
        long nights = 5;

        // Act
        double basePrice = PriceCalculator.calculateBasePrice(RoomType.SINGLE, nights);

        // Assert
        assertEquals(250.0, basePrice, 0.01); // 5 nights × $50
    }

    @ParameterizedTest
    @CsvSource({
            "SINGLE, 3, 150.0",    // 3 × 50
            "DOUBLE, 5, 400.0",    // 5 × 80
            "SUITE, 2, 300.0",     // 2 × 150
            "DELUXE, 4, 800.0"     // 4 × 200
    })
    @DisplayName("Should calculate base price for all room types")
    void testCalculateBasePriceAllRoomTypes(RoomType roomType, long nights, double expected) {
        // Act
        double basePrice = PriceCalculator.calculateBasePrice(roomType, nights);

        // Assert
        assertEquals(expected, basePrice, 0.01);
    }

    @Test
    @DisplayName("Should throw exception for null room type")
    void testCalculateBasePriceNullRoomType() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateBasePrice(null, 5);
        });
    }

    @Test
    @DisplayName("Should throw exception for negative nights")
    void testCalculateBasePriceNegativeNights() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateBasePrice(RoomType.DOUBLE, -1);
        });
    }

    @Test
    @DisplayName("Should calculate base price using dates")
    void testCalculateBasePriceWithDates() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10); // 3 nights

        // Act
        double basePrice = PriceCalculator.calculateBasePrice(RoomType.DOUBLE, checkIn, checkOut);

        // Assert
        assertEquals(240.0, basePrice, 0.01); // 3 × 80
    }

    @Test
    @DisplayName("Should return no discount for stays less than 7 nights")
    void testGetDiscountRateNoDiscount() {
        // Act
        double discountRate = PriceCalculator.getDiscountRate(5);

        // Assert
        assertEquals(0.0, discountRate, 0.001);
    }

    @ParameterizedTest
    @CsvSource({
            "6, 0.0",      // No discount
            "7, 0.05",     // Tier 1: 5%
            "10, 0.05",    // Tier 1: 5%
            "13, 0.05",    // Tier 1: 5%
            "14, 0.10",    // Tier 2: 10%
            "20, 0.10",    // Tier 2: 10%
            "29, 0.10",    // Tier 2: 10%
            "30, 0.15",    // Tier 3: 15%
            "45, 0.15"     // Tier 3: 15%
    })
    @DisplayName("Should apply correct discount tiers")
    void testGetDiscountRateTiers(long nights, double expectedRate) {
        // Act
        double discountRate = PriceCalculator.getDiscountRate(nights);

        // Assert
        assertEquals(expectedRate, discountRate, 0.001);
    }

    @Test
    @DisplayName("Should calculate discount amount correctly")
    void testCalculateDiscount() {
        // Arrange
        double basePrice = 1000.0;
        long nights = 10; // Should get 5% discount

        // Act
        double discount = PriceCalculator.calculateDiscount(basePrice, nights);

        // Assert
        assertEquals(50.0, discount, 0.01); // 5% of 1000
    }

    @Test
    @DisplayName("Should calculate zero discount for short stays")
    void testCalculateDiscountNoDiscount() {
        // Arrange
        double basePrice = 500.0;
        long nights = 3;

        // Act
        double discount = PriceCalculator.calculateDiscount(basePrice, nights);

        // Assert
        assertEquals(0.0, discount, 0.01);
    }

    @Test
    @DisplayName("Should calculate price after discount")
    void testCalculatePriceAfterDiscount() {
        // Arrange
        RoomType roomType = RoomType.DOUBLE; // $80/night
        long nights = 10; // Should get 5% discount
        // Base: 10 × 80 = 800
        // Discount: 5% of 800 = 40
        // After discount: 800 - 40 = 760

        // Act
        double priceAfterDiscount = PriceCalculator.calculatePriceAfterDiscount(roomType, nights);

        // Assert
        assertEquals(760.0, priceAfterDiscount, 0.01);
    }

    @Test
    @DisplayName("Should calculate tax correctly")
    void testCalculateTax() {
        // Arrange
        double price = 1000.0;

        // Act
        double tax = PriceCalculator.calculateTax(price);

        // Assert
        assertEquals(160.0, tax, 0.01); // 16% of 1000
    }

    @Test
    @DisplayName("Should throw exception for negative price in tax calculation")
    void testCalculateTaxNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateTax(-100.0);
        });
    }

    @Test
    @DisplayName("Should calculate total price with tax")
    void testCalculateTotalPriceWithTax() {
        // Arrange
        RoomType roomType = RoomType.SINGLE; // $50/night
        long nights = 5; // No discount
        // Base: 5 × 50 = 250
        // Tax: 16% of 250 = 40
        // Total: 250 + 40 = 290

        // Act
        double total = PriceCalculator.calculateTotalPrice(roomType, nights, true);

        // Assert
        assertEquals(290.0, total, 0.01);
    }

    @Test
    @DisplayName("Should calculate total price without tax")
    void testCalculateTotalPriceWithoutTax() {
        // Arrange
        RoomType roomType = RoomType.SINGLE; // $50/night
        long nights = 5;
        // Base: 5 × 50 = 250

        // Act
        double total = PriceCalculator.calculateTotalPrice(roomType, nights, false);

        // Assert
        assertEquals(250.0, total, 0.01);
    }

    @Test
    @DisplayName("Should calculate total price with discount and tax")
    void testCalculateTotalPriceWithDiscountAndTax() {
        // Arrange
        RoomType roomType = RoomType.SUITE; // $150/night
        long nights = 15; // Should get 10% discount
        // Base: 15 × 150 = 2250
        // Discount: 10% of 2250 = 225
        // After discount: 2250 - 225 = 2025
        // Tax: 16% of 2025 = 324
        // Total: 2025 + 324 = 2349

        // Act
        double total = PriceCalculator.calculateTotalPrice(roomType, nights);

        // Assert
        assertEquals(2349.0, total, 0.01);
    }

    @Test
    @DisplayName("Should calculate total price using dates")
    void testCalculateTotalPriceWithDates() {
        // Arrange
        LocalDate checkIn = LocalDate.now().plusDays(7);
        LocalDate checkOut = LocalDate.now().plusDays(10); // 3 nights
        RoomType roomType = RoomType.DOUBLE; // $80/night
        // Base: 3 × 80 = 240
        // Tax: 16% of 240 = 38.4
        // Total: 240 + 38.4 = 278.4

        // Act
        double total = PriceCalculator.calculateTotalPrice(roomType, checkIn, checkOut);

        // Assert
        assertEquals(278.4, total, 0.01);
    }

    @Test
    @DisplayName("Should get price breakdown with all components")
    void testGetPriceBreakdown() {
        // Arrange
        RoomType roomType = RoomType.DOUBLE; // $80/night
        long nights = 10; // Should get 5% discount
        // Base: 10 × 80 = 800
        // Discount: 5% of 800 = 40
        // After discount: 760
        // Tax: 16% of 760 = 121.6
        // Total: 881.6

        // Act
        PriceCalculator.PriceBreakdown breakdown =
                PriceCalculator.getPriceBreakdown(roomType, nights);

        // Assert
        assertNotNull(breakdown);
        assertEquals(800.0, breakdown.getBasePrice(), 0.01);
        assertEquals(0.05, breakdown.getDiscountRate(), 0.001);
        assertEquals(40.0, breakdown.getDiscountAmount(), 0.01);
        assertEquals(760.0, breakdown.getPriceAfterDiscount(), 0.01);
        assertEquals(121.6, breakdown.getTax(), 0.01);
        assertEquals(881.6, breakdown.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should format price breakdown as string")
    void testPriceBreakdownToString() {
        // Arrange
        PriceCalculator.PriceBreakdown breakdown =
                PriceCalculator.getPriceBreakdown(RoomType.SINGLE, 5);

        // Act
        String breakdownString = breakdown.toString();

        // Assert
        assertNotNull(breakdownString);
        assertTrue(breakdownString.contains("Price Breakdown"));
        assertTrue(breakdownString.contains("Base Price"));
        assertTrue(breakdownString.contains("Tax"));
        assertTrue(breakdownString.contains("Total"));
    }

    @Test
    @DisplayName("Should format price as currency")
    void testFormatPrice() {
        // Act
        String formatted = PriceCalculator.formatPrice(123.45);

        // Assert
        assertEquals("$123.45", formatted);
    }

    @Test
    @DisplayName("Should format price with two decimals")
    void testFormatPriceRounding() {
        // Act
        String formatted = PriceCalculator.formatPrice(123.456);

        // Assert
        assertEquals("$123.46", formatted); // Rounded
    }

    @ParameterizedTest
    @EnumSource(RoomType.class)
    @DisplayName("Should calculate prices for all room types")
    void testCalculatePricesAllRoomTypes(RoomType roomType) {
        // Act
        double price = PriceCalculator.calculateTotalPrice(roomType, 5);

        // Assert
        assertTrue(price > 0);
        assertTrue(price >= roomType.getBasePrice() * 5);
    }
    @Test
    @DisplayName("Should throw exception for negative nights in getDiscountRate")
    void testGetDiscountRateNegativeNights() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.getDiscountRate(-1);
        });
    }

    @Test
    @DisplayName("Should throw exception for negative price in calculateDiscount")
    void testCalculateDiscountNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateDiscount(-100, 5);
        });
    }

    @Test
    @DisplayName("Should throw exception for null room type in calculateTotalPrice")
    void testCalculateTotalPriceNullRoomType() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateTotalPrice(null, 5);
        });
    }
    @Test
    @DisplayName("Should throw exception for null dates in calculateBasePrice")
    void testCalculateBasePriceNullDates() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateBasePrice(RoomType.SINGLE, null, LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateBasePrice(RoomType.SINGLE, LocalDate.now(), null);
        });
    }

    @Test
    @DisplayName("Should throw exception for null dates in calculateTotalPrice")
    void testCalculateTotalPriceNullDates() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateTotalPrice(RoomType.SINGLE, null, LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.calculateTotalPrice(RoomType.SINGLE, LocalDate.now(), null);
        });
    }

    @Test
    @DisplayName("Should throw exception for nulls in getPriceBreakdown")
    void testGetPriceBreakdownNulls() {
        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.getPriceBreakdown(null, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            PriceCalculator.getPriceBreakdown(RoomType.SINGLE, -1);
        });
    }

    @Test
    @DisplayName("Should format price breakdown as string WITH discount")
    void testPriceBreakdownToString_WithDiscount() {
        // Arrange
        PriceCalculator.PriceBreakdown breakdown =
                PriceCalculator.getPriceBreakdown(RoomType.SINGLE, 7);

        // Act
        String breakdownString = breakdown.toString();

        // Assert
        assertNotNull(breakdownString);
        assertTrue(breakdownString.contains("Price Breakdown"));
        assertTrue(breakdownString.contains("Base Price"));

        assertTrue(breakdownString.contains("Discount"));
        assertTrue(breakdownString.contains("Subtotal"));

        assertTrue(breakdownString.contains("Tax"));
        assertTrue(breakdownString.contains("Total"));
    }
}