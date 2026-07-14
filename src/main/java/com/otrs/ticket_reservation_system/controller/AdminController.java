package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.Booking;
import com.otrs.ticket_reservation_system.entity.User;
import com.otrs.ticket_reservation_system.entity.enums.BookingStatus;
import com.otrs.ticket_reservation_system.entity.enums.Role;
import com.otrs.ticket_reservation_system.service.BookingService;
import com.otrs.ticket_reservation_system.service.RouteService;
import com.otrs.ticket_reservation_system.service.ScheduleService;
import com.otrs.ticket_reservation_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;
    private final RouteService routeService;
    private final ScheduleService scheduleService;

    public AdminController(UserService userService, BookingService bookingService,
                           RouteService routeService, ScheduleService scheduleService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.routeService = routeService;
        this.scheduleService = scheduleService;
    }

    private boolean isAdmin(HttpSession session) {
        Role role = (Role) session.getAttribute("userRole");
        return role == Role.ROLE_ADMIN;
    }

    private boolean isAdminOrAgent(HttpSession session) {
        Role role = (Role) session.getAttribute("userRole");
        return role == Role.ROLE_ADMIN || role == Role.ROLE_AGENT;
    }

    // ── Admin Dashboard with revenue stats ────────────────────────────────────
    @GetMapping
    public String adminPanel(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";

        List<Booking> allBookings = bookingService.getAllBookings();
        long confirmed = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long cancelled = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        long pending = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING).count();

        BigDecimal totalRevenue = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("allBookings",    allBookings);
        model.addAttribute("totalBookings",  allBookings.size());
        model.addAttribute("confirmedCount", confirmed);
        model.addAttribute("cancelledCount", cancelled);
        model.addAttribute("pendingCount",   pending);
        model.addAttribute("totalRevenue",   totalRevenue);
        model.addAttribute("totalRoutes",    routeService.getAllActiveRoutes().size());
        model.addAttribute("totalSchedules", scheduleService.getActiveSchedules().size());
        model.addAttribute("totalUsers",     userService.getAllUsers().size());
        model.addAttribute("userRole",       session.getAttribute("userRole").toString());
        model.addAttribute("userName",       session.getAttribute("userName"));
        return "admin-panel";
    }

    // ── User management (Admin only) ──────────────────────────────────────────
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdmin(session)) return "redirect:/dashboard";

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("userRole", session.getAttribute("userRole").toString());
        model.addAttribute("userName", session.getAttribute("userName"));
        return "admin-users";
    }

    @GetMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdmin(session)) return "redirect:/dashboard";
        // Don't let admin disable themselves
        Long myId = (Long) session.getAttribute("userId");
        if (id.equals(myId)) {
            ra.addFlashAttribute("error", "You cannot disable your own account.");
            return "redirect:/admin/users";
        }
        User user = userService.getUserById(id).orElse(null);
        if (user != null) {
            user.setEnabled(!user.isEnabled());
            userService.updateUser(user);
            ra.addFlashAttribute("success",
                    user.getFullName() + " has been " + (user.isEnabled() ? "enabled" : "disabled") + ".");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdmin(session)) return "redirect:/dashboard";
        Long myId = (Long) session.getAttribute("userId");
        if (id.equals(myId)) {
            ra.addFlashAttribute("error", "You cannot delete your own account.");
            return "redirect:/admin/users";
        }
        userService.deleteUser(id);
        ra.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/admin/users";
    }

    // ── Admin cancel any booking ───────────────────────────────────────────────
    @GetMapping("/bookings/{id}/cancel")
    public String adminCancelBooking(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        Booking b = bookingService.cancelBooking(id);
        if (b != null) {
            ra.addFlashAttribute("success", "Booking " + b.getPnr() + " cancelled and refund initiated.");
        } else {
            ra.addFlashAttribute("error", "Booking could not be cancelled (may already be cancelled).");
        }
        return "redirect:/admin";
    }
}
