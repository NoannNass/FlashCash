package com.app.flashcash.controller;

import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.User;
import com.app.flashcash.service.AccountService;
import com.app.flashcash.service.FriendshipService;
import com.app.flashcash.service.TransactionService;
import com.app.flashcash.service.UserService;
import com.app.flashcash.service.dto.DepositDTO;
import com.app.flashcash.service.dto.TransfertDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;
    private UserService userService;
    private FriendshipService friendshipService;
    private AccountService accountService;


    public TransactionController(TransactionService transactionService, UserService userService, FriendshipService friendshipService, AccountService accountService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.accountService = accountService;
    }


    @GetMapping("/deposit")
    public String showDepositForm(Model model, Principal principal) {
        //ajout du DTO vide pour le formulaire
        model.addAttribute("depositDTO",new DepositDTO());
        //affichage des comptes de l'utilisateur connecté
        model.addAttribute("user", userService.getUserByEmail(principal.getName()));
        return "/deposit";
    }

    //soumission du formulaire de dépot
    @PostMapping("/deposit")
    public String processDeposit(@ModelAttribute DepositDTO depositDTO, Principal principal) {
        try {
            // récupération de l'email utilisateur connecté via Principal
            transactionService.makeDeposit(depositDTO, principal.getName());
            return "redirect:/dashboard?success=deposit"; // succés
        }catch (RuntimeException e) {
            return "redirect:/transactions/deposit?error=" + e.getMessage();
        }
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model, Principal principal,@RequestParam(required = false) Long friendAccountId) {
        String userEmail = principal.getName();
        User currentUser = userService.getUserByEmail(userEmail);

        // On récupère tous les comptes de l'utilisateur connecté
        List<Account> userAccounts = currentUser.getAccounts();

        // On récupère la liste des comptes amis grâce au FriendshipService
        List<Account> friendAccounts = friendshipService.getFriendAccounts(userEmail);

        // Ajout des données au modèle
        model.addAttribute("user", currentUser);
        model.addAttribute("userAccounts", userAccounts);
        model.addAttribute("friendAccounts", friendAccounts); // Liste complète des comptes amis

        // Si un compte ami est présélectionné
        if (friendAccountId != null) {
            // On pré-remplit avec l'ID du compte ami
            model.addAttribute("selectedFriendAccountId", friendAccountId);
        }

        model.addAttribute("transferDTO", new TransfertDTO());

        return "/transfert";
    }

    @PostMapping("/transfer")
    public String processTransfer(@ModelAttribute TransfertDTO transferDTO,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Récupération de l'utilisateur connecté pour la vérification
            String userEmail = principal.getName();
            User currentUser = userService.getUserByEmail(userEmail);

            // Vérification que le compte source appartient bien à l'utilisateur connecté
            Account sourceAccount = accountService.getAccountById(transferDTO.getSourceAccountId());
            if (!sourceAccount.getUser().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Compte source non autorisé");
            }

            // Vérification que le compte destinataire est bien un ami
            Account friendAccount = accountService.getAccountById(transferDTO.getFriendAccountId());
            if (!friendshipService.areFriends(currentUser.getEmail(), friendAccount.getUser().getEmail())) {
                throw new RuntimeException("Transfert non autorisé - Utilisateur non ami");
            }

            // Exécution du transfert via le service
            transactionService.makeTransfer(transferDTO, userEmail);

            // Message de succès
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transfert de " + transferDTO.getAmount() + "€ effectué avec succès");

            return "redirect:/dashboard";

        } catch (RuntimeException e) {
            // En cas d'erreur, on redirige vers le formulaire avec un message d'erreur
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/transactions/transfert";
        }
    }



}
