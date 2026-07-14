package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.Route;
import com.otrs.ticket_reservation_system.entity.enums.Role;
import com.otrs.ticket_reservation_system.service.RouteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/routes")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
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
    public String listRoutes(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        addNavAttrs(model, session);
        List<Route> routes;
        if (source != null && !source.isBlank() && destination != null && !destination.isBlank()) {
            routes = routeService.searchRoutes(source, destination);
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
        } else {
            routes = routeService.getAllActiveRoutes();
        }
        model.addAttribute("routes", routes);
        return "routes";
    }

    @GetMapping("/search")
    public String searchRoutes(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        addNavAttrs(model, session);
        List<Route> routes;
        if (source != null && !source.isBlank() && destination != null && !destination.isBlank()) {
            routes = routeService.searchRoutes(source, destination);
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
        } else {
            routes = routeService.getAllActiveRoutes();
        }
        model.addAttribute("routes", routes);
        return "routes";
    }

    @GetMapping("/add")
    public String addRoutePage(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        return "add-route";
    }

    @PostMapping("/add")
    public String addRoute(@ModelAttribute Route route, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        routeService.addRoute(route);
        ra.addFlashAttribute("success", "Route added successfully!");
        return "redirect:/routes";
    }

    @GetMapping("/{id}/edit")
    public String editRoutePage(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        Route route = routeService.getRouteById(id).orElse(null);
        if (route == null) return "redirect:/routes";
        addNavAttrs(model, session);
        model.addAttribute("route", route);
        return "edit-route";
    }

    @PostMapping("/{id}/edit")
    public String editRoute(@PathVariable Long id, @ModelAttribute Route route,
                            HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        Route existing = routeService.getRouteById(id).orElse(null);
        if (existing != null) {
            existing.setSource(route.getSource());
            existing.setDestination(route.getDestination());
            existing.setBaseFare(route.getBaseFare());
            existing.setDistanceKm(route.getDistanceKm());
            existing.setTransportType(route.getTransportType());
            routeService.updateRoute(existing);
            ra.addFlashAttribute("success", "Route updated successfully!");
        }
        return "redirect:/routes";
    }

    @GetMapping("/{id}/delete")
    public String deleteRoute(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        if (!isAdminOrAgent(session)) return "redirect:/dashboard";
        routeService.deleteRoute(id);
        ra.addFlashAttribute("success", "Route deleted.");
        return "redirect:/routes";
    }
}
