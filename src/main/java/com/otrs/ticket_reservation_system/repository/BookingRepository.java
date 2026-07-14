package com.otrs.ticket_reservation_system.repository;

import com.otrs.ticket_reservation_system.entity.Booking;
import com.otrs.ticket_reservation_system.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    Optional<Booking> findByPnr(String pnr);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByScheduleId(Long scheduleId);
}
