package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.enums.Role;
import com.otrs.ticket_reservation_system.service.BookingService;
import com.otrs.ticket_reservation_system.service.RouteService;
import com.otrs.ticket_reservation_system.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final RouteService routeService;
    private final ScheduleService scheduleService;
    private final BookingService bookingService;

    public DashboardController(RouteService routeService,
                               ScheduleService scheduleService,
                               BookingService bookingService) {
        this.routeService = routeService;
        this.scheduleService = scheduleService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";

        Long userId  = (Long) session.getAttribute("userId");
        Role role    = (Role) session.getAttribute("userRole");
        String name  = (String) session.getAttribute("userName");

        model.addAttribute("totalRoutes",    routeService.getAllActiveRoutes().size());
        model.addAttribute("totalSchedules", scheduleService.getActiveSchedules().size());
        model.addAttribute("userName",       name);
        model.addAttribute("userRole",       role != null ? role.name() : "");
        model.addAttribute("userRoleString",  role != null ? role.name() : "");

        // Admins and agents see total bookings; customers see only their own count
        if (role == Role.ROLE_ADMIN || role == Role.ROLE_AGENT) {
            model.addAttribute("myBookings", bookingService.getAllBookings().size());
        } else {
            model.addAttribute("myBookings", bookingService.getUserBookings(userId).size());
        }
        return "dashboard";
    }
}
