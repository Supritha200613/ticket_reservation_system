package com.otrs.ticket_reservation_system.entity.enums;

public enum Role {
    ROLE_ADMIN,      // Manages entire system
    ROLE_AGENT,      // Books tickets on behalf of customers at counter
    ROLE_CUSTOMER    // Registers and books tickets online
}
