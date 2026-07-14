package com.otrs.ticket_reservation_system.service;

import com.otrs.ticket_reservation_system.entity.Booking;
import com.otrs.ticket_reservation_system.entity.Schedule;
import com.otrs.ticket_reservation_system.entity.enums.BookingStatus;
import com.otrs.ticket_reservation_system.entity.enums.PaymentStatus;
import com.otrs.ticket_reservation_system.repository.BookingRepository;
import com.otrs.ticket_reservation_system.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;

    public BookingService(BookingRepository bookingRepository, ScheduleRepository scheduleRepository) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * STEP 1 of booking flow: Create a PENDING booking and hold the seats.
     * Seats are deducted immediately (held) so two users can't double-book
     * the same seats while one is on the payment page. If payment fails or
     * is abandoned, seats should be released via releaseHeldBooking().
     */
    @Transactional
    public Booking createPendingBooking(Booking booking) {
        Schedule schedule = booking.getSchedule();
        if (schedule.getAvailableSeats() < booking.getSeatsBooked()) {
            throw new RuntimeException("Not enough seats available");
        }
        // Hold seats immediately
        schedule.setAvailableSeats(schedule.getAvailableSeats() - booking.getSeatsBooked());
        scheduleRepository.save(schedule);

        booking.setPnr(generatePnr());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        return bookingRepository.save(booking);
    }

    /**
     * STEP 2 of booking flow: Called after the demo payment gateway succeeds.
     * Marks the booking CONFIRMED and PAID, and stores the demo transaction details.
     */
    @Transactional
    public Booking confirmPayment(Long bookingId, String paymentMethod, String transactionId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not pending payment");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentMethod(paymentMethod);
        booking.setTransactionId(transactionId);
        booking.setPaymentDate(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    /**
     * Called if the user abandons/cancels payment before completing it.
     * Releases the held seats back to the schedule and marks booking CANCELLED.
     */
    @Transactional
    public void releaseHeldBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null && booking.getStatus() == BookingStatus.PENDING) {
            Schedule schedule = booking.getSchedule();
            schedule.setAvailableSeats(schedule.getAvailableSeats() + booking.getSeatsBooked());
            scheduleRepository.save(schedule);

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isPresent()) {
            Booking booking = opt.get();
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                // Restore seats
                Schedule schedule = booking.getSchedule();
                schedule.setAvailableSeats(schedule.getAvailableSeats() + booking.getSeatsBooked());
                scheduleRepository.save(schedule);

                booking.setStatus(BookingStatus.CANCELLED);
                booking.setPaymentStatus(PaymentStatus.REFUNDED);
                return bookingRepository.save(booking);
            }
        }
        return null;
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr);
    }

    private String generatePnr() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        String pnr;
        do {
            StringBuilder sb = new StringBuilder("TKT");
            for (int i = 0; i < 7; i++) {
                sb.append(chars.charAt(rnd.nextInt(chars.length())));
            }
            pnr = sb.toString();
        } while (bookingRepository.findByPnr(pnr).isPresent());
        return pnr;
    }
}
