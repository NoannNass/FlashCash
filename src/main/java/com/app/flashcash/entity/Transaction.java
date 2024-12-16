package com.app.flashcash.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_account_id", nullable = false)//Clé étrangère obligatoire
    private Account senderAccount; // compte émetteur

    @ManyToOne
    @JoinColumn(name = "receiver_account_id", nullable = false)//Clé étrangère obligatoire
    private Account receiverAccount; // compte receveur

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id") // la clé étrangère dans la table
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id") // Relation pour le destinataire
    private User receiver;

    @Column(precision = 10, scale = 2, nullable = false)//Clé étrangère obligatoire
    private BigDecimal amount;

    //frais associés à la transaction(ils seront à calculer de 0,5% du montant)
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fee = BigDecimal.ZERO; //BigDecimal.ZERO pour éviter des erreurs de calcul

    @Enumerated(EnumType.STRING)// persiste comme une chaine dans la base de donnée
    @Column(nullable = false)
    private TransactionType type;

public enum TransactionType {
    DEPOSIT, //Depot
    WITHDRAWAL, // Retrait
    TRANSFER // Transfert
}


//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private TransactionStatus status; // "TransactionStatus", cette énumération fait partie d'Hibernate et est généralement utilisée pour les transactions dans la gestion des transactions Hibernate.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionState state;

    public enum TransactionState {
        PENDING, //En attente
        COMPLETED,
        FAILED
    }
    @Enumerated(EnumType.STRING)
    private Currency currency;

    public enum Currency {
        Dollar, Euro, Pound, Yen
    }


    @CreationTimestamp //création automatique d'un horodatage
    private LocalDateTime createdAt;

    @UpdateTimestamp // met à jour date/heure à chaque maj
    private LocalDateTime updatedAt;


    //Méthode utilitaire pour calculer les frais : 0,5% du montant transféré uniquement
    @PrePersist // la méthode sera calculé avant l'insertion de l'entité(Tansaction) dans BDD
    @PreUpdate // indique la méthode doit être éxécutée avant que l'entité soit mise à jour dans la BDD
    public void calculateFee() {
        if (this.type == TransactionType.TRANSFER) { // prélève seulement si il s'agit d'une transaction
            this.fee = this.amount.multiply(new BigDecimal("0.05"));
        }else {
            this.fee = BigDecimal.ZERO; // pas de frais pour les dépot et retrait
        }
    }



}
