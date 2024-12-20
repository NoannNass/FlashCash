package com.app.flashcash.service;


import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Transaction;
import com.app.flashcash.repository.AccountRepository;
import com.app.flashcash.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    /**
     * Initialise le solde d'un compte avec un montant donné
     *
     * @param iban L'IBAN du compte à initialiser
     * @param initialAmount Le montant initial à placer sur le compte
     * @return Le compte mis à jour
     */
    @Transactional
    public Account initializeAccountBalance(String iban, BigDecimal initialAmount) {
        // Recherche du compte
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new IllegalArgumentException("Compte non trouvé : " + iban));

        // Définition du solde initial
        account.setBalance(initialAmount);

        // Sauvegarde et retour du compte mis à jour
        return accountRepository.save(account);
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte introuvable avec l'ID : " + accountId));
    }






//    public void makeTransfer(Account sourceAccount, Account destinationAccount, BigDecimal amount) {
//
//        // Vérifications
//        if (sourceAccount.getBalance().compareTo(amount) < 0) {
//            throw new RuntimeException("Solde insuffisant");
//        }
//
//        // Création de la transaction
//        Transaction transaction = Transaction.builder()
//                .senderAccount(sourceAccount)
//                .receiverAccount(destinationAccount)
//                .amount(amount)
//                .type(Transaction.TransactionType.TRANSFER)
//                .state(Transaction.TransactionState.COMPLETED)
//                .build();
//
//        // Mise à jour des soldes
//        sourceAccount.updateBalance(amount.negate());
//        destinationAccount.updateBalance(amount);
//
//        // Sauvegarde
//        transactionRepository.save(transaction);
//        accountRepository.save(sourceAccount);
//        accountRepository.save(destinationAccount);
//
//    }




}
