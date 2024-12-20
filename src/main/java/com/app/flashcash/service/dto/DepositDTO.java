package com.app.flashcash.service.dto;

import lombok.Data;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;


@Data
public class DepositDTO {


    private BigDecimal amount;
    private Long accountId;

}
