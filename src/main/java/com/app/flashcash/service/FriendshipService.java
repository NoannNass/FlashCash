package com.app.flashcash.service;

import com.app.flashcash.entity.Account;
import com.app.flashcash.entity.Friendship;
import com.app.flashcash.entity.User;
import com.app.flashcash.repository.AccountRepository;
import com.app.flashcash.repository.FriendshipRepository;
import com.app.flashcash.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendshipService {

    // Déclaration des repositories nécessaires pour accéder aux données
    private final FriendshipRepository friendshipRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    // Constructeur avec injection des dépendances
    public FriendshipService(FriendshipRepository friendshipRepository,
                             AccountRepository accountRepository,
                             UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /**
     * Envoie une demande d'amitié à un autre utilisateur
     * @param currentUserEmail email de l'utilisateur qui envoie la demande
     * @param friendEmail email de l'ami potentiel

     * @return l'entité Friendship créée
     */
    @Transactional
    public Friendship sendFriendRequest(String currentUserEmail, String friendEmail) {
        // Récupération de l'utilisateur qui envoie la demande
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur demandeur introuvable"));

        // Récupération du compte principal de l'utilisateur courant
        Account sourceAccount = currentUser.getAccounts().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("L'utilisateur n'a pas de compte"));

        // Récupération de l'utilisateur ami et de son compte principal
        User friendUser = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur ami introuvable"));

        Account friendAccount = friendUser.getAccounts().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("L'utilisateur ami n'a pas de compte"));

        // Empêcher d'ajouter son propre compte
        if (currentUser.getId().equals(friendUser.getId())) {
            throw new IllegalArgumentException("Impossible d'envoyer une demande d'ami à soi-même");
        }

        // Vérifier si une demande existe déjà
        if (friendshipRepository.existsBySourceAccountAndFriendAccount(sourceAccount, friendAccount)) {
            throw new IllegalStateException("Une demande d'ami existe déjà avec cet utilisateur");
        }

        // Création de la demande d'amitié
        Friendship friendship = Friendship.builder()
                .sourceAccount(sourceAccount)
                .friendAccount(friendAccount)
                .status(Friendship.FriendshipStatus.PENDING)
                .active(false)  // Sera activé une fois accepté
                .build();

        return friendshipRepository.save(friendship);
    }

    /**
     * Permet de répondre à une demande d'amitié
     * @param userEmail email de l'utilisateur qui répond
     * @param friendIban IBAN de l'utilisateur qui a envoyé la demande
     * @param accept true pour accepter, false pour refuser
     * @return l'entité Friendship mise à jour
     */
    @Transactional
    public Friendship respondToFriendRequest(String userEmail, String friendIban, boolean accept) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur répondant introuvable"));

        Account userAccount = user.getAccounts().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("L'utilisateur n'a pas de compte"));

        Account friendAccount = accountRepository.findByIban(friendIban)
                .orElseThrow(() -> new IllegalArgumentException("Compte demandeur introuvable"));

        // Recherche de la demande d'amitié en attente
        Friendship friendship = friendshipRepository
                .findPendingFriendship(friendAccount.getId(), userAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("Aucune demande d'amitié en attente"));

        // Mise à jour du statut et de l'état actif selon la réponse
        if (accept) {
            friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
            friendship.setActive(true);
        } else {
            friendship.setStatus(Friendship.FriendshipStatus.DECLINED);
            friendship.setActive(false);
        }

        return friendshipRepository.save(friendship);
    }

    /**
     * Récupère toutes les demandes d'amitié en attente pour un utilisateur
     * @param userEmail email de l'utilisateur
     * @return liste des demandes d'amitié en attente
     */
    public List<Friendship> getPendingFriendRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Account userAccount = user.getAccounts().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("L'utilisateur n'a pas de compte"));

        return friendshipRepository.findByFriendAccountAndStatus(
                userAccount, Friendship.FriendshipStatus.PENDING);
    }

    /**
     * Récupère la liste des amis actifs d'un utilisateur
     * @param userEmail email de l'utilisateur
     * @return liste des comptes amis
     */
    public List<Account> getFriendAccounts(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Account userAccount = user.getAccounts().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("L'utilisateur n'a pas de compte"));

        // Récupère les amitiés où l'utilisateur est source
        List<Friendship> directFriendships = friendshipRepository
                .findBySourceAccount(userAccount).stream()
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.ACCEPTED && f.isActive())
                .collect(Collectors.toList());

        // Récupère les amitiés où l'utilisateur est destinataire
        List<Friendship> inverseFriendships = friendshipRepository
                .findByFriendAccount(userAccount).stream()
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.ACCEPTED && f.isActive())
                .collect(Collectors.toList());

        // Combine et extrait les comptes amis
        List<Account> allFriends = new ArrayList<>();
        allFriends.addAll(directFriendships.stream()
                .map(Friendship::getFriendAccount)
                .collect(Collectors.toList()));
        allFriends.addAll(inverseFriendships.stream()
                .map(Friendship::getSourceAccount)
                .collect(Collectors.toList()));

        return allFriends;
    }

    /**
     * Supprime une relation d'amitié
     * @param currentUserEmail email de l'utilisateur qui supprime
     * @param friendIban IBAN de l'ami à supprimer
     */
    @Transactional
    public void removeFriend(String currentUserEmail, String friendIban) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Account sourceAccount = currentUser.getAccounts().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("L'utilisateur n'a pas de compte"));

        Account friendAccount = accountRepository.findByIban(friendIban)
                .orElseThrow(() -> new IllegalArgumentException("Compte ami introuvable"));

        // Recherche de l'amitié dans les deux sens
        Friendship friendship = friendshipRepository
                .findActiveFriendship(sourceAccount.getId(), friendAccount.getId())
                .orElseThrow(() -> new IllegalArgumentException("Relation d'amitié introuvable"));

        friendship.setActive(false);
        friendship.setStatus(Friendship.FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);
    }
}