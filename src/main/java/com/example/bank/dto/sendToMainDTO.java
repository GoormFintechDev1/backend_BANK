package com.example.bank.dto;

import com.example.bank.model.Account;
import com.example.bank.model.AccountHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class sendToMainDTO {
    private Account account; // 계좌 정보
    private List<AccountHistory> accountHistory; // 거래 내역 리스트
}
