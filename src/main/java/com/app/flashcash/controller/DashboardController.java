package com.app.flashcash.controller;

import com.app.flashcash.entity.User;
import com.app.flashcash.service.AccountService;
import com.app.flashcash.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final AccountService accountService; // Nous aurons besoin de le créer

    public DashboardController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping
    public String showDashboard(Model model, Principal principal) {
        // Principal contient l'utilisateur connecté
        String userEmail = principal.getName();
        User user = userService.getUserByEmail(userEmail);

        model.addAttribute("user", user);
        model.addAttribute("accounts", user.getAccounts());

        return "/user/dashboard";
    }
}