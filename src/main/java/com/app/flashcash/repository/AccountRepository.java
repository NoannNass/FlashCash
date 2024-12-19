package com.app.flashcash.repository;

import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Rechercher un compte par son numéro
    Optional<Account> findByAccountNumber(String accountNumber);

    // Rechercher un compte par IBAN
    Optional<Account> findByIban(String iban);

    // Rechercher tous les comptes d'un utilisateur
    List<Account> findByUser(User user);

    // Vérifier si un numéro de compte existe
    boolean existsByAccountNumber(String accountNumber);

    // Vérifier si un IBAN existe
    boolean existsByIban(String iban);
}