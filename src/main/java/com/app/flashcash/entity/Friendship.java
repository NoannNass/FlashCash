package com.app.flashcash.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = false) // Compte qui initie
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "friend_account_id", nullable = false) // Compte ami
    private Account friendAccount;

    @Column(nullable = false)
    private boolean active = true; // True si l'amitié est active


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
   private FriendshipStatus status = FriendshipStatus.PENDING;

    public enum FriendshipStatus {
        PENDING,    // En attente d'acceptation
        ACCEPTED,   // Amitié acceptée
        DECLINED,   // Amitié refusée
        BLOCKED     // Utilisateur bloqué
    }

}