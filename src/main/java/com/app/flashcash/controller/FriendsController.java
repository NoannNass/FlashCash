package com.app.flashcash.controller;

import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Friendship;
import com.app.flashcash.entity.User;
import com.app.flashcash.service.FriendshipService;
import com.app.flashcash.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(path = "/friends")
public class FriendsController {

    private final FriendshipService friendshipService;
    private final UserService userService;
    public FriendsController(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @GetMapping
    public String showFriendsPage(Model model, Principal principal) {

        String userEmail = principal.getName();
        User user = userService.getUserByEmail(userEmail);

        List<Account> friendAccounts = friendshipService.getFriendAccounts(userEmail);
        List<Friendship> pendingRequests = friendshipService.getPendingFriendRequests(userEmail);

        model.addAttribute("user", user);
        model.addAttribute("friendAccounts", friendAccounts);
        model.addAttribute("pendingRequests", pendingRequests);


        return "/user/friends";
    }

    @PostMapping("/request")
    public String sendFriendRequest(
            @RequestParam String friendEmail,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            friendshipService.sendFriendRequest(principal.getName(), friendEmail);
            redirectAttributes.addFlashAttribute("success",
                    "Demande d'ami envoyée avec succès ! En attente d'acceptation.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Une erreur inattendue s'est produite. Veuillez réessayer.");
        }

        return "redirect:/friends";
    }

    @PostMapping("/respond")
    public String respondToFriendRequest(
            @RequestParam String friendIban,
            @RequestParam boolean accept,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            friendshipService.respondToFriendRequest(principal.getName(), friendIban, accept);
            String message = accept ?
                    "Demande d'ami acceptée ! Vous pouvez maintenant effectuer des transferts." :
                    "Demande d'ami refusée.";
            redirectAttributes.addFlashAttribute("success", message);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Une erreur s'est produite lors du traitement de la demande.");
        }

        return "redirect:/friends";
    }

    @PostMapping("/remove")
    public String removeFriend(
            @RequestParam String friendIban,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            friendshipService.removeFriend(principal.getName(), friendIban);
            redirectAttributes.addFlashAttribute("success", "Ami supprimé avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Une erreur s'est produite lors de la suppression de l'ami.");
        }

        return "redirect:/friends";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "Une erreur inattendue s'est produite. Veuillez réessayer plus tard.");
        return "redirect:/friends";
    }
}