package com.app.flashcash.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
   private String accountNumber;


    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO; // Solde intitial par défaut

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) //association du compte à un utilsateur propriétaire
    private User user;

    @OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> sentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "receiverAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> receivedTransactions = new ArrayList<>();

    // Méthode utilitaire pour mettre à jour le solde
    public void updateBalance(BigDecimal amount) {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO; // Éviter les NullPointerException
        }
        this.balance = this.balance.add(amount);
    }



}
