package com.otrs.ticket_reservation_system.entity.enums;

public enum BookingStatus {
    CONFIRMED,   // Payment successful, booking confirmed
    CANCELLED,   // Booking cancelled by user or admin
    PENDING      // Awaiting payment confirmation
}
