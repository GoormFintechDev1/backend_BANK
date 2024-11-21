package com.example.bank.controller;


import com.example.bank.dto.DepositRequestDTO;
import com.example.bank.dto.WithdrawalRequestDTO;
import com.example.bank.service.AccountHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/bank/accountHistory")
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryController {
    private final AccountHistoryService accountHistoryService;
    private final WebClient webClient;
    // 출금
    @PostMapping("/withdrawal")
    public ResponseEntity<String> withdraw(
            @RequestParam String accountNum

    ) {
        // 외부 API 호출
        String externalApiUrl = "http://localhost:8083/pos/toss";
        WithdrawalRequestDTO request = webClient.get()
                .uri(externalApiUrl)
                .retrieve()
                .bodyToMono(WithdrawalRequestDTO.class)
                .block();

        if (request == null) {
            throw new IllegalArgumentException("외부 API로부터 데이터를 받을 수 없습니다.");
        }

        // 출금 처리
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
