package com.app.flashcash.repository;


import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Transaction;
import com.app.flashcash.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Trouver les transactions où un compte est émetteur
    List<Transaction> findBySenderAccount(Account account);

    // Trouver les transactions où un compte est destinataire
    List<Transaction> findByReceiverAccount(Account account);

    // Trouver toutes les transactions d'un compte (envoyées ou reçues)
    List<Transaction> findBySenderAccountOrReceiverAccount(Account senderAccount, Account receiverAccount);

    // Version paginée des transactions d'un compte
    Page<Transaction> findBySenderAccountOrReceiverAccount(
            Account senderAccount,
            Account receiverAccount,
            Pageable pageable
    );

    // Rechercher les transactions par période
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Rechercher les transactions d'un utilisateur
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);

    // Trouver les transactions par type
    List<Transaction> findByType(Transaction.TransactionType type);

    // Trouver les transactions par état
    List<Transaction> findByState(Transaction.TransactionState state);
}