package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.Route;
import com.otrs.ticket_reservation_system.entity.Schedule;
import com.otrs.ticket_reservation_system.entity.enums.Role;
import com.otrs.ticket_reservation_system.service.RouteService;
import com.otrs.ticket_reservation_system.service.ScheduleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final RouteService routeService;

    public ScheduleController(ScheduleService scheduleService, RouteService routeService) {
        this.scheduleService = scheduleService;
        this.routeService    = routeService;
    }

    private boolean isAdminOrAgent(HttpSession session) {
        Role role = (Role) session.getAttribute("userRole");
        return role == Role.ROLE_ADMIN || role == Role.ROLE_AGENT;
    }

    private void addNavAttrs(Model model, HttpSession session) {
        Object role = session.getAttribute("userRole");
        model.addAttribute("userRole",  role != null ? role.toString() : "");
        model.addAttribute("userName",  session.getAttribute("userName"));
        model.addAttribute("canManage", isAdminOrAgent(session));
    }

    @GetMapping
    public String listSchedules(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        addNavAttrs(model, session);
        List<Schedule> schedules;
        if (source != null && !source.isBlank() && destination != null && !destination.isBlank()) {
            schedules = scheduleService.searchSchedules(source, destination);
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
        } else {
            schedules = scheduleService.getActiveSchedules();
        }
        model.addAttribute("schedules", schedules);
        return "schedules";
    }

    @GetMapping("/search")
    public String searchSchedules(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        addNavAttrs(model, session);
        List<Schedule> schedules;
        if (source != null && !source.isBlank() && destination != null && !destination.isBlank()) {
            schedules = scheduleService.searchSchedules(source, destination);
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
        } else {
            schedules = scheduleService.getActiveSchedules();
        }
        model.addAttribute("schedules", schedules);
        return "schedules";
    }

    @GetMapping("/add")
    public String addSchedulePage(Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        addNavAttrs(model, session);
        model.addAttribute("routes", routeService.getAllActiveRoutes());
        return "add-schedule";
    }

    @PostMapping("/add")
    public String addSchedule(@ModelAttribute Schedule schedule,
                              @RequestParam Long routeId,
                              HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        Route route = routeService.getRouteById(routeId).orElse(null);
        if (route != null) {
            schedule.setRoute(route);
            schedule.setAvailableSeats(schedule.getTotalSeats());
            scheduleService.addSchedule(schedule);
            ra.addFlashAttribute("success", "Schedule added successfully!");
        }
        return "redirect:/schedules";
    }

    @GetMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        scheduleService.deleteSchedule(id);
        ra.addFlashAttribute("success", "Schedule deleted.");
        return "redirect:/schedules";
    }
}
