package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.Booking;
import com.otrs.ticket_reservation_system.entity.Schedule;
import com.otrs.ticket_reservation_system.entity.User;
import com.otrs.ticket_reservation_system.entity.enums.Role;
import com.otrs.ticket_reservation_system.service.BookingService;
import com.otrs.ticket_reservation_system.service.ScheduleService;
import com.otrs.ticket_reservation_system.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService,
                             ScheduleService scheduleService,
                             UserRepository userRepository) {
        this.bookingService = bookingService;
        this.scheduleService = scheduleService;
        this.userRepository = userRepository;
    }

    private boolean isAdminOrAgent(HttpSession session) {
        Role role = (Role) session.getAttribute("userRole");
        return role == Role.ROLE_ADMIN || role == Role.ROLE_AGENT;
    }

    @GetMapping("/my")
    public String myBookings(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("bookings", bookingService.getUserBookings(userId));
        model.addAttribute("userRole", session.getAttribute("userRole") != null ? session.getAttribute("userRole").toString() : "");
        model.addAttribute("userName", session.getAttribute("userName"));
        return "my-bookings";
    }

    @GetMapping("/all")
    public String allBookings(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("userRole", session.getAttribute("userRole") != null ? session.getAttribute("userRole").toString() : "");
        model.addAttribute("userName", session.getAttribute("userName"));
        return "all-bookings";
    }

    @GetMapping("/book/{scheduleId}")
    public String bookPage(@PathVariable Long scheduleId, Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Schedule schedule = scheduleService.getScheduleById(scheduleId).orElse(null);
        if (schedule == null) return "redirect:/schedules";
        model.addAttribute("schedule", schedule);
        model.addAttribute("userRole", session.getAttribute("userRole") != null ? session.getAttribute("userRole").toString() : "");
        model.addAttribute("userName", session.getAttribute("userName"));
        return "book-ticket";
    }

    @PostMapping("/book/{scheduleId}")
    public String confirmBooking(@PathVariable Long scheduleId,
                                 @RequestParam String passengerName,
                                 @RequestParam int passengerAge,
                                 @RequestParam String passengerGender,
                                 @RequestParam String idType,
                                 @RequestParam String idNumber,
                                 @RequestParam int seatsBooked,
                                 @RequestParam String seatNumbers,
                                 HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElse(null);
        Schedule schedule = scheduleService.getScheduleById(scheduleId).orElse(null);
        if (user == null || schedule == null) return "redirect:/schedules";
        try {
            if (seatsBooked < 1) throw new RuntimeException("Number of seats must be at least 1");
            if (seatsBooked > schedule.getAvailableSeats())
                throw new RuntimeException("Only " + schedule.getAvailableSeats() + " seat(s) available");
            String[] seatArr = seatNumbers.split(",");
            if (seatArr.length != seatsBooked)
                throw new RuntimeException("Enter exactly " + seatsBooked + " seat number(s) comma-separated (e.g. A1,A2)");

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setSchedule(schedule);
            booking.setSeatsBooked(seatsBooked);
            booking.setSeatNumbers(seatNumbers.trim());
            booking.setPassengerName(passengerName);
            booking.setPassengerAge(passengerAge);
            booking.setPassengerGender(passengerGender);
            booking.setIdType(idType);
            booking.setIdNumber(idNumber);
            booking.setTotalAmount(schedule.getRoute().getBaseFare().multiply(BigDecimal.valueOf(seatsBooked)));
            Booking saved = bookingService.createPendingBooking(booking);
            return "redirect:/payment/" + saved.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("schedule", schedule);
            model.addAttribute("userRole", session.getAttribute("userRole") != null ? session.getAttribute("userRole").toString() : "");
            model.addAttribute("userName", session.getAttribute("userName"));
            return "book-ticket";
        }
    }

    @GetMapping("/confirmation/{id}")
    public String confirmation(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Booking booking = bookingService.getBookingById(id).orElse(null);
        if (booking == null) return "redirect:/bookings/my";
        Long userId = (Long) session.getAttribute("userId");
        if (!isAdminOrAgent(session) && !booking.getUser().getId().equals(userId))
            return "redirect:/bookings/my";
        if (!booking.getStatus().name().equals("CONFIRMED"))
            return "redirect:/payment/" + booking.getId();
        model.addAttribute("booking", booking);
        model.addAttribute("userRole", session.getAttribute("userRole") != null ? session.getAttribute("userRole").toString() : "");
        model.addAttribute("userName", session.getAttribute("userName"));
        return "booking-confirmation";
    }

    @GetMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        Booking booking = bookingService.getBookingById(id).orElse(null);
        if (booking == null) return "redirect:/bookings/my";
        if (!isAdminOrAgent(session) && !booking.getUser().getId().equals(userId))
            return "redirect:/bookings/my";
        Booking cancelled = bookingService.cancelBooking(id);
        if (cancelled != null)
            ra.addFlashAttribute("success", "Booking " + cancelled.getPnr() + " cancelled. Refund will be processed.");
        return "redirect:/bookings/my";
    }

    @GetMapping("/search")
    public String searchByPnr(@RequestParam(required = false) String pnr,
                               Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        if (pnr != null && !pnr.isBlank()) {
            Booking booking = bookingService.getBookingByPnr(pnr.trim().toUpperCase()).orElse(null);
            if (booking != null && !isAdminOrAgent(session) && !booking.getUser().getId().equals(userId))
                booking = null;
            model.addAttribute("booking", booking);
            model.addAttribute("pnr", pnr);
        }
        model.addAttribute("bookings", bookingService.getUserBookings(userId));
        model.addAttribute("userRole", session.getAttribute("userRole") != null ? session.getAttribute("userRole").toString() : "");
        model.addAttribute("userName", session.getAttribute("userName"));
        return "my-bookings";
    }
}
