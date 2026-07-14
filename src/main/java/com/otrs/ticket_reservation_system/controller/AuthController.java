package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.User;
import com.otrs.ticket_reservation_system.entity.enums.Role;
import com.otrs.ticket_reservation_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        Optional<User> user = userService.login(email, password);
        if (user.isPresent()) {
            session.setAttribute("userId",   user.get().getId());
            session.setAttribute("userName", user.get().getFullName());
            // Store role as BOTH enum (for controller checks) and String (for Thymeleaf)
            session.setAttribute("userRole",       user.get().getRole());
            session.setAttribute("userRoleString", user.get().getRole().name());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password. Please try again.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("userId") != null) return "redirect:/dashboard";
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            // Default unrecognised role to CUSTOMER for safety
            if (user.getRole() == null) {
                user.setRole(Role.ROLE_CUSTOMER);
            }
            // Prevent self-registration as ADMIN
            if (user.getRole() == Role.ROLE_ADMIN) {
                user.setRole(Role.ROLE_CUSTOMER);
            }
            userService.register(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
