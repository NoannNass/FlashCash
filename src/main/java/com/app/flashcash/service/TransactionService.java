package com.app.flashcash.service;

import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Transaction;
import com.app.flashcash.entity.User;
import com.app.flashcash.repository.AccountRepository;
import com.app.flashcash.repository.TransactionRepository;
import com.app.flashcash.repository.UserRepository;
import com.app.flashcash.service.dto.DepositDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}
