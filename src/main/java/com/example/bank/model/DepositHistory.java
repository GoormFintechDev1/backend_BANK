package com.example.bank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deposit_history")
/////////////////////////// 입금 엔티티

public class DepositHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_history_id")
    private Long id;

    // 계좌 ID
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


}
