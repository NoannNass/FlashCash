package com.app.flashcash.service;

import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Transaction;
import com.app.flashcash.entity.User;
import com.app.flashcash.repository.AccountRepository;
import com.app.flashcash.repository.TransactionRepository;
import com.app.flashcash.repository.UserRepository;
import com.app.flashcash.service.dto.DepositDTO;
import com.app.flashcash.service.dto.TransfertDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void makeDeposit(DepositDTO depositDTO, String userEmail) {
        //Récupération et vérification du compte
        Account accout = accountRepository.findById(depositDTO.getAccountId()).orElseThrow(()->new RuntimeException("Compte non trouvé"));

        //Vérifie si le compte appartient au user connecté
        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new RuntimeException("Utilisateur non trouvé"));

        if(!accout.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Ce compte ne vous appartient pas");
        }

        //Création de la transaction
        Transaction deposit = Transaction.builder()
                .amount(depositDTO.getAmount())
                .type(Transaction.TransactionType.DEPOSIT)
                .state(Transaction.TransactionState.COMPLETED)
                .senderAccount(accout)
                .receiverAccount(accout)
                .sender(user)
                .receiver(user)
                .build();

        //mis à jour du solde de compte
        accout.updateBalance(depositDTO.getAmount());

        //sauvegarde des modifications
        transactionRepository.save(deposit);
        accountRepository.save(accout);
    }

    @Transactional // Assure que toute l'opération est atomique
    public void makeTransfer(TransfertDTO transferDTO, String senderEmail) {
        // Récupération et validation des comptes
        Account sourceAccount = accountRepository.findById(transferDTO.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Compte source introuvable"));

        Account destinationAccount = accountRepository.findById(transferDTO.getFriendAccountId())
                .orElseThrow(() -> new RuntimeException("Compte destinataire introuvable"));

        // Vérification du solde suffisant (montant + frais)
        BigDecimal fees = transferDTO.getAmount().multiply(new BigDecimal("0.05")); // 0.5% de frais
        BigDecimal totalDebit = transferDTO.getAmount().add(fees);

        if (sourceAccount.getBalance().compareTo(totalDebit) < 0) {
            throw new RuntimeException("Solde insuffisant pour effectuer le transfert");
        }

        // Création de la transaction
        Transaction transaction = Transaction.builder()
                .senderAccount(sourceAccount)
                .receiverAccount(destinationAccount)
                .sender(sourceAccount.getUser())
                .receiver(destinationAccount.getUser())
                .amount(transferDTO.getAmount())
                .type(Transaction.TransactionType.TRANSFER)
                .state(Transaction.TransactionState.COMPLETED)
                .fee(fees)
                .currency(Transaction.Currency.Euro) // Par défaut en euros
                .build();

        // Mise à jour des soldes
        sourceAccount.updateBalance(totalDebit.negate()); // Débit du montant + frais
        destinationAccount.updateBalance(transferDTO.getAmount()); // Crédit du montant sans les frais

        // Sauvegarde des modifications
        transactionRepository.save(transaction);
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }
}

