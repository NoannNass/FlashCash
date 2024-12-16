package com.app.flashcash.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
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
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fee;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER
}

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
