package com.otrs.ticket_reservation_system.entity.enums;

public enum PaymentStatus {
    PAID,      // Payment completed successfully
    UNPAID,    // Payment not yet made
    REFUNDED   // Payment refunded after cancellation
}
