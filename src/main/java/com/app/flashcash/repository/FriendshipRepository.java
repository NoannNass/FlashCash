package com.app.flashcash.repository;



import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Vérifier si une relation existe entre deux comptes
    boolean existsBySourceAccountAndFriendAccount(Account sourceAccount, Account friendAccount);

    // Trouver une relation d'amitié spécifique
    Optional<Friendship> findBySourceAccountIdAndFriendAccountId(Long sourceAccountId, Long friendAccountId);

    // Trouver tous les amis d'un compte source
    List<Friendship> findBySourceAccount(Account sourceAccount);

    // Trouver toutes les relations où un compte est ami
    List<Friendship> findByFriendAccount(Account friendAccount);
}