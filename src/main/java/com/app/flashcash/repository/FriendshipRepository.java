package com.app.flashcash.repository;



import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    // Vérifie si une relation active existe entre deux comptes
    boolean existsBySourceAccountAndFriendAccountAndActiveIsTrue(Account sourceAccount, Account friendAccount);

    // Vérifier si une relation existe entre deux comptes
    boolean existsBySourceAccountAndFriendAccount(Account sourceAccount, Account friendAccount);

    // Trouver une relation d'amitié spécifique
    Optional<Friendship> findBySourceAccountIdAndFriendAccountId(Long sourceAccountId, Long friendAccountId);

    // Trouver tous les amis d'un compte source
    List<Friendship> findBySourceAccount(Account sourceAccount);

    // Trouver toutes les relations où un compte est ami
    List<Friendship> findByFriendAccount(Account friendAccount);

    //Trouver les demandes d'amis
    List<Friendship> findByFriendAccountAndStatus(Account friendAccount, Friendship.FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE f.sourceAccount.id = :sourceId " +
            "AND f.friendAccount.id = :friendId AND f.status = 'PENDING'")
    Optional<Friendship> findPendingFriendship(Long sourceId, Long friendId);

    /**
     * Recherche une amitié active entre deux comptes, dans les deux sens.

     * @param sourceId ID du premier compte
     * @param friendId ID du deuxième compte
     * @return Optional<Friendship> contenant l'amitié si elle existe et est active
     */
    @Query("SELECT f FROM Friendship f WHERE " +
            "((f.sourceAccount.id = :sourceId AND f.friendAccount.id = :friendId) OR " +
            "(f.sourceAccount.id = :friendId AND f.friendAccount.id = :sourceId)) " +
            "AND f.active = true AND f.status = 'ACCEPTED'")
    Optional<Friendship> findActiveFriendship(
            @Param("sourceId") Long sourceId,
            @Param("friendId") Long friendId);
}

