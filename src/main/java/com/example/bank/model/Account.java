package com.example.bank.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    // 계좌 번호 (예: "110123456789")
    @Column(name = "account_num", length = 16, nullable = false)
    private String accountNum;

    // 계좌 소유자 이름 (예: "홍길동")
    @Column(name = "account_holder_name", length = 20, nullable = false)
    private String accountHolderName;

    // 은행 정보 (BankCode 테이블)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_code", referencedColumnName = "bank_code", nullable = false)
    private BankCode bankCode;

    // 계좌 잔액 (예: 1000000)
    @Column(name = "balance_amt")
    private BigDecimal balanceAmt;

    // 계좌 상태 (예: "ACTIVE", "SUSPENDED")
    @Column(name = "account_status", length = 10, nullable = false)
    private String accountStatus;

    // 최종 거래일시 (예: "20241120120000", 형식: yyyyMMddHHmmss)
    @Column(name = "last_transaction_time", length = 14)
    private String lastTransactionTime;

}
