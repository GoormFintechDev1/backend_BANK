package com.example.bank.dto;

import com.example.bank.model.Withdrawal_card;
import com.example.bank.model.Withdrawal_fee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestDTO {
    // 결제 정보 (tosspayments)
    private String mId;
    // 통화 (KRW)
    private String currency;
    // 결제 방식 (카드)
    private String method;
    // 세금 정보
    private List<Withdrawal_fee> fees;
    // 카드 정보
    private Withdrawal_card card;
    // 승인일
    private LocalDateTime approvedAt;
    // 판매일
    private LocalDate soldDate;
    // 결제일
    private LocalDate paidOutDate;

}