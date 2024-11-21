package com.example.bank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "withdrawal_history")
/////////////////////////// 출금 엔티티 (토스 페이먼츠 기반)

public class WithdrawalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdrawal_history_id")
    private Long id;

    // 계좌 ID
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // 결제 정보 (tosspayments)
    @Column(name = "m_id")
    private String mId;

    // 통화 (KRW)
    @Column(name = "currency")
    private String currency;

    // 결제 방식 (카드)
    @Column(name = "method")
    private String method;

    // 세금
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fee_id")
    private List<Withdrawal_fee> fees;

    // 카드
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "card_id")
    private Withdrawal_card card;

    // 승인일
    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    // 판매일
    @Column(name = "sold_date")
    private LocalDate soldDate;

    // 결제일
    @Column(name = "paid_out_date")
    private LocalDate paidOutDate;

}