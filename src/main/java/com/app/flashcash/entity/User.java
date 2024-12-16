package com.app.flashcash.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.app.flashcash.entity.Transaction;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO; //Initialisation pour éviter null lors des calculs //BigDecimal pour des calculs plus précis -> BigDecimal mieux que double

    @ManyToMany
    @JoinTable(
            name = "connections", // table intermédiaire
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private Set<User> connections = new HashSet<>(); // HashSet pour éviter les doublons (si y a les mêmes noms etc

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true) // Cascade pour que les transactions soient mises à jour en fonction des utilisateurs // ophran pour supprimer les transactions non associé à un utilisateur
    private List<Transaction> sentTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> receivedTransactions = new ArrayList<>();

    //mis à jour du solde
    public void updateBalance(BigDecimal amount) {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        this.balance = this.balance.add(amount);
    }


}
