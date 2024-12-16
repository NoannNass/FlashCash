//package com.app.flashcash.service;
//
//import com.app.flashcash.entity.Account;
//import com.app.flashcash.entity.Friendship;
//import com.app.flashcash.repository.AccountRepository;
//import com.app.flashcash.repository.FriendshipRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class FriendshipService {
//
//    @Autowired
//    private FriendshipRepository friendshipRepository;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    public void addFriend(Long accountId, Long friendAccountId) {
//        Account sourceAccount = accountRepository.findById(accountId)
//                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable : " + accountId));
//        Account friendAccount = accountRepository.findById(friendAccountId)
//                .orElseThrow(() -> new IllegalArgumentException("Compte ami introuvable : " + friendAccountId));
//
//        // Validation : empêcher les doublons
//        boolean alreadyFriends = friendshipRepository.existsBySourceAccountAndFriendAccount(sourceAccount, friendAccount);
//        if (alreadyFriends) {
//            throw new IllegalStateException("Les comptes sont déjà amis !");
//        }
//
//        // Créer une relation d'amitié
//        Friendship friendship = Friendship.builder()
//                .sourceAccount(sourceAccount)
//                .friendAccount(friendAccount)
//                .active(true)
//                .build();
//
//        friendshipRepository.save(friendship);
//    }
//
//    public void removeFriend(Long accountId, Long friendAccountId) {
//        Friendship friendship = friendshipRepository.findBySourceAccountIdAndFriendAccountId(accountId, friendAccountId)
//                .orElseThrow(() -> new IllegalArgumentException("Amitié introuvable"));
//        friendshipRepository.delete(friendship);
//    }
//
//    //Lister les amis d'un compte
//    public List<Account> getFriends(Long accountId) {
//        Account sourceAccount = accountRepository.findById(accountId)
//                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable"));
//        List<Friendship> friendships = friendshipRepository.findBySourceAccount(sourceAccount);
//        return friendships.stream()
//                .map(Friendship::getFriendAccount) // Extraire les comptes amis
//                .toList();
//    }
//
//}
