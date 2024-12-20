package com.app.flashcash.controller;

import com.app.flashcash.entity.Transaction;
import com.app.flashcash.service.AccountService;
import com.app.flashcash.service.TransactionService;
import com.app.flashcash.service.UserService;
import com.app.flashcash.service.dto.DepositDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;
    private UserService userService;


    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
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


}
