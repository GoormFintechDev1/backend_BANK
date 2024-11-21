package com.example.bank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "withdrawal_card")
public class Withdrawal_card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "cardType", nullable = false)
    private String cardType;

    @Column(name = "ownerType", nullable = false)
    private String ownerType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
