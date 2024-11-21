package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    // 계좌 번호
    private String accountNum;
    // 계좌 소유자 이름
    private String accountHolderName;
    // 은행 코드
    private String bankCode;
    // 은행 이름
    private String bankName;
    // 계좌 잔액
    private BigDecimal balanceAmt;
    // 계좌 상태
    private String accountStatus;
    // 최종 거래 일시
    private String lastTransactionTime;
}
