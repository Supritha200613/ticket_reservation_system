package com.otrs.ticket_reservation_system.controller;

import com.otrs.ticket_reservation_system.entity.User;
import com.otrs.ticket_reservation_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profilePage(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        user.setFullName(fullName.trim());
        user.setPhone(phone.trim());
        userService.updateUser(user);

        // Update session name immediately
        session.setAttribute("userName", fullName.trim());
        ra.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        if (!user.getPassword().equals(currentPassword)) {
            ra.addFlashAttribute("passwordError", "Current password is incorrect.");
            return "redirect:/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("passwordError", "New passwords do not match.");
            return "redirect:/profile";
        }
        if (newPassword.length() < 6) {
            ra.addFlashAttribute("passwordError", "New password must be at least 6 characters.");
            return "redirect:/profile";
        }

        user.setPassword(newPassword);
        userService.updateUser(user);
        ra.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/profile";
    }
}
