package com.app.flashcash.controller;

import com.app.flashcash.entity.User;
import com.app.flashcash.service.UserService;
import com.app.flashcash.service.form.SignupForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        // Si le formulaire n'est pas déjà dans le modèle, en ajouter un nouveau
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String handleRegistration(@ModelAttribute SignupForm signupForm, RedirectAttributes redirectAttributes) {
        try{
            userService.registration(signupForm);

            // Si réussi, ajoute un message de succès
            redirectAttributes.addFlashAttribute("success",
                    "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            return "redirect:/signin";
        }catch (RuntimeException e) {
            // Ajouter le message d'erreur
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }



}
