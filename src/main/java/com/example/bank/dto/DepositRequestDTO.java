package com.example.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestDTO {

    // 계좌 번호 (예: "110123456789")
    private String accountNumber;

    // 출금 금액 (예: 450000)
    private Long amount;

    // 거래 일시 (예: "20241120120000", 형식: yyyyMMddHHmmss)
    private String apiTranDtm;

    // 거래 구분 (예: "현금")
    private String tranType;

    // 통장 인쇄 내용 (예: "온라인 구매")
    private String printContent;
}
