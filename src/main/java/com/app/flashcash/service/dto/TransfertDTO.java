package com.app.flashcash.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransfertDTO {

    // ID du compte source de l'utilisateur connecté
    private Long sourceAccountId;

    // ID du compte de l'ami (qui contient déjà toutes ses informations)
    private Long friendAccountId;

    // Montant à transférer
    private BigDecimal amount;
}
