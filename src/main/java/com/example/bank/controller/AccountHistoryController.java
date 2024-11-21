package com.example.bank.controller;


import com.example.bank.dto.DepositRequestDTO;
import com.example.bank.dto.WithdrawalRequestDTO;
import com.example.bank.service.AccountHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank/accountHistory")
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryController {
    private final AccountHistoryService accountHistoryService;

    // 출금
    @PostMapping("/withdrawal")
    public ResponseEntity<String> withdraw(
            @RequestBody WithdrawalRequestDTO request,
            @RequestParam String accountNum
            ) {
        accountHistoryService.withdraw(request, accountNum);
        return ResponseEntity.ok("출금 성공");
    }

    // 입금
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @RequestBody DepositRequestDTO request,
            @RequestParam String accountNum
    ) {
       accountHistoryService.deposit(request,accountNum);
        return ResponseEntity.ok("입금 성공");
    }
}
