package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.Booking;
import com.otrs.ticket_reservation_system.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    private final BookingService bookingService;

    public PaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private void addNavAttrs(Model model, HttpSession session) {
        Object role = session.getAttribute("userRole");
        model.addAttribute("userRole", role != null ? role.toString() : "");
        model.addAttribute("userName", session.getAttribute("userName"));
    }

    @GetMapping("/{bookingId}")
    public String paymentPage(@PathVariable Long bookingId, Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Booking booking = bookingService.getBookingById(bookingId).orElse(null);
        if (booking == null) return "redirect:/bookings/my";
        Long userId = (Long) session.getAttribute("userId");
        if (!booking.getUser().getId().equals(userId)) return "redirect:/bookings/my";
        if (booking.getStatus().name().equals("CONFIRMED"))
            return "redirect:/bookings/confirmation/" + booking.getId();
        addNavAttrs(model, session);
        model.addAttribute("booking", booking);
        return "payment";
    }

    @PostMapping("/{bookingId}/process")
    public String processPayment(@PathVariable Long bookingId,
                                 @RequestParam String paymentMethod,
                                 @RequestParam(required = false) String cardNumber,
                                 @RequestParam(required = false) String upiId,
                                 HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Booking booking = bookingService.getBookingById(bookingId).orElse(null);
        if (booking == null) return "redirect:/bookings/my";
        Long userId = (Long) session.getAttribute("userId");
        if (!booking.getUser().getId().equals(userId)) return "redirect:/bookings/my";
        try {
            String transactionId = generateTransactionId();
            bookingService.confirmPayment(bookingId, paymentMethod, transactionId);
            return "redirect:/payment/" + bookingId + "/success";
        } catch (Exception e) {
            addNavAttrs(model, session);
            model.addAttribute("error", "Payment failed: " + e.getMessage());
            model.addAttribute("booking", booking);
            return "payment";
        }
    }

    @GetMapping("/{bookingId}/success")
    public String paymentSuccess(@PathVariable Long bookingId, Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Booking booking = bookingService.getBookingById(bookingId).orElse(null);
        if (booking == null) return "redirect:/bookings/my";
        addNavAttrs(model, session);
        model.addAttribute("booking", booking);
        return "payment-success";
    }

    @GetMapping("/{bookingId}/cancel")
    public String cancelPayment(@PathVariable Long bookingId, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Booking booking = bookingService.getBookingById(bookingId).orElse(null);
        if (booking != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (booking.getUser().getId().equals(userId)) {
                bookingService.releaseHeldBooking(bookingId);
            }
        }
        return "redirect:/schedules";
    }

    private String generateTransactionId() {
        String chars = "0123456789ABCDEF";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder("TXN");
        for (int i = 0; i < 10; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
