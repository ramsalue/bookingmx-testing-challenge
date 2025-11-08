package com.bookingmx.reservation.model;

import com.bookingmx.reservation.exception.InvalidReservationException;
import com.bookingmx.reservation.model.Reservation;
import com.bookingmx.reservation.model.RoomType;

import java.time.LocalDate;

/**
 * Manual test class to verify Reservation functionality.
 * This is not a JUnit test - just a quick check that the class works.
 * Delete this file before final submission.
 */
public class ReservationManualTest {

    public static void main(String[] args) {
        try {
            // Test 1: Create a valid reservation
            Reservation reservation = new Reservation(
                    "John Doe",
                    "john.doe@example.com",
                    LocalDate.now().plusDays(7),
                    LocalDate.now().plusDays(10),
                    RoomType.DOUBLE
            );

            System.out.println("Test 1 PASSED: Valid reservation created");
            System.out.println(reservation);
            System.out.println("Total price: $" + reservation.getTotalPrice());
            System.out.println("Number of nights: " + reservation.calculateNumberOfNights());
            System.out.println();

            // Test 2: Try to create reservation with past date (should fail)
            try {
                Reservation invalidReservation = new Reservation(
                        "Jane Smith",
                        "jane@example.com",
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(3),
                        RoomType.SUITE
                );
                System.out.println("Test 2 FAILED: Should have thrown exception for past date");
            } catch (InvalidReservationException e) {
                System.out.println("Test 2 PASSED: Correctly rejected past date");
                System.out.println("Error message: " + e.getMessage());
            }
            System.out.println();

            // Test 3: Cancel a reservation
            reservation.cancel();
            System.out.println("Test 3 PASSED: Reservation cancelled");
            System.out.println("Status: " + reservation.getStatus());
            System.out.println("Is active: " + reservation.isActive());

        } catch (Exception e) {
            System.out.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}