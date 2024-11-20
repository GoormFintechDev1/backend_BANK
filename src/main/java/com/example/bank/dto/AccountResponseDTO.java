package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private String cntrAccountNum;      // 계좌 번호
    private String accountHolderName;   // 계좌 소유자 이름
    private String bankCode;            // 은행 코드
    private String bankName;            // 은행 이름
    private Long balanceAmt;            // 계좌 잔액
    private String accountStatus;       // 계좌 상태
    private String lastTransactionTime; // 최종 거래 일시
}
