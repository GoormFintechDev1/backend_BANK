package com.example.bank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_history")
public class AccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_history_id")
    private Long accountHistoryId;

    // 거래일시 (예: "20241120120000", 형식: yyyyMMddHHmmss)
    @Column(name = "api_tran_dtm", length = 17, nullable = false)
    private String apiTranDtm;

    // 계좌 ID (참조용)
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // 거래일자 (예: "20241120", 형식: yyyyMMdd)
    @Column(name = "tran_date", length = 8, nullable = false)
    private String tranDate;

    // 거래시간 (예: "120000", 형식: HHmmss)
    @Column(name = "tran_time", length = 6, nullable = false)
    private String tranTime;

    // 입출금구분 ("입금" 또는 "출금")
    @Column(name = "inout_type", length = 10, nullable = false)
    private String inoutType;

    // 거래금액 (예: 450000)
    @Column(name = "tran_amt", nullable = false)
    private Long tranAmt;

    // 거래 후 잔액 (예: 550000)
    @Column(name = "after_balance_amt")
    private Long afterBalanceAmt;

    // 통장 인쇄 내용 (예: "온라인 구매")
    @Column(name = "print_content", length = 40)
    private String printContent;

    // 거래 상태 (예: "SUCCESS", "PENDING", "FAILED")
    @Column(name = "tran_status", length = 10, nullable = false)
    private String tranStatus;

    // 거래 구분 (예: "현금")
    @Column(name = "tran_type", length = 10)
    private String tranType;
}
